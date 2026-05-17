package com.gelco.repository;

import com.gelco.model.VentaConsultora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaConsultoraRepository extends JpaRepository<VentaConsultora, Long> {
    List<VentaConsultora> findByConsultoraId(Long consultoraId);
}
