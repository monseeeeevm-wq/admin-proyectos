package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Proyectos;

class GestorControllerAutoTest {

	private final GestorController controller = new GestorController();

	private Proyectos proyectoValido() {
		Proyectos p = new Proyectos();
		p.setId_proyecto(197);
		p.setNombre("My API Rest");
		p.setDescripcion("Proyecto de prueba");
		p.setFecha_inicio("2026-10-16");
		p.setFecha_finalizacion("2026-12-01");
		p.setEstado_proyecto("En proceso");
		p.setFondo("/Images/qa.gif");
		return p;
	}

	// ------------------------- CASOS DE ÉXITO -------------------------

	@Test
	@DisplayName("AUT-E01 Servicio de consulta: el backend devuelve información válida y se procesa")
	void aut_e01_servicioConsultaProcesaRespuestaValida() {
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Proyectos[].class)))
						.thenReturn(new Proyectos[] { proyectoValido() }))) {

			ResponseEntity<Object> respuesta = controller.consultarProyectos(modelo);

			assertEquals(200, respuesta.getStatusCode().value());
			assertEquals(1, ((List<?>) respuesta.getBody()).size());
		}
	}

	@Test
	@DisplayName("AUT-E02 Formulario: con datos válidos la validación permite enviar la información")
	void aut_e02_formularioDatosValidosPermiteEnviar() {
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForEntity(anyString(), any(), eq(Integer.class)))
						.thenReturn(ResponseEntity.ok(1)))) {

			ModelAndView mav = controller.setSaveProyecto(proyectoValido());

			assertEquals("redirect:/inicio", mav.getViewName());
			verify(mocked.constructed().get(0)).postForEntity(anyString(), any(), eq(Integer.class));
		}
	}

	@Test
	@DisplayName("AUT-E03 Componente / Vista: tras la consulta, los datos quedan disponibles para la interfaz")
	@SuppressWarnings("unchecked")
	void aut_e03_vistaRecibeLosDatosConsultados() {
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Proyectos[].class)))
						.thenReturn(new Proyectos[] { proyectoValido() }))) {

			controller.consultarProyectos(modelo);

			List<Proyectos> lista = (List<Proyectos>) modelo.getAttribute("listaProyectos");
			assertNotNull(lista);
			assertEquals(1, lista.size());
			assertEquals("My API Rest", lista.get(0).getNombre());
		}
	}

	// ------------------------- CASOS DE FRACASO -------------------------

	@Test
	@DisplayName("AUT-F01 Servicio de consulta: si el backend devuelve un error, se captura y se controla")
	void aut_f01_servicioConsultaControlaErrorDelBackend() {
		Model modelo = new ConcurrentModel();
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.postForObject(anyString(), any(), eq(Proyectos[].class)))
						.thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))) {

			ResponseEntity<Object> respuesta = assertDoesNotThrow(() -> controller.consultarProyectos(modelo));

			assertTrue(respuesta.getStatusCode().is5xxServerError());
			assertNotNull(respuesta.getBody());
		}
	}

	@Test
	@DisplayName("AUT-F02 Formulario: con datos incompletos la validación impide el envío y muestra el error")
	void aut_f02_formularioDatosIncompletosNoSeEnvia() {
		Proyectos incompleto = new Proyectos();
		incompleto.setNombre("");
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class)) {

			ModelAndView mav = controller.setSaveProyecto(incompleto);

			assertEquals("inicio", mav.getViewName());
			assertNotNull(mav.getModel().get("error"));
			for (RestTemplate rt : mocked.constructed()) {
				verify(rt, never()).postForEntity(anyString(), any(), eq(Integer.class));
			}
		}
	}

	@Test
	@DisplayName("AUT-F03 Componente / Vista: si el recurso no existe, se mantiene un estado controlado (404)")
	void aut_f03_recursoInexistenteDevuelve404Controlado() {
		Proyectos inexistente = new Proyectos();
		inexistente.setId_proyecto(999999);
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.getForObject(anyString(), eq(Proyectos.class), anyMap()))
						.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND)))) {

			ResponseEntity<Object> respuesta = assertDoesNotThrow(() -> controller.updateProyecto(inexistente));

			assertEquals(404, respuesta.getStatusCode().value());
		}
	}
}