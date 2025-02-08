package com.ecommerce.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.demo.model.Product;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
   
   @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
   List<Product> findByStockLessThan(@Param("threshold") Integer threshold);

   @Query("SELECT p FROM Product p WHERE p.category = :category")
   Page<Product> findByCategory(@Param("category") String category, Pageable pageable);

   @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
   Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                 @Param("maxPrice") BigDecimal maxPrice, 
                                 Pageable pageable);

   @Modifying
   @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
   int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("SELECT p FROM Product p WHERE p.id = :id")
   Optional<Product> findByIdWithLock(@Param("id") Long id);

   @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.name = :name")
   boolean existsByName(@Param("name") String name);

   @Query(value = "SELECT * FROM products WHERE tsv @@ to_tsquery(:query)", nativeQuery = true)
   Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
}
