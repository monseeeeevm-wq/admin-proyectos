package com.mx.cbtis.gestor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@WebMvcTest(ProyectosController.class)
class ProyectosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private ProtectosRepository repo;

	private Proyectos proyecto() {
		return new Proyectos(197, "My API Rest", "Desarrollo de un API Rest", "2024-05-31", "2024-06-29",
				"En proceso", "/Images/animacion1.gif");
	}

	@Test
	@DisplayName("UT-10 GET /buscarProyecto con ID existente devuelve el proyecto")
	void buscarProyectoExistente() throws Exception {
		when(repo.findById(197)).thenReturn(Optional.of(proyecto()));

		mockMvc.perform(get("/buscarProyecto").param("id_proyecto", "197"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_proyecto").value(197))
				.andExpect(jsonPath("$.nombre").value("My API Rest"))
				.andExpect(jsonPath("$.estado_proyecto").value("En proceso"));
	}

	@Test
	@DisplayName("UT-11 GET /buscarProyecto con ID inexistente responde 404 (corrección F-02)")
	void buscarProyectoInexistente() throws Exception {
		when(repo.findById(999999)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarProyecto").param("id_proyecto", "999999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("UT-12 POST /addProyecto con JSON válido guarda y responde 1")
	void addProyecto() throws Exception {
		mockMvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(proyecto())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-13 POST /updateProyecto guarda cambios y responde 1")
	void updateProyecto() throws Exception {
		Proyectos p = proyecto();
		p.setEstado_proyecto("Terminado");

		mockMvc.perform(post("/updateProyecto").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(p)))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-14 DELETE /deleteProyecto elimina y responde 1")
	void deleteProyecto() throws Exception {
		mockMvc.perform(delete("/deleteProyecto").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(proyecto())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).delete(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-15 POST /buscarProyectos devuelve la lista")
	void listarProyectos() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(proyecto()));

		mockMvc.perform(post("/buscarProyectos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].nombre").value("My API Rest"));
	}

	@Test
	@DisplayName("UT-36 POST /addProyecto con fecha inválida responde 400 y no guarda (corrección F-03)")
	void addProyectoFechaInvalida() throws Exception {
		Proyectos p = proyecto();
		p.setFecha_inicio("no-es-una-fecha");

		mockMvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(p)))
				.andExpect(status().isBadRequest());

		verify(repo, never()).save(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-37 POST /updateProyecto con fecha inválida responde 400 y no guarda (corrección F-03)")
	void updateProyectoFechaInvalida() throws Exception {
		Proyectos p = proyecto();
		p.setFecha_finalizacion("31/12/2026");

		mockMvc.perform(post("/updateProyecto").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(p)))
				.andExpect(status().isBadRequest());

		verify(repo, never()).save(any(Proyectos.class));
	}
}