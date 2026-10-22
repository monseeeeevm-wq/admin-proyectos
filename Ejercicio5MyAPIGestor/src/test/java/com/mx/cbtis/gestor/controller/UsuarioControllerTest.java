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

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private UsuarioRepository repo;

	private Usuario ana() {
		return new Usuario(131, "Ana Paola Ramírez Vázquez", "ana22533@cbtis75.edu.mx", "0722", "Estudiante", "#70FAB7");
	}

	@Test
	@DisplayName("UT-01 GET /buscarUsuario con ID existente devuelve el usuario")
	void buscarUsuarioExistente() throws Exception {
		when(repo.findById(131)).thenReturn(Optional.of(ana()));

		mockMvc.perform(get("/buscarUsuario").param("id", "131"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario").value(131))
				.andExpect(jsonPath("$.nombre").value("Ana Paola Ramírez Vázquez"));
	}

	@Test
	@DisplayName("UT-02 GET /buscarUsuario con ID inexistente devuelve objeto vacío (comportamiento actual)")
	void buscarUsuarioInexistente() throws Exception {
		when(repo.findById(999999)).thenReturn(Optional.empty());

		mockMvc.perform(get("/buscarUsuario").param("id", "999999"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario").isEmpty())
				.andExpect(jsonPath("$.nombre").isEmpty());
	}

	@Test
	@DisplayName("UT-03 POST /addUsuario con JSON válido guarda y responde 1")
	void addUsuarioValido() throws Exception {
		mockMvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(ana())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-04 POST /addUsuario con JSON mal formado responde 400")
	void addUsuarioJsonMalFormado() throws Exception {
		mockMvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON)
				.content("{esto no es json"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("UT-05 POST /getUsuarioLogin con credenciales correctas devuelve el usuario")
	void loginCorrecto() throws Exception {
		when(repo.findByCorreoPass("ana22533@cbtis75.edu.mx", "0722")).thenReturn(Optional.of(ana()));

		mockMvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"ana22533@cbtis75.edu.mx\",\"contrasena\":\"0722\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id_usuario").value(131))
				.andExpect(jsonPath("$.rol").value("Estudiante"));
	}

	@Test
	@DisplayName("UT-06 POST /getUsuarioLogin con credenciales incorrectas devuelve nombre 000")
	void loginIncorrecto() throws Exception {
		when(repo.findByCorreoPass("x@x.com", "mala")).thenReturn(Optional.empty());

		mockMvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo_electronico\":\"x@x.com\",\"contrasena\":\"mala\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("000"));
	}

	@Test
	@DisplayName("UT-07 POST /updateUsuario guarda cambios y responde 1")
	void updateUsuario() throws Exception {
		Usuario u = ana();
		u.setRol("Administrador");

		mockMvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(u)))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).save(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-08 DELETE /deleteUsuario elimina y responde 1")
	void deleteUsuario() throws Exception {
		mockMvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(ana())))
				.andExpect(status().isOk())
				.andExpect(content().string("1"));

		verify(repo).delete(any(Usuario.class));
	}

	@Test
	@DisplayName("UT-09 POST /buscarUsuarios devuelve la lista completa")
	void listarUsuarios() throws Exception {
		Usuario otra = new Usuario(172, "Liliana Gómez Martínez", "liliana22512@cbtis75.edu.mx", "ada", "Estudiante", "#54EA01");
		when(repo.findAll()).thenReturn(Arrays.asList(ana(), otra));

		mockMvc.perform(post("/buscarUsuarios"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[1].id_usuario").value(172));
	}
}
