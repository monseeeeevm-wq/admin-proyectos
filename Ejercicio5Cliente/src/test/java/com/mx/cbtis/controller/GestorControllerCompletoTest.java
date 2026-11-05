package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

/**
 * Pruebas unitarias del resto de métodos de GestorController. El backend se simula
 * interceptando la creación de RestTemplate, por lo que no requieren la API en ejecución.
 */
class GestorControllerCompletoTest {

	private static final String BASE = "http://localhost:8081";

	private final GestorController controller = new GestorController();

	private Usuario usuario() {
		return new Usuario(131, "Ana Paola", "ana@cbtis75.edu.mx", "0722", "Estudiante", "#70FAB7");
	}

	private Tareas tarea() {
		return new Tareas(199, "Base de datos", "Crear las tablas", "2024-06-13", "terminado", "danger", 197);
	}

	private Comentarios comentario() {
		return new Comentarios(201, "Perla", 199, "Subirlo ya", "2024-06-26 13:48:00");
	}

	// ------------------------------------------------------------------ vistas

	@Test
	@DisplayName("CTL-01 Vistas simples devuelven el nombre de la plantilla")
	void vistasSimples() {
		assertEquals("home", controller.getHome());
		assertEquals("tutorial", controller.getTutorial());
		assertEquals("manual", controller.getManual());
		assertEquals("login", controller.getLogin());
	}

	@Test
	@DisplayName("CTL-02 Registro: la vista recibe un usuario vacío")
	void registroAgregaUsuarioVacio() {
		Model modelo = new ConcurrentModel();

		assertEquals("register", controller.getRegister(modelo));
		assertTrue(modelo.getAttribute("usuario") instanceof Usuario);
	}

	@Test
	@DisplayName("CTL-03 Inicio: la vista recibe un proyecto vacío para el formulario")
	void inicioAgregaProyectoVacio() {
		Model modelo = new ConcurrentModel();

		assertEquals("inicio", controller.getInicio(modelo));
		assertTrue(modelo.getAttribute("proyectos") instanceof Proyectos);
	}

