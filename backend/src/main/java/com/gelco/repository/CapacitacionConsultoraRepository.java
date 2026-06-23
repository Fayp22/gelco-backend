package com.gelco.repository;

import com.gelco.model.CapacitacionConsultora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapacitacionConsultoraRepository extends JpaRepository<CapacitacionConsultora, Long> {
    List<CapacitacionConsultora> findByConsultoraId(Long consultoraId);
    List<CapacitacionConsultora> findByCapacitacionId(Long capacitacionId);
    long countByConsultoraIdAndCompletado(Long consultoraId, Boolean completado);
    long countByCapacitacionId(Long capacitacionId);
    long countByCapacitacionIdAndCompletado(Long capacitacionId, Boolean completado);
    boolean existsByCapacitacionIdAndConsultoraId(Long capacitacionId, Long consultoraId);
    void deleteByCapacitacionIdAndConsultoraId(Long capacitacionId, Long consultoraId);
}
