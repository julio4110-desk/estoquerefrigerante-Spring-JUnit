package com.juliobalbino.estoquerefrigerante.repository;

import com.juliobalbino.estoquerefrigerante.entity.Soda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SodaRepository extends JpaRepository<Soda, Long> {

    Optional<Soda> findByName(String name);
}
