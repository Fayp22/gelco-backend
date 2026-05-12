package com.gelco.repository;

import com.gelco.model.Consultora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultoraRepository extends JpaRepository<Consultora, Long> {
    Optional<Consultora> findByUsuarioId(Long usuarioId);
}
