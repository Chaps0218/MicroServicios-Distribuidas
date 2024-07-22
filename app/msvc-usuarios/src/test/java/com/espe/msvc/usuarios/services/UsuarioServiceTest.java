package com.espe.msvc.usuarios.services;

import com.espe.msvc.usuarios.clients.CursoClientRest;
import com.espe.msvc.usuarios.models.Curso;
import com.espe.msvc.usuarios.models.CursoUsuario;
import com.espe.msvc.usuarios.models.entity.Usuario;
import com.espe.msvc.usuarios.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private CursoClientRest cursoClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test listar usuarios")
    void testListar() {
    //dada la siguiente lista de usuarios
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(), new Usuario(), new Usuario()
        );
        when(repository.findAll()).thenReturn(usuarios);
//cuando

        List<Usuario> result = usuarioService.listar();

      //entonces
        assertEquals(3, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test buscar usuario por ID")
    void testPorId() {

        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(usuario));


        Optional<Usuario> result = usuarioService.porId(id);


        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test guardar usuario")
    void testGuardar() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);


        Usuario result = usuarioService.guardar(usuario);


        assertNotNull(result);
        assertEquals("Test User", result.getNombre());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Test eliminar usuario")
    void testEliminar() {

        Long id = 1L;


        usuarioService.eliminar(id);


        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Test puede eliminar usuario - sin cursos")
    void testPuedeEliminarSinCursos() {

        Long id = 1L;
        when(cursoClient.getCursos()).thenReturn(Arrays.asList());


        boolean result = usuarioService.puedeEliminar(id);


        assertTrue(result);
        verify(cursoClient, times(1)).getCursos();
    }

    @Test
    @DisplayName("Test puede eliminar usuario - con cursos, pero no asignado")
    void testPuedeEliminarConCursosNoAsignado() {

        Long id = 1L;
        Curso curso = new Curso();
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(2L);
        curso.setCursoUsuarios(Arrays.asList(cursoUsuario));
        when(cursoClient.getCursos()).thenReturn(Arrays.asList(curso));


        boolean result = usuarioService.puedeEliminar(id);


        assertTrue(result);
        verify(cursoClient, times(1)).getCursos();
    }

    @Test
    @DisplayName("Test puede eliminar usuario - con cursos asignados")
    void testPuedeEliminarConCursosAsignados() {

        Long id = 1L;
        Curso curso = new Curso();
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(id);
        curso.setCursoUsuarios(Arrays.asList(cursoUsuario));
        when(cursoClient.getCursos()).thenReturn(Arrays.asList(curso));


        boolean result = usuarioService.puedeEliminar(id);


        assertFalse(result);
        verify(cursoClient, times(1)).getCursos();
    }
}