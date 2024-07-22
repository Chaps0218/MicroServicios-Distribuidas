package com.espe.msvc.cursos.services;

import com.espe.msvc.cursos.clients.UsuarioClientRest;
import com.espe.msvc.cursos.models.Usuario;
import com.espe.msvc.cursos.models.entity.Curso;
import com.espe.msvc.cursos.models.entity.CursoUsuario;
import com.espe.msvc.cursos.repositories.CursoRepository;
import com.espe.msvc.cursos.repositories.CursoUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
@SpringBootTest
class CursoServiceTest {
    @Autowired
    private CursoService cursoService;
    @MockBean
    private CursoRepository cursoRepository;
    @MockBean
    private UsuarioClientRest usuarioClientRest;

    @MockBean
    private CursoUsuarioRepository cursoUsuarioRepository;
    @BeforeEach
    void setUp() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNombre("Curso de prueba");

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));
    }
    @Test
    @DisplayName("Cuando se busca un curso por su id")
    public void encontrarPorId(){
        long valorId = 1L;
        Optional<Curso> curso = cursoService.porId(valorId);
        if (curso.isPresent()) {
            assertEquals(valorId, curso.get().getId());
            System.out.println("curso.get() = " + curso.get().getNombre());
        }
    }

    @Test
    @DisplayName("Cuando se guarda un nuevo curso")
    public void guardarCurso() {
        Curso nuevoCurso = new Curso();
        nuevoCurso.setNombre("Nuevo Curso");

        when(cursoRepository.save(nuevoCurso)).thenReturn(nuevoCurso);

        Curso cursoGuardado = cursoService.guardar(nuevoCurso);
        assertNotNull(cursoGuardado);
        assertEquals("Nuevo Curso", cursoGuardado.getNombre());
    }

    @Test
    @DisplayName("Cuando se elimina un curso por su id")
    public void eliminarCurso() {
        long idCurso = 1L;

        cursoService.eliminar(idCurso);

        Mockito.verify(cursoRepository, Mockito.times(1)).deleteById(idCurso);
    }

    @Test
    @DisplayName("Cuando se listan todos los cursos")
    public void listarCursos() {
        List<Curso> cursosMock = Arrays.asList(
                new Curso(), new Curso(), new Curso()
        );
        when(cursoRepository.findAll()).thenReturn(cursosMock);

        List<Curso> cursosResultado = cursoService.listar();

        assertEquals(3, cursosResultado.size());
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Cuando se agrega un usuario a un curso")
    public void agregarUsuarioACurso() {
        Long idCurso = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Curso curso = new Curso();
        curso.setId(idCurso);

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(usuarioClientRest.detalle(usuario.getId())).thenReturn(usuario);
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Boolean resultado = cursoService.agregarUsuario(usuario, idCurso);

        assertTrue(resultado);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Cuando se elimina un usuario de un curso")
    public void eliminarUsuarioDeCurso() {
        Long idCurso = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Curso curso = new Curso();
        curso.setId(idCurso);
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(usuario.getId());
        curso.addCursoUsuario(cursoUsuario);

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(usuarioClientRest.detalle(usuario.getId())).thenReturn(usuario);
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Boolean resultado = cursoService.eliminarUsuario(usuario, idCurso);

        assertTrue(resultado);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Cuando se obtienen usuarios no matriculados en un curso")
    public void obtenerUsuariosNoEnCurso() {
        Long idCurso = 1L;
        Curso curso = new Curso();
        curso.setId(idCurso);
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(1L);
        curso.addCursoUsuario(cursoUsuario);

        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);

        List<Usuario> todosUsuarios = Arrays.asList(usuario1, usuario2);

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(usuarioClientRest.obtenerTodosUsuarios()).thenReturn(todosUsuarios);

        List<Usuario> resultado = cursoService.obtenerUsuariosNoEnCurso(idCurso);

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("Cuando se crea un usuario y se agrega a un curso")
    public void crearUsuarioYAgregarACurso() {
        Long idCurso = 1L;
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(1L);

        Curso curso = new Curso();
        curso.setId(idCurso);

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(usuarioClientRest.crear(any(Usuario.class))).thenReturn(nuevoUsuario);
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Optional<Usuario> resultado = cursoService.crearUsuario(nuevoUsuario, idCurso);

        assertTrue(resultado.isEmpty());  // The method returns Optional.empty()
        verify(cursoRepository, times(1)).save(any(Curso.class));
        verify(usuarioClientRest, times(1)).crear(any(Usuario.class));
    }
    }