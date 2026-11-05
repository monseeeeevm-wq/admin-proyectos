package com.mx.cbtis.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModelosTest {

	@Test
	@DisplayName("MOD-01 Usuario: constructores, getters y setters")
	void usuario() {
		Usuario vacio = new Usuario();
		assertNull(vacio.getId_usuario());
		assertNull(vacio.getNombre());
		assertNull(vacio.getCorreo_electronico());
		assertNull(vacio.getContrasena());
		assertNull(vacio.getRol());
		assertNull(vacio.getImagen_usuario());

		Usuario obj = new Usuario(131, "Ana Paola", "ana@cbtis75.edu.mx", "0722", "Estudiante", "#70FAB7");
		assertEquals(Integer.valueOf(131), obj.getId_usuario());
		assertEquals("Ana Paola", obj.getNombre());
		assertEquals("ana@cbtis75.edu.mx", obj.getCorreo_electronico());
		assertEquals("0722", obj.getContrasena());
		assertEquals("Estudiante", obj.getRol());
		assertEquals("#70FAB7", obj.getImagen_usuario());

		vacio.setId_usuario(132);
		vacio.setNombre("Liliana");
		vacio.setCorreo_electronico("lili@cbtis75.edu.mx");
		vacio.setContrasena("ada");
		vacio.setRol("Administrador");
		vacio.setImagen_usuario("#54EA01");
		assertEquals(Integer.valueOf(132), vacio.getId_usuario());
		assertEquals("Liliana", vacio.getNombre());
		assertEquals("lili@cbtis75.edu.mx", vacio.getCorreo_electronico());
		assertEquals("ada", vacio.getContrasena());
		assertEquals("Administrador", vacio.getRol());
		assertEquals("#54EA01", vacio.getImagen_usuario());
	}

	@Test
	@DisplayName("MOD-02 Proyectos: constructores, getters y setters")
	void proyectos() {
		Proyectos vacio = new Proyectos();
		assertNull(vacio.getId_proyecto());
		assertNull(vacio.getNombre());
		assertNull(vacio.getDescripcion());
		assertNull(vacio.getFecha_inicio());
		assertNull(vacio.getFecha_finalizacion());
		assertNull(vacio.getEstado_proyecto());
		assertNull(vacio.getFondo());

		Proyectos obj = new Proyectos(197, "My API Rest", "Desarrollo de un API", "2024-05-31", "2024-06-29", "En proceso", "/Images/animacion1.gif");
		assertEquals(Integer.valueOf(197), obj.getId_proyecto());
		assertEquals("My API Rest", obj.getNombre());
		assertEquals("Desarrollo de un API", obj.getDescripcion());
		assertEquals("2024-05-31", obj.getFecha_inicio());
		assertEquals("2024-06-29", obj.getFecha_finalizacion());
		assertEquals("En proceso", obj.getEstado_proyecto());
		assertEquals("/Images/animacion1.gif", obj.getFondo());

		vacio.setId_proyecto(198);
		vacio.setNombre("Otro proyecto");
		vacio.setDescripcion("Otra descripcion");
		vacio.setFecha_inicio("2026-10-16");
		vacio.setFecha_finalizacion("2026-12-01");
		vacio.setEstado_proyecto("Terminado");
		vacio.setFondo("/Images/qa.gif");
		assertEquals(Integer.valueOf(198), vacio.getId_proyecto());
		assertEquals("Otro proyecto", vacio.getNombre());
		assertEquals("Otra descripcion", vacio.getDescripcion());
		assertEquals("2026-10-16", vacio.getFecha_inicio());
		assertEquals("2026-12-01", vacio.getFecha_finalizacion());
		assertEquals("Terminado", vacio.getEstado_proyecto());
		assertEquals("/Images/qa.gif", vacio.getFondo());
	}

	@Test
	@DisplayName("MOD-03 Tareas: constructores, getters y setters")
	void tareas() {
		Tareas vacio = new Tareas();
		assertNull(vacio.getId_tarea());
		assertNull(vacio.getNombre());
		assertNull(vacio.getDescripcion());
		assertNull(vacio.getFecha_vencimiento());
		assertNull(vacio.getLista());
		assertNull(vacio.getEstado());
		assertNull(vacio.getId_proyecto());

		Tareas obj = new Tareas(199, "Base de datos", "Crear las tablas", "2024-06-13", "terminado", "danger", 197);
		assertEquals(Integer.valueOf(199), obj.getId_tarea());
		assertEquals("Base de datos", obj.getNombre());
		assertEquals("Crear las tablas", obj.getDescripcion());
		assertEquals("2024-06-13", obj.getFecha_vencimiento());
		assertEquals("terminado", obj.getLista());
		assertEquals("danger", obj.getEstado());
		assertEquals(Integer.valueOf(197), obj.getId_proyecto());

		vacio.setId_tarea(200);
		vacio.setNombre("Pruebas");
		vacio.setDescripcion("Escribir pruebas");
		vacio.setFecha_vencimiento("2026-11-01");
		vacio.setLista("pendiente");
		vacio.setEstado("warning");
		vacio.setId_proyecto(198);
		assertEquals(Integer.valueOf(200), vacio.getId_tarea());
		assertEquals("Pruebas", vacio.getNombre());
		assertEquals("Escribir pruebas", vacio.getDescripcion());
		assertEquals("2026-11-01", vacio.getFecha_vencimiento());
		assertEquals("pendiente", vacio.getLista());
		assertEquals("warning", vacio.getEstado());
		assertEquals(Integer.valueOf(198), vacio.getId_proyecto());
	}

	@Test
	@DisplayName("MOD-04 Comentarios: constructores, getters y setters")
	void comentarios() {
		Comentarios vacio = new Comentarios();
		assertNull(vacio.getId_comentario());
		assertNull(vacio.getId_usuario());
		assertNull(vacio.getId_tarea());
		assertNull(vacio.getTexto_comentario());
		assertNull(vacio.getFecha_hora());

		Comentarios obj = new Comentarios(201, "Perla", 199, "Subirlo ya", "2024-06-26 13:48:00");
		assertEquals(Integer.valueOf(201), obj.getId_comentario());
		assertEquals("Perla", obj.getId_usuario());
		assertEquals(Integer.valueOf(199), obj.getId_tarea());
		assertEquals("Subirlo ya", obj.getTexto_comentario());
		assertEquals("2024-06-26 13:48:00", obj.getFecha_hora());

		vacio.setId_comentario(202);
		vacio.setId_usuario("Liliana");
		vacio.setId_tarea(200);
		vacio.setTexto_comentario("hola");
		vacio.setFecha_hora("2024-06-26 13:49:00");
		assertEquals(Integer.valueOf(202), vacio.getId_comentario());
		assertEquals("Liliana", vacio.getId_usuario());
		assertEquals(Integer.valueOf(200), vacio.getId_tarea());
		assertEquals("hola", vacio.getTexto_comentario());
		assertEquals("2024-06-26 13:49:00", vacio.getFecha_hora());
	}

	@Test
	@DisplayName("MOD-05 Asignaciones: constructores, getters y setters")
	void asignaciones() {
		Asignaciones vacio = new Asignaciones();
		assertNull(vacio.getId_asignacion());
		assertNull(vacio.getId_usuario());
		assertNull(vacio.getId_tarea());
		assertNull(vacio.getFecha_asignacion());

		Asignaciones obj = new Asignaciones(1, 131, 199, "2024-06-20");
		assertEquals(Integer.valueOf(1), obj.getId_asignacion());
		assertEquals(Integer.valueOf(131), obj.getId_usuario());
		assertEquals(Integer.valueOf(199), obj.getId_tarea());
		assertEquals("2024-06-20", obj.getFecha_asignacion());

		vacio.setId_asignacion(2);
		vacio.setId_usuario(172);
		vacio.setId_tarea(200);
		vacio.setFecha_asignacion("2024-06-25");
		assertEquals(Integer.valueOf(2), vacio.getId_asignacion());
		assertEquals(Integer.valueOf(172), vacio.getId_usuario());
		assertEquals(Integer.valueOf(200), vacio.getId_tarea());
		assertEquals("2024-06-25", vacio.getFecha_asignacion());
	}
}
