package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.model.Category;
import com.LibreriaCreativa.LibreriaCreativa.service.CategoryService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public List<Category> obtenerTodas() {
        return categoryService.listarTodas();
    }

    @GetMapping("/admin")
    public Page<Category> obtenerCategoriasAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.obtenerCategoriasPaginadas(page, size);
    }

    @PostMapping("/save")
    public ResponseEntity<?> guardarCategoria(@RequestBody Category categoria) {
        Optional<Category> existente = categoryService.buscarPorNombre(categoria.getName());
        if (existente.isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El nombre de la categoría ya existe."));
        }

        try {
            Category saved = categoryService.guardar(categoria);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al guardar la categoría."));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> actualizarCategoria(@RequestBody Category categoria) {
        if (categoria.getId() == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "ID de categoría no especificado."));
        }

        Optional<Category> encontrada = categoryService.buscarPorId(categoria.getId());
        if (encontrada.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "La categoría no existe."));
        }

        Category catExistente = encontrada.get();
        String nuevoNombre = categoria.getName();

        // Validar si cambió
        if (catExistente.getName().equalsIgnoreCase(nuevoNombre)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El nuevo nombre es igual al anterior."));
        }

        // Verificar unicidad del nuevo nombre
        if (categoryService.buscarPorNombre(nuevoNombre).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El nombre de la categoría ya está en uso."));
        }

        try {
            catExistente.setName(nuevoNombre);
            Category updated = categoryService.guardar(catExistente);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar la categoría."));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        Optional<Category> encontrada = categoryService.buscarPorId(id);
        if (encontrada.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Categoría no encontrada."));
        }

        try {
            categoryService.borrarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar la categoría."));
        }
    }
}
