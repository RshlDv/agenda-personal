package com.agenda.agenda_personal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.agenda_personal.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByColorHex(String color);
}
