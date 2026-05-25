package com.gelco.repository;

import com.gelco.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByConsultoraId(Long consultoraId);
    Optional<Cliente> findByIdAndConsultoraId(Long id, Long consultoraId);
    boolean existsByIdAndConsultoraId(Long id, Long consultoraId);
}
