package com.example.demo.repositories;

import com.example.demo.entities.ConversionTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ConversionRepository extends JpaRepository<ConversionTransactionEntity, UUID> {
    Page<ConversionTransactionEntity> findByTimeStampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
