package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.service.CloudinaryService;
import com.LibreriaCreativa.LibreriaCreativa.service.CategoryService;
import com.LibreriaCreativa.LibreriaCreativa.service.ProductService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.LibreriaCreativa.LibreriaCreativa.model.Category;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import com.LibreriaCreativa.LibreriaCreativa.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductService productService;

    @Autowired
    CategoryService catSer;

    @PostMapping("/save")
    public ResponseEntity<?> saveProduct(
            @ModelAttribute Product producto,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            Category categoria = catSer.buscarPorId(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            producto.setCategory(categoria);

            // Validar: imagen obligatoria en alta
            if ((producto.getId() == null || producto.getImg() == null || producto.getImg().isEmpty())
                    && (imageFile == null || imageFile.isEmpty())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe proporcionar una imagen para un producto nuevo."));
            }

            // Manejo de imagen
            if (imageFile != null && !imageFile.isEmpty()) {
                // Borrar imagen anterior si existe
                if (producto.getImg() != null && !producto.getImg().isEmpty()) {
                    String publicId = cloudinaryService.getPublicId(producto.getImg());
                    if (publicId != null) {
                        cloudinaryService.deleteFile(publicId);
                    }
                }

                String imageUrl = cloudinaryService.uploadFile(imageFile, "productos");
                producto.setImg(imageUrl);
            }

            productService.guardarProducto(producto);

            return ResponseEntity.ok().body(Map.of("message", "Producto guardado correctamente."));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al subir la imagen."));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        try {
            Optional<Product> producto = productService.buscarPorId(id);

            if (producto.isPresent() && producto.get().getImg() != null) {
                String publicId = cloudinaryService.getPublicId(producto.get().getImg());
                if (publicId != null) {
                    cloudinaryService.deleteFile(publicId);
                }
            }

            productService.EliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo eliminar el producto.");
        }
    }

    @GetMapping("/admin")
    public Page<Product> listarProductosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return productService.obtenerProductosPaginados(page, size);
    }

    @GetMapping("/catalogo")
    public Page<Product> obtenerCatalogo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "default") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> categorias,
            @RequestParam(required = false) String precio,
            @RequestParam(required = false) String stock
    ) {

        Double minPrecio = null;
        Double maxPrecio = null;

        Sort orden;
        orden = switch (sort) {
            case "name" ->
                Sort.by("name").ascending();
            case "name-desc" ->
                Sort.by("name").descending();
            case "price" ->
                Sort.by("precio").ascending();
            case "price-desc" ->
                Sort.by("precio").descending();
            default ->
                Sort.by("id").descending();
        };

        if (precio != null && !precio.isBlank()) {
            try {
                if (precio.startsWith("+")) {
                    minPrecio = Double.valueOf(precio.substring(1));
                } else if (precio.contains("-")) {
                    String[] partes = precio.split("-");
                    minPrecio = Double.valueOf(partes[0]);
                    maxPrecio = Double.valueOf(partes[1]);
                } else {
                    minPrecio = Double.valueOf(precio);
                    maxPrecio = minPrecio;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato de precio inválido: " + precio);
            }
        }

        Pageable pageable = PageRequest.of(page, size, orden);

        return productService.obtenerProductosConFiltros(
                (q != null && !q.isBlank()) ? q : null,
                (categorias != null && !categorias.isEmpty()) ? categorias : null,
                (stock != null && !stock.isBlank()) ? stock : null,
                minPrecio, maxPrecio, pageable
        );
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Integer id) throws Exception {
        Product product = productService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // Parsear specs del string JSON a Map
        if (product.getSpecs() != null && !product.getSpecs().trim().equals("{}")) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> specsMap = mapper.readValue(product.getSpecs(), new TypeReference<Map<String, String>>() {
            });
            product.setSpecsMap(specsMap);
        } else {
            product.setSpecsMap(null);
        }

        return product;
    }

    @GetMapping("/dest")
    public List<Product> getDestacados() {
        return productService.obtenerDestacados();
    }

}
