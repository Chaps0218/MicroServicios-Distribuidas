package com.espe.msvc.usuarios;
import com.espe.msvc.usuarios.models.entity.Usuario;
import com.espe.msvc.usuarios.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class MsvcUsuariosApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		usuarioRepository.deleteAll();
	}

	@Test
	public void testListarUsuarios() throws Exception {
		Usuario usuario1 = new Usuario();
		usuario1.setNombre("Usuario 1");
		usuario1.setEmail("usuario1@test.com");
		usuario1.setPassword("password1");
		usuarioRepository.save(usuario1);

		Usuario usuario2 = new Usuario();
		usuario2.setNombre("Usuario 2");
		usuario2.setEmail("usuario2@test.com");
		usuario2.setPassword("password2");
		usuarioRepository.save(usuario2);

		mockMvc.perform(get("/getUsers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].nombre", is("Usuario 1")))
				.andExpect(jsonPath("$[1].nombre", is("Usuario 2")));
	}

	@Test
	public void testCrearUsuario() throws Exception {
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombre("Nuevo Usuario");
		nuevoUsuario.setEmail("nuevo@test.com");
		nuevoUsuario.setPassword("password");

		mockMvc.perform(post("/saveUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(nuevoUsuario)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre", is("Nuevo Usuario")))
				.andExpect(jsonPath("$.email", is("nuevo@test.com")));
	}

	@Test
	public void testObtenerUsuarioPorId() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario Test");
		usuario.setEmail("test@test.com");
		usuario.setPassword("password");
		usuario = usuarioRepository.save(usuario);

		mockMvc.perform(get("/findUser/{id}", usuario.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre", is("Usuario Test")))
				.andExpect(jsonPath("$.email", is("test@test.com")));
	}

	@Test
	public void testActualizarUsuario() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario Original");
		usuario.setEmail("original@test.com");
		usuario.setPassword("password");
		usuario = usuarioRepository.save(usuario);

		Usuario usuarioActualizado = new Usuario();
		usuarioActualizado.setNombre("Usuario Actualizado");
		usuarioActualizado.setEmail("actualizado@test.com");
		usuarioActualizado.setPassword("newpassword");

		mockMvc.perform(put("/modificarUser/{id}", usuario.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usuarioActualizado)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre", is("Usuario Actualizado")))
				.andExpect(jsonPath("$.email", is("actualizado@test.com")));
	}

	@Test
	public void testEliminarUsuario() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setNombre("Usuario a Eliminar");
		usuario.setEmail("eliminar@test.com");
		usuario.setPassword("password");
		usuario = usuarioRepository.save(usuario);

		mockMvc.perform(delete("/eliminarUser/{id}", usuario.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/findUser/{id}", usuario.getId()))
				.andExpect(status().isNotFound());
	}
	// Prueba no válida
	@Test
	public void testCrearUsuarioConDatosInvalidos() throws Exception {
		Usuario usuarioInvalido = new Usuario();


		mockMvc.perform(post("/saveUser")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(usuarioInvalido)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.nombre", is("El campo nombre no debe estar vacío")))
				.andExpect(jsonPath("$.email", is("El campo email no debe estar vacío")));
	}

}
