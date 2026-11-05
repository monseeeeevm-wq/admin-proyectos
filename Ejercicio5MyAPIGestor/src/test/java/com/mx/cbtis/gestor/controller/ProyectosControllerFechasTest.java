package com.mx.cbtis.gestor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

/** Casos de borde de la validación de fechas de ProyectosController (fechas ausentes o vacías). */
@WebMvcTest(ProyectosController.class)
class ProyectosControllerFechasTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProtectosRepository repo;

	@Test
	@DisplayName("UT-38 POST /addProyecto sin fechas (nulas) es válido y se guarda")
	void addProyectoSinFechasSeGuarda() throws Exception {
		mockMvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Sin fechas\",\"descripcion\":\"x\",\"estado_proyecto\":\"En proceso\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Proyectos.class));
	}

	@Test
	@DisplayName("UT-39 POST /updateProyecto con fechas vacías es válido y se guarda")
	void updateProyectoConFechasVaciasSeGuarda() throws Exception {
		mockMvc.perform(post("/updateProyecto").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_proyecto\":197,\"nombre\":\"Vacias\",\"fecha_inicio\":\"\",\"fecha_finalizacion\":\"\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Proyectos.class));
	}
}
