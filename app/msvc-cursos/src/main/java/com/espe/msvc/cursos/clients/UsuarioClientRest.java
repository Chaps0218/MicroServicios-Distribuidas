package com.espe.msvc.cursos.clients;
import com.espe.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "msvc-usuarios", url = "localhost:8001")
public interface UsuarioClientRest {
    @GetMapping("/findUser/{id}")
    Usuario detalle(@PathVariable Long id);
    @GetMapping("/getUsers")
    List<Usuario> obtenerTodosUsuarios();

    @PostMapping
    Usuario crear(@RequestBody Usuario usuario);

}

