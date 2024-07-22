package com.espe.msvc.cursos.controllers;

import com.espe.msvc.cursos.models.Usuario;
import com.espe.msvc.cursos.models.entity.Curso;
import com.espe.msvc.cursos.models.entity.CursoUsuario;
import com.espe.msvc.cursos.services.CursoService;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping("/getCursos")
    public List<Curso> listar() {
        return service.listar();
    }

    @GetMapping("/findCurso/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> cursoOptional = service.porId(id);
        if (cursoOptional.isPresent()){
            return ResponseEntity.ok().body(cursoOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/saveCurso")
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }

        List<Curso> cursos = service.listar();
        boolean nombreExiste = cursos.stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(curso.getNombre()));

        if (nombreExiste) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "El nombre del curso ya existe."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(curso));
    }

    @DeleteMapping("/eliminarCurso/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Curso> o = service.porId(id);
        if(o.isPresent()){
            if(!o.get().getCursoUsuarios().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "El curso tiene usuarios asignados"));
            }
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("mensaje", "Curso eliminado"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "Curso no encontrado"));
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

    @PutMapping("/modificarCurso/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        // Verificar si el curso a modificar existe
        Optional<Curso> cursoExistente = service.porId(id);
        if (!cursoExistente.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener la lista de cursos y verificar si ya existe otro curso con el mismo nombre
        List<Curso> cursos = service.listar();
        boolean nombreExiste = cursos.stream()
                .anyMatch(c -> c.getId() != id && c.getNombre().equalsIgnoreCase(curso.getNombre()));

        if (nombreExiste) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "El nombre del curso ya existe."));
        }

        Curso cursoDB = cursoExistente.get();
        cursoDB.setNombre(curso.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDB));
    }


    @PutMapping("/eliminarUsuario/{idCurso}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long idCurso) {
        Boolean result;
        try {
            result = service.eliminarUsuario(usuario, idCurso);
            if (result) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(Collections.singletonMap("error", "Error al eliminar usuario del curso"));
        }
    }

    @PutMapping("/asignarUsuario/{idCurso}")
    public ResponseEntity<?> agregarUsuario(@RequestBody Usuario usuario, @PathVariable Long idCurso) {
        Boolean result;
        try {
            result = service.agregarUsuario(usuario, idCurso);
            if (result) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(Collections.singletonMap("error", "Error al agregar usuario al curso"));
        }
    }


    @GetMapping("/usuarioIds/{idCurso}")
    public List<Usuario> listarNoMatriculados(@PathVariable Long idCurso) {
        return service.obtenerUsuariosNoEnCurso(idCurso);
    }
}
