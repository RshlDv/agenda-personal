package com.agenda.agenda_personal.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agenda.agenda_personal.model.Categoria;
import com.agenda.agenda_personal.model.Evento;
import com.agenda.agenda_personal.repository.CategoriaRepository;
import com.agenda.agenda_personal.service.EventoService;

@Controller
@RequestMapping("/")
public class AgendaController {

    private final EventoService eventoService;
    private final CategoriaRepository categoriaRepository;

    public AgendaController(EventoService eventoService, CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.eventoService = eventoService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFiltro,
            Model model) {

        List<Evento> todosLosEventos = eventoService.obtenerTodos();
        LocalDate hoy = LocalDate.now();

        //Busqueda y filtrado
        List<Evento> eventosFiltrados = todosLosEventos;
        if (buscar != null && !buscar.trim().isEmpty()) {
            eventosFiltrados = eventosFiltrados.stream()
                    .filter(e -> e.getTitulo().toLowerCase().contains(buscar.toLowerCase())
                    || (e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(buscar.toLowerCase())))
                    .collect(Collectors.toList());
        }
        if (fechaFiltro != null) {
            eventosFiltrados = eventosFiltrados.stream()
                    .filter(e -> e.getFecha().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }

        //Contadores
        long totalHoy = todosLosEventos.stream().filter(e -> e.getFecha().equals(hoy) && !e.isCompletado()).count();

        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);
        long totalSemana = todosLosEventos.stream()
                .filter(e -> !e.getFecha().isBefore(inicioSemana) && !e.getFecha().isAfter(finSemana) && !e.isCompletado())
                .count();

        long atrasadas = todosLosEventos.stream().filter(e -> e.getFecha().isBefore(hoy) && !e.isCompletado()).count();
        long total = todosLosEventos.stream().filter(e -> !e.isCompletado()).count();

        // Pasar variables a Thymeleaf
        model.addAttribute("eventos", eventosFiltrados);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("totalHoy", totalHoy);
        model.addAttribute("totalSemana", totalSemana);
        model.addAttribute("atrasadas", atrasadas);
        model.addAttribute("total", total);
        model.addAttribute("buscar", buscar);
        model.addAttribute("fechaFiltro", fechaFiltro);

        return "dashboard";
    }

    @GetMapping("/calendario")
    public String calendario(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "calendario";
    }

    //Guardar Categorías, color no repetido
    @PostMapping("/categorias")
    public String guardarCategoria(@RequestParam String nombre, @RequestParam String colorHex) {
        if (!categoriaRepository.existsByColorHex(colorHex) && nombre != null && !nombre.trim().isEmpty()) {
            Categoria nuevaCat = Categoria.builder()
                    .nombre(nombre)
                    .colorHex(colorHex)
                    .build();
            categoriaRepository.save(nuevaCat);
        }
        return "redirect:/";
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaRepository.findById(id).ifPresent(categoria -> {
            if (categoria.getEventos() != null) {
                categoria.getEventos().forEach(evento -> evento.setCategoria(null));
            }
            categoriaRepository.delete(categoria);
        });
        return "redirect:/";
    }

    @PostMapping("/eventos")
    public String guardarEvento(@ModelAttribute Evento evento, @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            categoriaRepository.findById(categoriaId).ifPresent(evento::setCategoria);
        }
        eventoService.guardarEvento(evento);
        return "redirect:/";
    }

    @PostMapping("/eventos/completar/{id}")
    public String completarEvento(@PathVariable Long id) {
        eventoService.completarEvento(id);
        return "redirect:/";
    }

    @PostMapping("/eventos/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return "redirect:/";
    }

    @PostMapping("/eventos/editar/{id}")
    public String editarEvento(@PathVariable Long id, @ModelAttribute Evento evento, @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            categoriaRepository.findById(categoriaId).ifPresent(evento::setCategoria);
        } else {
            evento.setCategoria(null);
        }
        eventoService.actualizarEvento(id, evento);
        return "redirect:/";
    }
}
