package com.ecommerce.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderStatus;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   
   @Query("SELECT o FROM Order o WHERE o.customerEmail = :email")
   Page<Order> findByCustomerEmail(@Param("email") String email, Pageable pageable);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("SELECT o FROM Order o WHERE o.id = :id")
   Optional<Order> findByIdWithLock(@Param("id") Long id);

   @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :timestamp")
   List<Order> findByStatusAndCreatedAtBefore(@Param("status") OrderStatus status, 
                                             @Param("timestamp") LocalDateTime timestamp);

   @Query("SELECT o FROM Order o WHERE o.status = :status")
   Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

   @Query(value = "SELECT * FROM orders WHERE created_at BETWEEN :startDate AND :endDate", 
          nativeQuery = true)
   List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

   @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
   BigDecimal calculateTotalAmountByStatus(@Param("status") OrderStatus status);

   @Modifying
   @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
   int updateOrderStatus(@Param("id") Long id, @Param("status") OrderStatus status);
}
