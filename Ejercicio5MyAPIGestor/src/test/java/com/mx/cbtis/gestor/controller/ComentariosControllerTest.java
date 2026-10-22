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

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@WebMvcTest(ComentariosController.class)
class ComentariosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private ComentariosRepository repo;

	private Comentarios comentario() {
		return new Comentarios(201, "Perla Joceline Martínez Meza", 199, "Subirlo YA!!!", "2024-06-26 13:48:00");
	}

	@Test
	@DisplayName("UT-23 GET /buscarComentario con ID existente devuelve el comentario")
	void buscarComentarioExistente() throws Exception {
		when(repo.findById(201)).thenReturn(Optional.of(comentario()));

		mockMvc.perform(get("/buscarComentario").param("id", "201"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_comentario").value(201))
				.andExpect(jsonPath("$.texto_comentario").value("Subirlo YA!!!"));
	}

	@Test
	@DisplayName("UT-24 GET /buscarComentario con ID inexistente devuelve objeto vacío (comportamiento actual)")
	void buscarComentarioInexistente() throws Exception {
		when(repo.findById(999999)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarComentario").param("id", "999999"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_comentario").isEmpty());
	}

	@Test
	@DisplayName("UT-25 POST /getComentario devuelve los comentarios de una tarea")
	void comentariosPorTarea() throws Exception {
		when(repo.findByTarea(199)).thenReturn(Arrays.asList(comentario()));

		mockMvc.perform(post("/getComentario").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id_tarea\":199}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id_tarea").value(199));
	}

	@Test
	@DisplayName("UT-26 POST /addComentario con JSON válido guarda y responde 1")
	void addComentario() throws Exception {
		mockMvc.perform(post("/addComentario").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(comentario())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Comentarios.class));
	}

	@Test
	@DisplayName("UT-27 POST /updateComentario guarda cambios y responde 1")
	void updateComentario() throws Exception {
		Comentarios c = comentario();
		c.setTexto_comentario("Texto editado");

		mockMvc.perform(post("/updateComentario").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(c)))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Comentarios.class));
	}

	@Test
	@DisplayName("UT-28 DELETE /deleteComentario elimina y responde 1")
	void deleteComentario() throws Exception {
		mockMvc.perform(delete("/deleteComentario").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(comentario())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).delete(any(Comentarios.class));
	}

	@Test
	@DisplayName("UT-29 POST /buscarComentarios devuelve la lista")
	void listarComentarios() throws Exception {
		when(repo.findAll()).thenReturn(Arrays.asList(comentario()));

		mockMvc.perform(post("/buscarComentarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
	}
}
