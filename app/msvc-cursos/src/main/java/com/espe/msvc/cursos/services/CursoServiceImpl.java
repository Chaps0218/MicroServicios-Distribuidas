package com.espe.msvc.cursos.services;

import com.espe.msvc.cursos.clients.UsuarioClientRest;
import com.espe.msvc.cursos.models.Usuario;
import com.espe.msvc.cursos.models.entity.CursoUsuario;
import com.espe.msvc.cursos.repositories.CursoUsuarioRepository;
import org.springframework.stereotype.Service;

import com.espe.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.espe.msvc.cursos.models.entity.Curso;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService{
    @Autowired
    private CursoRepository repository;

    @Autowired
    UsuarioClientRest usuarioClientRest;

    @Autowired
    private CursoUsuarioRepository cursoUsuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar(){
        return (List<Curso>) repository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }
    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
    @Override
    public Optional<Usuario> crearUsuario(Usuario usuario, Long idCurso) {
        Optional<Curso> o = repository.findById(idCurso);
        if (o.isPresent()) {
            Usuario usuarioMicro = usuarioClientRest.crear(usuario);

            Curso curso = o.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicro.getId());

            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
        }


        return Optional.empty();
    }

    @Override
    public Boolean agregarUsuario(Usuario usuario, Long idCurso) {
        Optional<Curso> o = repository.findById(idCurso);
        if (o.isPresent()) {
            Usuario usuarioMicro = usuarioClientRest.detalle(usuario.getId());
            Curso curso = o.get();

            boolean usuarioYaRegistrado = curso.getCursoUsuarios().stream()
                    .anyMatch(cu -> cu.getUsuarioId().equals(usuarioMicro.getId()));

            if (usuarioYaRegistrado) {
                return false;
            }

            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMicro.getId());

            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
            return true;
        }
        return false;
    }


    @Override
    public Boolean eliminarUsuario(Usuario usuario, Long idCurso) {
        Optional<Curso> o = repository.findById(idCurso);
        if (o.isPresent()) {
            Usuario usuarioMicro = usuarioClientRest.detalle(usuario.getId());
            Curso curso = o.get();
            CursoUsuario existentes = curso.getCursoUsuarios().stream()
                    .filter(cu -> cu.getUsuarioId().equals(usuarioMicro.getId()))
                    .findFirst()
                    .orElse(null);
            if (existentes != null) {
                curso.removeCursoUsuario(existentes);
                repository.save(curso);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Usuario> obtenerUsuariosNoEnCurso(Long cursoId) {
        List<Usuario> todosUsuarios = usuarioClientRest.obtenerTodosUsuarios();
        Optional<Curso> o = repository.findById(cursoId);

        if (o.isPresent()) {
            Curso curso = o.get();
            List<Long> idsEnCurso = curso.getCursoUsuarios().stream()
                    .map(CursoUsuario::getUsuarioId)
                    .collect(Collectors.toList());
            return todosUsuarios.stream()
                    .filter(usuario -> !idsEnCurso.contains(usuario.getId()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
