package com.agenda.agenda_personal.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agenda.agenda_personal.model.Evento;
import com.agenda.agenda_personal.repository.EventoRepository;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public void guardarEvento(Evento evento) {
        //Validación de fecha pasada
        if (evento.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No puedes agendar un evento en una fecha pasada.");
        }

        //Validación de no mismo día u hora
        if (eventoRepository.existsByFechaAndHora(evento.getFecha(), evento.getHora())) {
            throw new IllegalArgumentException("Ya tienes un evento programado para esa misma fecha y hora.");
        }

        eventoRepository.save(evento);
    }

    public void completarEvento(Long id) {
        eventoRepository.findById(id).ifPresent(evento -> {
            evento.setCompletado(true);
            eventoRepository.save(evento);
        });
    }

    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }

    public void actualizarEvento(Long id, Evento eventoActualizado) {
        eventoRepository.findById(id).ifPresent(eventoExistente -> {
            eventoExistente.setTitulo(eventoActualizado.getTitulo());
            eventoExistente.setDescripcion(eventoActualizado.getDescripcion());
            eventoExistente.setFecha(eventoActualizado.getFecha());
            eventoExistente.setHora(eventoActualizado.getHora());
            eventoExistente.setUbicacion(eventoActualizado.getUbicacion());
            eventoExistente.setNota(eventoActualizado.getNota());
            eventoExistente.setCategoria(eventoActualizado.getCategoria());

            if (eventoExistente.getFecha().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No puedes mover un evento a una fecha pasada.");
            }

            eventoRepository.save(eventoExistente);
        });
    }
}
