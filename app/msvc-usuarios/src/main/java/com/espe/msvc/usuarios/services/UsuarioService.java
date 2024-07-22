package com.espe.msvc.usuarios.services;
import com.espe.msvc.usuarios.models.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listar();
    Optional<Usuario> porId(long id);
    Object guardar(Usuario usuario);
    void eliminar(Long id);
    boolean puedeEliminar(Long id);
}
