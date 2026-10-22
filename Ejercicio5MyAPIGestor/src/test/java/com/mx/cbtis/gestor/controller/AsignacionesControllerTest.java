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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@WebMvcTest(AsignacionesController.class)
class AsignacionesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private AsignacionesRepository repo;

	private Asignaciones asignacion() {
		return new Asignaciones(1, 131, 199, "2024-06-20");
	}

	@Test
	@DisplayName("UT-30 GET /buscarAsignacion con ID existente devuelve la asignación")
	void buscarAsignacionExistente() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.of(asignacion()));

		mockMvc.perform(get("/buscarAsignacion").param("id", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_asignacion").value(1))
				.andExpect(jsonPath("$.id_usuario").value(131));
	}

	@Test
	@DisplayName("UT-31 GET /buscarAsignacion con ID inexistente devuelve objeto vacío (comportamiento actual)")
	void buscarAsignacionInexistente() throws Exception {
		when(repo.findById(999999)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarAsignacion").param("id", "999999"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_asignacion").isEmpty());
	}

	@Test
	@DisplayName("UT-32 POST /addAsignacion con JSON válido guarda y responde 1")
	void addAsignacion() throws Exception {
		mockMvc.perform(post("/addAsignacion").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(asignacion())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Asignaciones.class));
	}

	@Test
	@DisplayName("UT-33 POST /updateAsignacion guarda cambios y responde 1")
	void updateAsignacion() throws Exception {
		Asignaciones a = asignacion();
		a.setFecha_asignacion("2024-06-25");

		mockMvc.perform(post("/updateAsignacion").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(a)))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Asignaciones.class));
	}

	@Test
	@DisplayName("UT-34 DELETE /deleteAsignacion elimina y responde 1")
	void deleteAsignacion() throws Exception {
		mockMvc.perform(delete("/deleteAsignacion").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(asignacion())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).delete(any(Asignaciones.class));
	}

	@Test
	@DisplayName("UT-35 POST /buscarAsignaciones devuelve la lista")
	void listarAsignaciones() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(asignacion()));

		mockMvc.perform(post("/buscarAsignaciones"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
	}
}
