package com.talep.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TalepRepository extends JpaRepository<Talep, Long> {
    // Müşteri veya Başlık sütunlarında büyük/küçük harf duyarsız arama yapar
    List<Talep> findByMusteriContainingIgnoreCaseOrBaslikContainingIgnoreCase(String musteri, String baslik);
}