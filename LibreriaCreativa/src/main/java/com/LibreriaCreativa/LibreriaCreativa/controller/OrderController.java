package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.model.Order;
import com.LibreriaCreativa.LibreriaCreativa.model.OrderDetail;
import com.LibreriaCreativa.LibreriaCreativa.model.OrderStatus;
import com.LibreriaCreativa.LibreriaCreativa.model.Payment;
import com.LibreriaCreativa.LibreriaCreativa.model.PedidoRequest;
import com.LibreriaCreativa.LibreriaCreativa.model.Product;
import com.LibreriaCreativa.LibreriaCreativa.model.User;
import com.LibreriaCreativa.LibreriaCreativa.service.CloudinaryService;
import com.LibreriaCreativa.LibreriaCreativa.service.OrderDetailService;
import com.LibreriaCreativa.LibreriaCreativa.service.OrderService;
import com.LibreriaCreativa.LibreriaCreativa.service.OrderStatusService;
import com.LibreriaCreativa.LibreriaCreativa.service.PaymentService;
import com.LibreriaCreativa.LibreriaCreativa.service.ProductService;
import com.LibreriaCreativa.LibreriaCreativa.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/admin")
    public Page<Order> obtenerCategoriasAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.obtenerOrdenesPaginados(page, size);
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> guardarPedido(
            @RequestPart("pedido") PedidoRequest pedidoRequest,
            @RequestPart(value = "comprobante", required = false) MultipartFile comprobante
    ) {
        try {
            // 1. Usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> optionalUser = userService.findByEmail(auth.getName());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            User user = optionalUser.get();

            // 2. Crear pedido
            Order pedido = new Order();
            pedido.setUser(user);
            pedido.setSubtotal(pedidoRequest.getSubtotal());
            pedido.setEnvio(pedidoRequest.getEnvio());
            pedido.setDescuento(pedidoRequest.getDescuento());
            pedido.setTotal(pedidoRequest.getTotal());
            pedido.setDireccion(pedidoRequest.getDireccion());

            // 3. Estado por tipo de pago
            String tipoPago = pedidoRequest.getTipoPago();
            int estadoId = "tarjeta".equalsIgnoreCase(tipoPago) ? 6 : 1;

            OrderStatus estado = orderStatusService.findById(estadoId)
                    .orElseThrow(() -> new RuntimeException("Estado de pedido no encontrado"));
            pedido.setEstado(estado);

            // 4. Guardar pedido inicial
            orderService.guardar(pedido);

            // 5. Guardar detalles
            for (PedidoRequest.ProductoItem item : pedidoRequest.getProductos()) {
                Product producto = productService.buscarPorId(item.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto con ID " + item.getIdProducto() + " no encontrado"));

                if (producto.getStock() < item.getCantidad()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Stock insuficiente para el producto " + producto.getName()));
                }

                producto.setStock(producto.getStock() - item.getCantidad());
                productService.guardarProducto(producto);

                OrderDetail detalle = new OrderDetail();
                detalle.setPedido(pedido);
                detalle.setProducto(producto);
                detalle.setCantidad(item.getCantidad());
                detalle.setSubtotal(item.getSubtotal());
                orderDetailService.guardar(detalle);
            }

            // 6. Crear pago
            Payment pago = new Payment();
            pago.setOrder(pedido);
            pago.setTipo(tipoPago);

            if ("yape".equalsIgnoreCase(tipoPago)) {
                if (comprobante != null && !comprobante.isEmpty()) {
                    String urlImagen = cloudinaryService.uploadFile(comprobante, "comprobantes-yape");
                    pago.setImgYape(urlImagen);
                } else {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Debe adjuntar la imagen del comprobante para pago con Yape."));
                }
            }

            paymentService.guardar(pago);

            // 7. Respuesta exitosa
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido registrado correctamente",
                    "pedidoId", pedido.getId()
            ));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al subir la imagen del comprobante."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado en el servidor"));
        }
    }

    @PutMapping("/cancelarUser/{id}")
    public ResponseEntity<?> cancelarPedido(@PathVariable Integer id) {
        try {
            // 1. Usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> optionalUser = userService.findByEmail(auth.getName());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            User user = optionalUser.get();

            // 2. Obtener pedido
            Optional<Order> optionalPedido = orderService.buscarPorId(id);
            if (optionalPedido.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pedido no encontrado"));
            }

            Order pedido = optionalPedido.get();

            // 3. Verificar que el pedido pertenece al usuario autenticado
            if (!pedido.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permiso para cancelar este pedido"));
            }

            // 4. Verificar estado actual
            if (pedido.getEstado() == null || pedido.getEstado().getId() != 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Solo se pueden cancelar pedidos en estado Pendiente"));
            }

            // 5. Asignar estado 5 (Cancelado)
            OrderStatus estadoCancelado = orderStatusService.findById(5)
                    .orElseThrow(() -> new RuntimeException("Estado de Cancelado no encontrado"));
            pedido.setEstado(estadoCancelado);

            // 6. Guardar
            orderService.guardar(pedido);

            // 7. Respuesta exitosa
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido cancelado exitosamente",
                    "pedidoId", pedido.getId()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cancelar el pedido"));
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam Integer nuevoEstadoId
    ) {
        try {
            // 1. Validar estado de destino
            if (nuevoEstadoId < 1 || nuevoEstadoId > 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "Estado destino inválido"));
            }

            // 2. Obtener pedido
            Optional<Order> optionalPedido = orderService.buscarPorId(id);
            if (optionalPedido.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pedido no encontrado"));
            }

            Order pedido = optionalPedido.get();
            int estadoActualId = pedido.getEstado() != null ? pedido.getEstado().getId() : 0;

            // 3. Reglas de transición prohibidas
            // a) No volver a 1 o 6 si ya está en 2,3 o 4
            if ((estadoActualId == 2 || estadoActualId == 3 || estadoActualId == 4)
                    && (nuevoEstadoId == 1 || nuevoEstadoId == 6)) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "No se puede volver a Pendiente o Pagado desde Aprobado, Enviado o Entregado")
                );
            }

            // b) No se puede cancelar si está Enviado o Entregado
            if ((estadoActualId == 3 || estadoActualId == 4) && nuevoEstadoId == 5) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "No se puede cancelar un pedido que ya fue Enviado o Entregado")
                );
            }

            // 4. Cargar entidad OrderStatus destino
            OrderStatus nuevoEstado = orderStatusService.findById(nuevoEstadoId)
                    .orElseThrow(() -> new RuntimeException("Estado de pedido no encontrado"));

            // 5. Generar boleta si es Aprobado, Enviado o Entregado y no tiene aún
            if ((nuevoEstadoId == 2 || nuevoEstadoId == 3 || nuevoEstadoId == 4) && pedido.getRutaBoleta() == null) {
                // a) Obtener detalles
                List<OrderDetail> detalles = orderDetailService.buscarPorPedido(pedido);

                // b) Generar PDF
                byte[] pdfBytes = orderService.generarBoletaPDF(pedido, detalles);

                // c) Subir a Cloudinary
                String carpeta = "boletas";
                String fileName = "boleta_" + pedido.getId() + ".pdf";
                String urlPdf = cloudinaryService.uploadPdfBytes(pdfBytes, carpeta, fileName);

                // d) Guardar ruta en pedido
                pedido.setRutaBoleta(urlPdf);
            }

            // 6. Actualizar estado
            pedido.setEstado(nuevoEstado);

            // 7. Guardar
            orderService.guardar(pedido);

            // 8. Respuesta exitosa
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Estado actualizado exitosamente",
                    "pedidoId", pedido.getId(),
                    "nuevoEstado", nuevoEstado.getName()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el estado del pedido"));
        }
    }


    /*
    @PostMapping("/save")
    public ResponseEntity<?> guardar(@RequestBody PedidoRequest pedidoRequest) {
        try {
            // 1. Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> optionalUser = userService.findByEmail(auth.getName());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            User user = optionalUser.get();

            // 2. Crear y guardar el pedido
            Order pedido = new Order();
            pedido.setUser(user);
            pedido.setSubtotal(pedidoRequest.getSubtotal());
            pedido.setEnvio(pedidoRequest.getEnvio());
            pedido.setTotal(pedidoRequest.getTotal());

            OrderStatus status = orderStatusService.findById(4)
                    .orElseThrow(() -> new RuntimeException("Estado de pedido no encontrado"));

            pedido.setEstado(status);
            orderService.guardar(pedido);

            // 3. Guardar los detalles del pedido
            for (PedidoRequest.ProductoItem item : pedidoRequest.getProductos()) {
                Product producto = productService.buscarPorId(item.idProducto)
                        .orElseThrow(() -> new RuntimeException("Producto con ID " + item.idProducto + " no encontrado"));

                if (producto.getStock() < item.cantidad) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Stock insuficiente para el producto " + producto.getName()));
                }

                producto.setStock(producto.getStock() - item.cantidad);
                productService.guardarProducto(producto);

                OrderDetail detalle = new OrderDetail();
                detalle.setPedido(pedido);
                detalle.setProducto(producto);
                detalle.setCantidad(item.cantidad);
                detalle.setSubtotal(item.subtotal);

                orderDetailService.guardar(detalle);
            }

            // 4. Generar PDF (boleta)
            List<OrderDetail> detalles = orderDetailService.buscarPorPedido(pedido);
            byte[] pdfBytes = orderService.generarBoletaPDF(pedido, detalles);

            // 5. Subir a Cloudinary
            String carpeta = "boletas";
            String fileName = "boleta_" + pedido.getId() + ".pdf";
            String urlPdf = cloudinaryService.uploadPdfBytes(pdfBytes, carpeta, fileName);

            // 6. Guardar URL
            pedido.setRutaBoleta(urlPdf);
            orderService.guardar(pedido);

            // 7. Respuesta exitosa
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido registrado correctamente",
                    "boletaUrl", urlPdf
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado en el servidor"));
        }
    }*/
    @GetMapping("/mine")
    public ResponseEntity<?> obtenerPedidosDelUsuario(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = userService.findByEmail(auth.getName());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado"));
        }

        User user = optionalUser.get();
        Page<Order> pedidos = orderService.obtenerOrdenesDeUsuarioPaginadas(user, page, size);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> obtenerDetallePedido(@RequestParam Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = userService.findByEmail(auth.getName());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado"));
        }

        Optional<Order> optionalPedido = orderService.buscarPorId(id);

        if (optionalPedido.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Pedido no encontrado"));
        }

        Order pedido = optionalPedido.get();

        // Validar que el pedido pertenece al usuario autenticado
        if (!pedido.getUser().getId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No autorizado para ver este pedido"));
        }

        return ResponseEntity.ok(pedido);
    }

}
