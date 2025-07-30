package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    public Optional<Product> findByName(String name);

    public Page<Product> findByActiveTrue(Pageable pageable);

    @Query("""
        SELECT p FROM Product p 
        WHERE p.active = true AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.marca) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(p.category.name) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    public Page<Product> buscarPorNombreMarcaOCategoria(@Param("q") String q, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        AND (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
             OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :q, '%'))
             OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :q, '%')))
        AND (:categorias IS NULL OR p.category.name IN :categorias)
        AND (
            :stock IS NULL OR
            (:stock = 'disponible' AND p.stock > 0) OR
            (:stock = 'agotado' AND p.stock = 0)
        )
        AND (:minPrecio IS NULL OR p.precio >= :minPrecio)
        AND (:maxPrecio IS NULL OR p.precio <= :maxPrecio)
    """)
    Page<Product> buscarConFiltros(
            @Param("q") String q,
            @Param("categorias") List<String> categorias,
            @Param("stock") String stock,
            @Param("minPrecio") Double minPrecio,
            @Param("maxPrecio") Double maxPrecio,
            Pageable pageable
    );

    @Query(value = """
        SELECT p.*
                FROM producto p
                JOIN detallepedido dp ON p.Id = dp.id_producto
                GROUP BY p.Id
                ORDER BY SUM(dp.cantidad) DESC
                LIMIT 4
    """, nativeQuery = true)
    List<Product> findTop4BestSellersNative();

}
