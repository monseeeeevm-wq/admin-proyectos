package com.mx.cbtis.gestor.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
public class ProyectosController {

	private final ProtectosRepository proyectostable;

	public ProyectosController(ProtectosRepository proyectostable) {
		this.proyectostable = proyectostable;
	}

	@GetMapping("/buscarProyecto")
	public Proyectos getProyecto(@RequestParam int id_proyecto) {

		Optional<Proyectos> proyecto = proyectostable.findById(id_proyecto);
		if (!proyecto.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado");
		}

		return proyecto.get();
	}

	@PostMapping("/addProyecto")
	public int addProyecto(@RequestBody Proyectos proyecto) {

		validarFechas(proyecto);
		int flag = 0;
		if (proyecto != null) {

			proyectostable.save(proyecto);
			flag = 1;
		}

		return flag;
	}

	@PostMapping("/updateProyecto")
	public int updateProyecto(@RequestBody Proyectos proyecto) {
		return addProyecto(proyecto);
	}

	@DeleteMapping("/deleteProyecto")
	public int deleteProyecto(@RequestBody Proyectos proyecto) {

		int flag = 0;
		if (proyecto != null) {
			proyectostable.delete(proyecto);
			flag = 1;
		}

		return flag;
	}

	@PostMapping("/buscarProyectos")
	public List<Proyectos> getProyectos() {

		List<Proyectos> listaProyectos = new ArrayList<>();
		proyectostable.findAll().forEach(listaProyectos::add);
		return listaProyectos;
	}

	/** Valida que las fechas (si vienen) tengan formato yyyy-MM-dd; si no, responde 400. */
	private void validarFechas(Proyectos proyecto) {
		validarFecha(proyecto.getFecha_inicio(), "fecha_inicio");
		validarFecha(proyecto.getFecha_finalizacion(), "fecha_finalizacion");
	}

	private void validarFecha(String valor, String campo) {
		if (valor == null || valor.isEmpty()) {
			return;
		}
		try {
			LocalDate.parse(valor);
		} catch (DateTimeParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Fecha inválida en " + campo + " (formato yyyy-MM-dd)");
		}
	}

}