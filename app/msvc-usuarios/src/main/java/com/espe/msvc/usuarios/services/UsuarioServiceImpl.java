package com.espe.msvc.usuarios.services;

import com.espe.msvc.usuarios.clients.CursoClientRest;
import com.espe.msvc.usuarios.models.Curso;
import com.espe.msvc.usuarios.models.CursoUsuario;
import com.espe.msvc.usuarios.models.entity.Usuario;
import com.espe.msvc.usuarios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService{
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private CursoClientRest cursoClient;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return (List<Usuario>) repository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> porId(long id){
        return repository.findById(id);
    }

   @Override
   @Transactional
   public Usuario guardar(Usuario usuario) {
        return (Usuario) repository.save(usuario);
   }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean puedeEliminar(Long id) {
        List<Curso> cursos = cursoClient.getCursos();
        for (Curso curso : cursos) {
            for (CursoUsuario cursoUsuario : curso.getCursoUsuarios()) {
                if (cursoUsuario.getUsuarioId().equals(id)) {
                    return false;
                }
            }
        }
        return true;
    }

}
