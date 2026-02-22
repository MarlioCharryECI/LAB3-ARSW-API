package edu.eci.arsw.blueprints.dto;

import edu.eci.arsw.blueprints.model.Blueprint;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta con los planos de un autor y el total de puntos")
public record AuthorBlueprintsDTO(
        @Schema(description = "Lista de planos del autor")
        List<Blueprint> blueprints,
        
        @Schema(description = "Total de puntos en todos los planos del autor", example = "15")
        int totalPoints
) {
}
