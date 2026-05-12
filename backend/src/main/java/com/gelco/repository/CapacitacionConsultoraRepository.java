package com.gelco.repository;

import com.gelco.model.CapacitacionConsultora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapacitacionConsultoraRepository extends JpaRepository<CapacitacionConsultora, Long> {
    List<CapacitacionConsultora> findByConsultoraId(Long consultoraId);
    List<CapacitacionConsultora> findByCapacitacionId(Long capacitacionId);
}
