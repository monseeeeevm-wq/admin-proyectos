package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Proyectos;

/** Casos de borde: validación del formulario de proyectos y errores al consultar un proyecto. */
class GestorControllerValidacionTest {

	private final GestorController controller = new GestorController();

	private Proyectos completo() {
		Proyectos p = new Proyectos();
		p.setNombre("My API Rest");
		p.setDescripcion("Proyecto de prueba");
		p.setFecha_inicio("2026-10-16");
		p.setFecha_finalizacion("2026-12-01");
		return p;
	}

	private void assertRechazado(Proyectos proyecto) {
		ModelAndView mav = controller.setSaveProyecto(proyecto);

		assertEquals("inicio", mav.getViewName());
		assertNotNull(mav.getModel().get("error"));
	}

	@Test
	@DisplayName("VAL-01 Formulario nulo se rechaza")
	void formularioNuloSeRechaza() {
		assertRechazado(null);
	}

	@Test
	@DisplayName("VAL-02 Sin nombre se rechaza")
	void sinNombreSeRechaza() {
		Proyectos p = completo();
		p.setNombre(null);
		assertRechazado(p);
	}

	@Test
	@DisplayName("VAL-03 Descripción en blanco se rechaza")
	void descripcionEnBlancoSeRechaza() {
		Proyectos p = completo();
		p.setDescripcion("   ");
		assertRechazado(p);
	}

	@Test
	@DisplayName("VAL-04 Fecha de inicio vacía se rechaza")
	void fechaInicioVaciaSeRechaza() {
		Proyectos p = completo();
		p.setFecha_inicio("");
		assertRechazado(p);
	}

	@Test
	@DisplayName("VAL-05 Fecha de finalización ausente se rechaza")
	void fechaFinalizacionAusenteSeRechaza() {
		Proyectos p = completo();
		p.setFecha_finalizacion(null);
		assertRechazado(p);
	}

	@Test
	@DisplayName("VAL-06 Actualizar proyecto: error de cliente distinto de 404 responde 502")
	void actualizarConErrorDeClienteResponde502() {
		Proyectos p = new Proyectos();
		p.setId_proyecto(197);
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.getForObject(anyString(), eq(Proyectos.class), anyMap()))
						.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))) {

			ResponseEntity<Object> respuesta = controller.updateProyecto(p);

			assertEquals(502, respuesta.getStatusCode().value());
		}
	}

	@Test
	@DisplayName("VAL-07 Actualizar proyecto: si el backend no responde (sin conexión) responde 502")
	void actualizarSinConexionResponde502() {
		Proyectos p = new Proyectos();
		p.setId_proyecto(197);
		try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
				(mock, ctx) -> when(mock.getForObject(anyString(), eq(Proyectos.class), anyMap()))
						.thenThrow(new ResourceAccessException("Sin conexión con el backend")))) {

			ResponseEntity<Object> respuesta = controller.updateProyecto(p);

			assertEquals(502, respuesta.getStatusCode().value());
		}
	}
}
