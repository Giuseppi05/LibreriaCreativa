package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Product;
import com.LibreriaCreativa.LibreriaCreativa.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    public Product guardarProducto(Product producto) {
        return productRepo.save(producto);
    }

    public void EliminarProducto(Integer id) {
        productRepo.deleteById(id);
    }
    
    public List<Product> ObtenerProductos(){
        return productRepo.findAll();
    }

    public Optional<Product> buscarPorNombre(String name) {
        return productRepo.findByName(name);
    }

    public Optional<Product> buscarPorId(Integer id) {
        return productRepo.findById(id);
    }

    public Page<Product> obtenerProductosPaginados(int page, int size) {
        return productRepo.findAll(PageRequest.of(page, size));
    }

    public Page<Product> obtenerProductosConFiltros(String q, List<String> categorias,
            String stock, Double minPrecio,
            Double maxPrecio, Pageable pageable) {
        return productRepo.buscarConFiltros(q, categorias, stock, minPrecio, maxPrecio, pageable);
    }
    
    public List<Product> obtenerDestacados(){
        return productRepo.findTop4BestSellersNative();
    }

}
