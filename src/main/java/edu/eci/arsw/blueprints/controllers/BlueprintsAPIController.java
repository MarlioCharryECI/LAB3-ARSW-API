package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.dto.ApiResponseDTO;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints API", description = "API para gestionar planos arquitectónicos")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /api/v1/blueprints
    @GetMapping
    @Operation(summary = "Obtener todos los planos", description = "Retorna una lista con todos los planos disponibles en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planos encontrados exitosamente",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<Set<Blueprint>>> getAll() {
        Set<Blueprint> blueprints = services.getAllBlueprints();
        return ResponseEntity.ok(ApiResponseDTO.success("Planos obtenidos exitosamente", blueprints));
    }

    // GET /api/v1/blueprints/{author}
    @GetMapping("/{author}")
    @Operation(summary = "Obtener planos por autor", description = "Retorna todos los planos creados por un autor específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planos del autor encontrados",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Autor no encontrado o no tiene planos",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<Set<Blueprint>>> byAuthor(
            @Parameter(description = "Nombre del autor", required = true) 
            @PathVariable String author) {
        try {
            Set<Blueprint> blueprints = services.getBlueprintsByAuthor(author);
            return ResponseEntity.ok(ApiResponseDTO.success("Planos del autor encontrados", blueprints));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.notFound(e.getMessage()));
        }
    }

    // GET /api/v1/blueprints/{author}/{bpname}
    @GetMapping("/{author}/{bpname}")
    @Operation(summary = "Obtener plano específico", description = "Retorna un plano específico dado su autor y nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plano encontrado exitosamente",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plano no encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<Blueprint>> byAuthorAndName(
            @Parameter(description = "Nombre del autor", required = true) 
            @PathVariable String author, 
            @Parameter(description = "Nombre del plano", required = true) 
            @PathVariable String bpname) {
        try {
            Blueprint blueprint = services.getBlueprint(author, bpname);
            return ResponseEntity.ok(ApiResponseDTO.success("Plano encontrado exitosamente", blueprint));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.notFound(e.getMessage()));
        }
    }

    // POST /api/v1/blueprints
    @PostMapping
    @Operation(summary = "Crear nuevo plano", description = "Crea un nuevo plano con el autor, nombre y puntos especificados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plano creado exitosamente",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "El plano ya existe o datos inválidos",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<Blueprint>> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), null);
            for (Point point : req.points()) {
                bp.addPoint(new Point(point.getX(), point.getY()));
            }
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success(201, "Plano creado exitosamente", bp));
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDTO.forbidden(e.getMessage()));
        }
    }

    // PUT /api/v1/blueprints/{author}/{bpname}/points
    @PutMapping("/{author}/{bpname}/points")
    @Operation(summary = "Agregar punto a plano", description = "Agrega un nuevo punto a un plano existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Punto agregado exitosamente",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plano no encontrado",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos del punto inválidos",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<String>> addPoint(
            @Parameter(description = "Nombre del autor", required = true) 
            @PathVariable String author, 
            @Parameter(description = "Nombre del plano", required = true) 
            @PathVariable String bpname,
            @Parameter(description = "Punto a agregar", required = true) 
            @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.getX(), p.getY());
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(ApiResponseDTO.success(202, "Punto agregado exitosamente", "Punto añadido al plano"));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.notFound(e.getMessage()));
        }
    }

    @Schema(description = "Request para crear un nuevo plano")
    public record NewBlueprintRequest(
            @NotBlank(message = "El autor es obligatorio")
            @Schema(description = "Nombre del autor del plano", example = "Marlio")
            String author,
            
            @NotBlank(message = "El nombre del plano es obligatorio")
            @Schema(description = "Nombre del plano", example = "Edificio A")
            String name,
            
            @Valid
            @Schema(description = "Lista de puntos que conforman el plano")
            java.util.List<Point> points
    ) { }
}
