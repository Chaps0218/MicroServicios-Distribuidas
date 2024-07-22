package com.espe.msvc.cursos;

import com.espe.msvc.cursos.models.Usuario;
import com.espe.msvc.cursos.services.CursoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.espe.msvc.cursos.models.entity.Curso;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MsvcCursosApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CursoService cursoService;
	@Test
	void contextLoads() {
	}
	@Test
	public void llamadaCursos() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getCursos")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

	}

	@Test
	public void crearCurso() throws Exception {
		Curso nuevoCurso = new Curso();
		nuevoCurso.setNombre("Curso de Prueba");

		when(cursoService.guardar(any(Curso.class))).thenReturn(nuevoCurso);

		mockMvc.perform(MockMvcRequestBuilders.post("/saveCurso")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(nuevoCurso)))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}
	@Test
	public void obtenerCursoPorId() throws Exception {
		Curso curso = new Curso();
		curso.setId(1L);
		curso.setNombre("Curso de Prueba");

		when(cursoService.porId(1L)).thenReturn(Optional.of(curso));

		mockMvc.perform(MockMvcRequestBuilders.get("/findCurso/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Curso de Prueba"));
	}

	@Test
	public void eliminarCurso() throws Exception {
		Curso curso = new Curso();
		curso.setId(1L);

		when(cursoService.porId(1L)).thenReturn(Optional.of(curso));

		mockMvc.perform(MockMvcRequestBuilders.delete("/eliminarCurso/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void modificarCurso() throws Exception {
		Curso cursoExistente = new Curso();
		cursoExistente.setId(1L);
		cursoExistente.setNombre("Curso Original");

		Curso cursoModificado = new Curso();
		cursoModificado.setId(1L);
		cursoModificado.setNombre("Curso Modificado");

		when(cursoService.porId(1L)).thenReturn(Optional.of(cursoExistente));
		when(cursoService.guardar(any(Curso.class))).thenReturn(cursoModificado);

		mockMvc.perform(MockMvcRequestBuilders.put("/modificarCurso/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(cursoModificado)))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Curso Modificado"));
	}

	@Test
	public void eliminarUsuarioDeCurso() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setId(1L);

		when(cursoService.eliminarUsuario(any(Usuario.class), anyLong())).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.put("/eliminarUsuario/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usuario)))
				.andExpect(status().isOk());
	}

	@Test
	public void asignarUsuarioACurso() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setId(1L);

		when(cursoService.agregarUsuario(any(Usuario.class), anyLong())).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.put("/asignarUsuario/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usuario)))
				.andExpect(status().isOk());
	}

	@Test
	public void listarUsuariosNoMatriculados() throws Exception {
		when(cursoService.obtenerUsuariosNoEnCurso(anyLong())).thenReturn(Arrays.asList(new Usuario(), new Usuario()));

		mockMvc.perform(MockMvcRequestBuilders.get("/usuarioIds/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}
}