	@Test
	@DisplayName("CTL-04 Proyecto: la vista recibe tarea, comentario y lista de usuarios")
	void proyectoCargaLosDatosDeLaVista() {
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Usuario[].class)))
						.thenReturn(new Usuario[] { usuario() }))) {

			assertEquals("proyecto", controller.getProyecto(modelo));

			assertTrue(modelo.getAttribute("tarea") instanceof Tareas);
			assertTrue(modelo.getAttribute("comentarios") instanceof Comentarios);
			assertEquals(1, ((List<?>) modelo.getAttribute("listaUsuarios")).size());
			verify(mocked.constructed().get(0)).postForObject(eq(BASE + "/buscarUsuarios"), any(), eq(Usuario[].class));
		}
	}

	@Test
	@DisplayName("CTL-05 Tarea: la vista recibe una tarea vacía")
	void tareaAgregaTareaVacia() {
		Model modelo = new ConcurrentModel();

		assertEquals("proyecto", controller.getTarea(modelo));
		assertTrue(modelo.getAttribute("tareas") instanceof Tareas);
	}

	// ------------------------------------------------------------------ usuarios

	@Test
	@DisplayName("CTL-06 Guardar usuario: envía al backend y redirige al login")
	void guardarUsuarioRedirigeAlLogin() {
		Usuario usuario = usuario();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Integer.class)))
						.thenReturn(ResponseEntity.ok(1)))) {

			ModelAndView mav = controller.setSaveUsuario(usuario);

			assertEquals("redirect:/login", mav.getViewName());
			verify(mocked.constructed().get(0)).postForEntity(BASE + "/addUsuario", usuario, Integer.class);
		}
	}

	@Test
	@DisplayName("CTL-07 Login válido: devuelve el usuario que responde el backend")
	void loginValidoDevuelveElUsuario() {
		Usuario entrada = new Usuario();
		entrada.setCorreo_electronico("ana@cbtis75.edu.mx");
		entrada.setContrasena("0722");
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Usuario.class)))
						.thenReturn(ResponseEntity.ok(usuario())))) {

			ResponseEntity<Usuario> respuesta = controller.verificarUsuario(entrada);

			assertEquals(200, respuesta.getStatusCode().value());
			assertEquals(Integer.valueOf(131), respuesta.getBody().getId_usuario());
			verify(mocked.constructed().get(0)).postForEntity(BASE + "/getUsuarioLogin", entrada, Usuario.class);
		}
	}

	@Test
	@DisplayName("CTL-08 Login inválido: el backend responde nombre 000 y se devuelve tal cual")
	void loginInvalidoDevuelveNombre000() {
		Usuario entrada = new Usuario();
		entrada.setCorreo_electronico("nadie@cbtis75.edu.mx");
		Usuario noExiste = new Usuario();
		noExiste.setNombre("000");
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Usuario.class)))
						.thenReturn(ResponseEntity.ok(noExiste)))) {

			ResponseEntity<Usuario> respuesta = controller.verificarUsuario(entrada);

			assertEquals("000", respuesta.getBody().getNombre());
		}
	}

	@Test
	@DisplayName("CTL-09 Consultar usuarios: devuelve la lista y la deja en el modelo")
	void consultarUsuariosDevuelveLaLista() {
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Usuario[].class)))
						.thenReturn(new Usuario[] { usuario() }))) {

			ResponseEntity<Object> respuesta = controller.consultarUsuarios(modelo);

			assertEquals(200, respuesta.getStatusCode().value());
			assertEquals(1, ((List<?>) respuesta.getBody()).size());
			assertNotNull(modelo.getAttribute("listaUsuarios"));
		}
	}

	// ------------------------------------------------------------------ proyectos

	@Test
	@DisplayName("CTL-10 Eliminar proyecto: envía DELETE al backend y redirige al inicio")
	void eliminarProyectoRedirigeAlInicio() {
		Proyectos proyecto = new Proyectos();
		proyecto.setId_proyecto(197);
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
						eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {

			ModelAndView mav = controller.eliminarProyecto(proyecto);

			assertEquals("redirect:/inicio", mav.getViewName());
			verify(mocked.constructed().get(0)).exchange(eq(BASE + "/deleteProyecto"), eq(HttpMethod.DELETE),
					any(HttpEntity.class), eq(Integer.class));
		}
	}

	// ------------------------------------------------------------------ tareas

	@Test
	@DisplayName("CTL-11 Obtener tareas de un proyecto: devuelve la lista y la deja en el modelo")
	void obtenerTareasDevuelveLaLista() {
		Tareas filtro = new Tareas();
		filtro.setId_proyecto(197);
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Tareas[].class)))
						.thenReturn(new Tareas[] { tarea() }))) {

			ResponseEntity<List<Tareas>> respuesta = controller.obtenerTareas(filtro, modelo);

			assertEquals(1, respuesta.getBody().size());
			assertNotNull(modelo.getAttribute("tareas"));
			verify(mocked.constructed().get(0)).postForObject(BASE + "/getTarea", filtro, Tareas[].class);
		}
	}

	@Test
	@DisplayName("CTL-12 Guardar tarea: envía al backend y redirige al proyecto")
	void guardarTareaRedirigeAlProyecto() {
		Tareas tarea = tarea();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Integer.class)))
						.thenReturn(ResponseEntity.ok(1)))) {

			ModelAndView mav = controller.setSaveTareas(tarea);

			assertEquals("redirect:/proyecto", mav.getViewName());
			assertEquals(Integer.valueOf(197), mav.getModel().get("id_proyecto"));
			verify(mocked.constructed().get(0)).postForEntity(BASE + "/addTareas", tarea, Integer.class);
		}
	}

	@Test
	@DisplayName("CTL-13 Actualizar tarea: consulta la tarea al backend por su id")
	void actualizarTareaConsultaLaTarea() {
		Tareas pedida = new Tareas();
		pedida.setId_tarea(199);
		Tareas encontrada = tarea();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.getForObject(anyString(), eq(Tareas.class), anyMap()))
						.thenReturn(encontrada))) {

			ResponseEntity<Object> respuesta = controller.updateTarea(pedida);

			assertEquals(200, respuesta.getStatusCode().value());
			assertSame(encontrada, respuesta.getBody());
		}
	}

	@Test
	@DisplayName("CTL-14 Eliminar tarea: envía DELETE al backend y redirige al proyecto")
	void eliminarTareaRedirigeAlProyecto() {
		Tareas tarea = tarea();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
						eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {

			ModelAndView mav = controller.eliminarTarea(tarea);

			assertEquals("redirect:/proyecto", mav.getViewName());
			verify(mocked.constructed().get(0)).exchange(eq(BASE + "/deleteTareas"), eq(HttpMethod.DELETE),
					any(HttpEntity.class), eq(Integer.class));
		}
	}

	@Test
	@DisplayName("CTL-15 Verificar tarea: reenvía la tarea al backend y devuelve su respuesta")
	void verificarTareaDevuelveLaRespuestaDelBackend() {
		Tareas tarea = tarea();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Integer.class)))
						.thenReturn(ResponseEntity.ok(1)))) {

			ResponseEntity<Integer> respuesta = controller.verificarTarea(tarea);

			assertEquals(Integer.valueOf(1), respuesta.getBody());
			verify(mocked.constructed().get(0)).postForEntity(BASE + "/addTareas", tarea, Integer.class);
		}
	}

	@Test
	@DisplayName("CTL-16 Cambios de tareas: reenvía la tarea al backend y devuelve su respuesta")
	void cambiosTareasDevuelveLaRespuestaDelBackend() {
		Tareas tarea = tarea();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Integer.class)))
						.thenReturn(ResponseEntity.ok(1)))) {

			ResponseEntity<Integer> respuesta = controller.cambiosTareas(tarea);

			assertEquals(Integer.valueOf(1), respuesta.getBody());
			verify(mocked.constructed().get(0)).postForEntity(BASE + "/addTareas", tarea, Integer.class);
		}
	}

	@Test
	@DisplayName("CTL-17 Consultar tareas: devuelve la lista completa")
	void consultarTareasDevuelveLaLista() {
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Tareas[].class)))
						.thenReturn(new Tareas[] { tarea(), tarea() }))) {

			ResponseEntity<Object> respuesta = controller.consultarTareas();

			assertEquals(200, respuesta.getStatusCode().value());
			assertEquals(2, ((List<?>) respuesta.getBody()).size());
		}
	}

	// ------------------------------------------------------------------ comentarios

	@Test
	@DisplayName("CTL-18 Obtener comentarios de una tarea: devuelve la lista y la deja en el modelo")
	void obtenerComentariosDevuelveLaLista() {
		Comentarios filtro = new Comentarios();
		filtro.setId_tarea(199);
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Comentarios[].class)))
						.thenReturn(new Comentarios[] { comentario() }))) {

			ResponseEntity<List<Comentarios>> respuesta = controller.obtenerComentarios(filtro, modelo);

			assertEquals(1, respuesta.getBody().size());
			assertNotNull(modelo.getAttribute("comentarios"));
			verify(mocked.constructed().get(0)).postForObject(BASE + "/getComentario", filtro, Comentarios[].class);
		}
	}

	@Test
	@DisplayName("CTL-19 Guardar comentario: envía al backend y devuelve la vista del proyecto")
	void guardarComentarioDevuelveLaVistaDelProyecto() {
		Comentarios comentario = comentario();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Integer.class)))
						.thenReturn(ResponseEntity.ok(1)))) {

			assertEquals("proyecto", controller.setSaveComentario(comentario));

			verify(mocked.constructed().get(0)).postForEntity(BASE + "/addComentario", comentario, Integer.class);
		}
	}

	@Test
	@DisplayName("CTL-20 Consultar comentarios: devuelve la lista completa")
	void consultarComentariosDevuelveLaLista() {
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Comentarios[].class)))
						.thenReturn(new Comentarios[] { comentario() }))) {

			ResponseEntity<Object> respuesta = controller.consultarComentarios();

			assertEquals(200, respuesta.getStatusCode().value());
			assertEquals(1, ((List<?>) respuesta.getBody()).size());
		}
	}
}
