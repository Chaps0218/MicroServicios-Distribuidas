package com.espe.msvc.cursos.repositories;

import com.espe.msvc.cursos.models.entity.CursoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoUsuarioRepository extends JpaRepository<CursoUsuario, Long> {
    List<CursoUsuario> findAll();
}
