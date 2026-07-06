package com.agenda.agenda_personal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.agenda_personal.model.Evento;
import java.time.LocalDate;
import java.time.LocalTime;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    // Nueva query para verificar si ya hay un plan a esa misma fecha y hora
    boolean existsByFechaAndHora(LocalDate fecha, LocalTime hora);
}