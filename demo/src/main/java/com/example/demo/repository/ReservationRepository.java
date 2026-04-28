package com.example.demo.repository;

import com.example.demo.enums.ReservationStatus;
import com.example.demo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, String>, JpaSpecificationExecutor<Reservation> {
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.item.id = :itemId AND r.status = 'PENDING' AND r.priority < :priority")
    int countHigherPriority(@Param("itemId") int itemId, @Param("priority") int priority);

    @Query("SELECT r FROM Reservation r WHERE r.item.id = :itemId AND r.status = :status ORDER BY r.priority ASC")
    List<Reservation> findByItemIdAndStatusOrderByPriorityAsc(@Param("itemId") int itemId, @Param("status") ReservationStatus status);
}
