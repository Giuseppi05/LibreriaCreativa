package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Order;
import com.LibreriaCreativa.LibreriaCreativa.model.OrderDetail;
import com.LibreriaCreativa.LibreriaCreativa.model.Product;
import com.LibreriaCreativa.LibreriaCreativa.model.User;
import com.LibreriaCreativa.LibreriaCreativa.repository.OrderRepository;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    public Order guardar(Order order) {
        return orderRepo.save(order);
    }

    public Optional<Order> buscarPorId(Integer id) {
        return orderRepo.findById(id);
    }

    public Page<Order> obtenerOrdenesPaginados(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Order> obtenerOrdenesDeUsuarioPaginadas(User user, int page, int size) {
        return orderRepo.findByUserId(user.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public byte[] generarBoletaPDF(Order pedido, List<OrderDetail> detalles) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 60, 60);

        try {
            PdfWriter writer = PdfWriter.getInstance(documento, baos);
            documento.open();

            // Colores de la empresa
            Color colorPrincipal = new Color(69, 129, 142);    // #45818e
            Color colorSecundario = new Color(255, 153, 0);    // #ff9900
            Color colorTerciario = new Color(208, 225, 228);   // #d0e1e4
            Color colorTexto = new Color(19, 81, 115);         // #135173

            // ==================== ENCABEZADO ====================
            // Logo y nombre de la empresa
            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{3, 1});

            // Lado izquierdo - Información de la empresa
            PdfPCell celdaEmpresa = new PdfPCell();
            celdaEmpresa.setBorder(Rectangle.NO_BORDER);
            celdaEmpresa.setPaddingBottom(10);

            Font fuenteEmpresa = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, colorPrincipal);
            Font fuenteSlogan = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, colorTexto);
            Font fuenteContacto = FontFactory.getFont(FontFactory.HELVETICA, 10, colorTexto);

            Paragraph nombreEmpresa = new Paragraph("LIBRERÍA CREATIVA", fuenteEmpresa);
            nombreEmpresa.setAlignment(Element.ALIGN_LEFT);
            celdaEmpresa.addElement(nombreEmpresa);

            Paragraph slogan = new Paragraph("Donde la imaginación cobra vida", fuenteSlogan);
            slogan.setAlignment(Element.ALIGN_LEFT);
            slogan.setSpacingAfter(5);
            celdaEmpresa.addElement(slogan);

            Paragraph contacto = new Paragraph("📍 Av. Principal 123, Lima | 📞 123 456 789 | 📧 info@libreriacreativa.com", fuenteContacto);
            contacto.setAlignment(Element.ALIGN_LEFT);
            celdaEmpresa.addElement(contacto);

            encabezado.addCell(celdaEmpresa);

            // Lado derecho - Número de boleta
            PdfPCell celdaBoleta = new PdfPCell();
            celdaBoleta.setBorder(Rectangle.NO_BORDER);
            celdaBoleta.setPadding(10);
            celdaBoleta.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaBoleta.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Font fuenteBoleta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, colorPrincipal);
            Font fuenteNumero = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, colorSecundario);

            Paragraph tituloBoleta = new Paragraph("BOLETA DE VENTA", fuenteBoleta);
            tituloBoleta.setAlignment(Element.ALIGN_CENTER);
            tituloBoleta.setSpacingAfter(5);
            celdaBoleta.addElement(tituloBoleta);

            Paragraph numeroBoleta = new Paragraph("N° " + String.format("%06d", pedido.getId()), fuenteNumero);
            numeroBoleta.setAlignment(Element.ALIGN_CENTER);
            celdaBoleta.addElement(numeroBoleta);

            encabezado.addCell(celdaBoleta);
            documento.add(encabezado);

            // Línea separadora
            Paragraph linea = new Paragraph();
            linea.setSpacingBefore(15);
            linea.setSpacingAfter(15);
            documento.add(linea);

            // ==================== INFORMACIÓN DEL PEDIDO ====================
            PdfPTable infoPedido = new PdfPTable(2);
            infoPedido.setWidthPercentage(100);
            infoPedido.setWidths(new float[]{1, 1});
            infoPedido.setSpacingAfter(20);

            Font fuenteLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, colorTexto);
            Font fuenteValue = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

            // Información del cliente
            PdfPCell celdaCliente = new PdfPCell();
            celdaCliente.setBorder(Rectangle.NO_BORDER);
            celdaCliente.setPadding(15);

            Paragraph tituloCliente = new Paragraph("DATOS DEL CLIENTE", fuenteLabel);
            tituloCliente.setSpacingAfter(8);
            celdaCliente.addElement(tituloCliente);

            // Línea sutil debajo del título
            Paragraph lineaCliente = new Paragraph("_________________________________");
            lineaCliente.getFont().setColor(colorTerciario);
            lineaCliente.setSpacingAfter(8);
            celdaCliente.addElement(lineaCliente);

            Paragraph nombreCliente = new Paragraph();
            nombreCliente.add(new Chunk("Cliente: ", fuenteLabel));
            nombreCliente.add(new Chunk(pedido.getUser().getName(), fuenteValue));
            nombreCliente.setSpacingAfter(3);
            celdaCliente.addElement(nombreCliente);

            infoPedido.addCell(celdaCliente);

            // Información del pedido
            PdfPCell celdaPedidoInfo = new PdfPCell();
            celdaPedidoInfo.setBorder(Rectangle.NO_BORDER);
            celdaPedidoInfo.setPadding(15);

            Paragraph tituloPedido = new Paragraph("DATOS DEL PEDIDO", fuenteLabel);
            tituloPedido.setSpacingAfter(8);
            celdaPedidoInfo.addElement(tituloPedido);

            // Línea sutil debajo del título
            Paragraph lineaPedido = new Paragraph("_________________________________");
            lineaPedido.getFont().setColor(colorTerciario);
            lineaPedido.setSpacingAfter(8);
            celdaPedidoInfo.addElement(lineaPedido);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaFormateada = pedido.getCreatedAt().format(formatter);

            Paragraph fechaPedido = new Paragraph();
            fechaPedido.add(new Chunk("Fecha: ", fuenteLabel));
            fechaPedido.add(new Chunk(fechaFormateada, fuenteValue));
            fechaPedido.setSpacingAfter(3);
            celdaPedidoInfo.addElement(fechaPedido);

            Paragraph idPedido = new Paragraph();
            idPedido.add(new Chunk("ID Pedido: ", fuenteLabel));
            idPedido.add(new Chunk(pedido.getId().toString(), fuenteValue));
            celdaPedidoInfo.addElement(idPedido);

            infoPedido.addCell(celdaPedidoInfo);
            documento.add(infoPedido);

            // ==================== TABLA DE PRODUCTOS ====================
            // Título de la sección
            Font fuenteTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, colorPrincipal);
            Paragraph tituloProductos = new Paragraph("DETALLE DE PRODUCTOS", fuenteTituloSeccion);
            tituloProductos.setSpacingAfter(10);
            documento.add(tituloProductos);

            // Crear tabla de productos
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{4, 1.5f, 2, 2});
            tabla.setSpacingAfter(15);

            // Encabezados de tabla
            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            String[] encabezados = {"PRODUCTO", "CANT.", "PRECIO UNIT.", "SUBTOTAL"};

            for (String e : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(e, fuenteEncabezado));
                celda.setBackgroundColor(colorPrincipal);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(8);
                celda.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(celda);
            }

            // Filas de productos
            Font fuenteProducto = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font fuenteNumeros = FontFactory.getFont(FontFactory.HELVETICA, 10, colorTexto);

            boolean filaAlterna = false;
            for (OrderDetail detalle : detalles) {
                Product producto = detalle.getProducto();
                Color colorFondo = filaAlterna ? new Color(252, 252, 252) : Color.WHITE;

                // Nombre del producto
                PdfPCell celdaProducto = new PdfPCell(new Phrase(producto.getName(), fuenteProducto));
                celdaProducto.setBackgroundColor(colorFondo);
                celdaProducto.setPadding(10);
                celdaProducto.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(celdaProducto);

                // Cantidad
                PdfPCell celdaCantidad = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), fuenteNumeros));
                celdaCantidad.setBackgroundColor(colorFondo);
                celdaCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaCantidad.setPadding(10);
                celdaCantidad.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(celdaCantidad);

                // Precio unitario
                PdfPCell celdaPrecio = new PdfPCell(new Phrase("S/. " + String.format("%.2f", producto.getPrecio()), fuenteNumeros));
                celdaPrecio.setBackgroundColor(colorFondo);
                celdaPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaPrecio.setPadding(10);
                celdaPrecio.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(celdaPrecio);

                // Subtotal
                PdfPCell celdaSubtotal = new PdfPCell(new Phrase("S/. " + String.format("%.2f", detalle.getSubtotal()), fuenteNumeros));
                celdaSubtotal.setBackgroundColor(colorFondo);
                celdaSubtotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaSubtotal.setPadding(10);
                celdaSubtotal.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(celdaSubtotal);

                filaAlterna = !filaAlterna;
            }

            documento.add(tabla);

            // ==================== RESUMEN DE COSTOS ====================
            PdfPTable tablaResumen = new PdfPTable(2);
            tablaResumen.setWidthPercentage(60);
            tablaResumen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaResumen.setWidths(new float[]{2, 1});

            Font fuenteResumenLabel = FontFactory.getFont(FontFactory.HELVETICA, 11, colorTexto);
            Font fuenteResumenValue = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font fuenteTotalLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, colorSecundario);
            Font fuenteTotalValue = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, colorSecundario);

            // Calcular subtotal (total - envío)
            double subtotal = pedido.getTotal() - pedido.getEnvio();

            // Subtotal
            PdfPCell labelSubtotal = new PdfPCell(new Phrase("Subtotal:", fuenteResumenLabel));
            labelSubtotal.setBorder(Rectangle.NO_BORDER);
            labelSubtotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelSubtotal.setPadding(5);
            tablaResumen.addCell(labelSubtotal);

            PdfPCell valueSubtotal = new PdfPCell(new Phrase("S/. " + String.format("%.2f", subtotal), fuenteResumenValue));
            valueSubtotal.setBorder(Rectangle.NO_BORDER);
            valueSubtotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valueSubtotal.setPadding(5);
            tablaResumen.addCell(valueSubtotal);

            // Envío
            PdfPCell labelEnvio = new PdfPCell(new Phrase("Envío:", fuenteResumenLabel));
            labelEnvio.setBorder(Rectangle.NO_BORDER);
            labelEnvio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelEnvio.setPadding(5);
            tablaResumen.addCell(labelEnvio);

            PdfPCell valueEnvio = new PdfPCell(new Phrase("S/. " + String.format("%.2f", pedido.getEnvio()), fuenteResumenValue));
            valueEnvio.setBorder(Rectangle.NO_BORDER);
            valueEnvio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valueEnvio.setPadding(5);
            tablaResumen.addCell(valueEnvio);

            // Línea separadora
            PdfPCell lineaSep1 = new PdfPCell(new Phrase(""));
            lineaSep1.setBorder(Rectangle.TOP);
            lineaSep1.setBorderColor(colorTerciario);
            lineaSep1.setPadding(2);
            tablaResumen.addCell(lineaSep1);

            PdfPCell lineaSep2 = new PdfPCell(new Phrase(""));
            lineaSep2.setBorder(Rectangle.TOP);
            lineaSep2.setBorderColor(colorTerciario);
            lineaSep2.setPadding(2);
            tablaResumen.addCell(lineaSep2);

            // Total
            PdfPCell labelTotal = new PdfPCell(new Phrase("TOTAL:", fuenteTotalLabel));
            labelTotal.setBorder(Rectangle.NO_BORDER);
            labelTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelTotal.setPadding(8);
            tablaResumen.addCell(labelTotal);

            PdfPCell valueTotal = new PdfPCell(new Phrase("S/. " + String.format("%.2f", pedido.getTotal()), fuenteTotalValue));
            valueTotal.setBorder(Rectangle.NO_BORDER);
            valueTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valueTotal.setPadding(8);
            tablaResumen.addCell(valueTotal);

            documento.add(tablaResumen);

            // ==================== PIE DE PÁGINA ====================
            Paragraph espacioFinal = new Paragraph(" ");
            espacioFinal.setSpacingBefore(30);
            documento.add(espacioFinal);

            Font fuentePie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, colorTexto);
            Paragraph agradecimiento = new Paragraph("¡Gracias por elegirnos! En Librería Creativa valoramos tu confianza.", fuentePie);
            agradecimiento.setAlignment(Element.ALIGN_CENTER);
            agradecimiento.setSpacingAfter(5);
            documento.add(agradecimiento);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }

        return baos.toByteArray();
    }
}
