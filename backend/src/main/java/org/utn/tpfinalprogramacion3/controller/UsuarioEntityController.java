package org.utn.tpfinalprogramacion3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.UsuarioAdminDTO;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCreateDTO;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.services.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class UsuarioEntityController {

    private final UsuarioService usuarioService;

    public UsuarioEntityController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado con éxito", content = @Content(schema = @Schema(implementation = UsuarioCreateDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    @PostMapping("/crear")
    public ResponseEntity<UsuarioCreateDTO> createUsuario(@RequestBody UsuarioCreateDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.createUsuario(usuarioDTO), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar todos los usuarios activos",
            description = "Obtiene una página de usuarios activos registrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
            }
    )
    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<UsuarioCreateDTO>> listarUsuarios(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(usuarioService.findAll(numeroPagina), HttpStatus.OK);
    }

    @Operation(
            summary = "Listar usuarios inactivos",
            description = "Devuelve una página con los usuarios que fueron dados de baja.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuarios inactivos listados correctamente")
            }
    )
    @GetMapping("/listar/inactivos/{numeroPagina}")
    public ResponseEntity<Page<UsuarioCreateDTO>> listarUsuariosInactivos(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(usuarioService.findAllInactivos(numeroPagina), HttpStatus.OK);
    }

    @Operation(
            summary = "Listar usuarios para el panel de administración",
            description = "Devuelve una página de 10 usuarios con id, nombre, email, rol y estado. "
                    + "Permite buscar por nombre o email y filtrar por rol y por estado (activo/inactivo).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para ver usuarios")
            }
    )
    @GetMapping("/admin/listar/{numeroPagina}")
    public ResponseEntity<Page<UsuarioAdminDTO>> listarUsuariosAdmin(@PathVariable int numeroPagina,
                                                                     @RequestParam(required = false) String busqueda,
                                                                     @RequestParam(required = false) Rol rol,
                                                                     @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(usuarioService.listarParaAdmin(numeroPagina, busqueda, rol, activo));
    }

    @Operation(
            summary = "Buscar usuario por nombre",
            description = "Busca y devuelve un usuario que coincida con el nombre proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @GetMapping("/buscar_nombre")
    public ResponseEntity<UsuarioCreateDTO> buscarNombre(@RequestParam String nombre) {
        return new ResponseEntity<>(usuarioService.findByNombre(nombre), HttpStatus.OK);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario por su ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable int id) {
        return new ResponseEntity<>(usuarioService.delete(id), HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Dar de baja a un usuario",
            description = "Cambia el estado del usuario a inactivo (baja lógica).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario dado de baja"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "409", description = "El usuario ya estaba dado de baja o es el propio administrador")
            }
    )
    @PutMapping("/baja/{id}")
    public ResponseEntity<String> darDeBajaUsuario(@PathVariable int id) {
        return new ResponseEntity<>(usuarioService.bajaUsuario(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Dar de alta a un usuario",
            description = "Activa nuevamente a un usuario que estaba dado de baja.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario dado de alta"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @PutMapping("/alta/{id}")
    public ResponseEntity<String> darDeAltaUsuario(@PathVariable int id) {
        return new ResponseEntity<>(usuarioService.altaUsuario(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Verificar disponibilidad de nombre de usuario",
            description = "Verifica si ya existe un usuario registrado con el nombre indicado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultado de la verificación", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    // Para que esta esto aca?
    @GetMapping("/verificar_nombre/{nombre}")
    public ResponseEntity<String> verificarUsuarioPorNombre(@PathVariable String nombre) {
        String mensaje = usuarioService.verificar_nombre(nombre);
        return ResponseEntity.ok(mensaje);
    }

}
