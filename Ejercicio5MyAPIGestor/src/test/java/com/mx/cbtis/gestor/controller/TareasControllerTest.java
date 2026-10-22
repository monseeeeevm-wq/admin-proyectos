package com.mx.cbtis.gestor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@WebMvcTest(TareasController.class)
class TareasControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private TareasRepository repo;

	private Tareas tarea() {
		return new Tareas(199, "Base de datos", "Realizar la estructura de las tablas", "2024-06-13", "terminado",
				"danger", 197);
	}

	@Test
	@DisplayName("UT-16 GET /buscarTarea con ID existente devuelve la tarea")
	void buscarTareaExistente() throws Exception {
		when(repo.findById(199)).thenReturn(Optional.of(tarea()));

		mockMvc.perform(get("/buscarTarea").param("id_tarea", "199"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea").value(199))
				.andExpect(jsonPath("$.nombre").value("Base de datos"));
	}

	@Test
	@DisplayName("UT-17 GET /buscarTarea con ID inexistente devuelve objeto vacío (comportamiento actual)")
	void buscarTareaInexistente() throws Exception {
		when(repo.findById(999999)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarTarea").param("id_tarea", "999999"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_tarea").isEmpty());
	}

	@Test
	@DisplayName("UT-18 POST /getTarea devuelve las tareas de un proyecto")
	void tareasPorProyecto() throws Exception {
		when(repo.findByIdProyecto(197)).thenReturn(Arrays.asList(tarea()));

		mockMvc.perform(post("/getTarea").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id_proyecto").value(197));
	}

	@Test
	@DisplayName("UT-19 POST /addTareas con JSON válido guarda y responde 1")
	void addTareas() throws Exception {
		mockMvc.perform(post("/addTareas").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(tarea())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Tareas.class));
	}

	@Test
	@DisplayName("UT-20 POST /updateTareas guarda cambios y responde 1")
	void updateTareas() throws Exception {
		Tareas t = tarea();
		t.setLista("en proceso");

		mockMvc.perform(post("/updateTareas").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(t)))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Tareas.class));
	}

	@Test
	@DisplayName("UT-21 DELETE /deleteTareas elimina y responde 1")
	void deleteTareas() throws Exception {
		mockMvc.perform(delete("/deleteTareas").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(tarea())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).delete(any(Tareas.class));
	}

	@Test
	@DisplayName("UT-22 POST /buscarTareas devuelve la lista")
	void listarTareas() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(tarea()));

		mockMvc.perform(post("/buscarTareas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
	}
}
