package com.gelco.repository;

import com.gelco.model.Chofer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {
}
