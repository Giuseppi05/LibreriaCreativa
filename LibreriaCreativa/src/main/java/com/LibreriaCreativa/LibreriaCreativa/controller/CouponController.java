package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.model.Coupon;
import com.LibreriaCreativa.LibreriaCreativa.service.CouponService;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService cpnSrv;

    @GetMapping("/admin")
    public Page<Coupon> obtenerCategoriasAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return cpnSrv.obtenerCuponesPaginados(page, size);
    }

    @PostMapping("/save")
    public ResponseEntity<?> guardarCoupon(@RequestBody Coupon coupon) {
        Optional<Coupon> existente = cpnSrv.buscarPorCodigo(coupon.getCodigoCupon());
        if (existente.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El código del cupón ya existe."));
        }

        try {
            Coupon saved = cpnSrv.guardar(coupon);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar el cupón."));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarCoupon(@RequestBody Coupon coupon) {
        if (coupon.getId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ID de cupón no especificado."));
        }

        Optional<Coupon> encontrada = cpnSrv.buscarPorId(coupon.getId());
        if (encontrada.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El cupón no existe."));
        }

        Coupon cupExistente = encontrada.get();
        String nuevoCodigo = coupon.getCodigoCupon();

        // Validar si el código no cambió
        if (cupExistente.getCodigoCupon().equalsIgnoreCase(nuevoCodigo)) {
            // El código es el mismo, pero igual podemos actualizar descuento y estado
            cupExistente.setDescuento(coupon.getDescuento());
            cupExistente.setActivo(coupon.getActivo());

            try {
                Coupon actualizado = cpnSrv.guardar(cupExistente);
                return ResponseEntity.ok(actualizado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al actualizar el cupón."));
            }
        }

        // Si cambió el código, verificar que no exista otro cupón con el nuevo código
        Optional<Coupon> codigoEnUso = cpnSrv.buscarPorCodigo(nuevoCodigo);
        if (codigoEnUso.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El código del cupón ya está en uso."));
        }

        // Actualizar todo
        cupExistente.setCodigoCupon(nuevoCodigo);
        cupExistente.setDescuento(coupon.getDescuento());
        cupExistente.setActivo(coupon.getActivo());

        try {
            Coupon actualizado = cpnSrv.guardar(cupExistente);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el cupón."));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        Optional<Coupon> encontrada = cpnSrv.buscarPorId(id);
        if (encontrada.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cupón no encontrada."));
        }

        try {
            cpnSrv.borrarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar la categoría."));
        }
    }

    @GetMapping("/coupon/{code}")
    public ResponseEntity<?> buscarCuponPorCodigo(@PathVariable String code) {
        try {
            Optional<Coupon> c = cpnSrv.buscarPorCodigo(code);

            if (c.isEmpty() || !c.get().getActivo()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("El cupón no está disponible");
            }

            return ResponseEntity.ok(c.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al buscar el cupón.");
        }
    }

}
