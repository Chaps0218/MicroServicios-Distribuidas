package com.espe.msvc.cursos.services;
import com.espe.msvc.cursos.models.Usuario;
import com.espe.msvc.cursos.models.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<Curso> listar();

    Optional<Curso> porId(Long id);

    Curso guardar(Curso curso);

    void eliminar(Long id);


    Boolean agregarUsuario(Usuario usuario, Long idCurso);
    Optional<Usuario> crearUsuario(Usuario usuario, Long idCurso);
    Boolean eliminarUsuario(Usuario usuario, Long idCurso);


    List<Usuario> obtenerUsuariosNoEnCurso(Long idCurso);
}
