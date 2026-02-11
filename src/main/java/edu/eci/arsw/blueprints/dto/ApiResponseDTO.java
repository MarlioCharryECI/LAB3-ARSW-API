package edu.eci.arsw.blueprints.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta estándar para todos los endpoints de la API
 * @param <T> Tipo de dato contenido en la respuesta
 */
@Schema(description = "Respuesta estándar de la API")
public record ApiResponseDTO<T>(
        @Schema(description = "Código de estado HTTP", example = "200")
        int code,
        
        @Schema(description = "Mensaje descriptivo de la respuesta", example = "Operación exitosa")
        String message,
        
        @Schema(description = "Datos de respuesta", implementation = Object.class)
        T data
) {
    
    /**
     * Crea una respuesta exitosa
     */
    public static <T> ApiResponseDTO<T> success(int code, String message, T data) {
        return new ApiResponseDTO<>(code, message, data);
    }
    
    /**
     * Crea una respuesta exitosa con código 200
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(200, message, data);
    }
    
    /**
     * Crea una respuesta de error
     */
    public static <T> ApiResponseDTO<T> error(int code, String message) {
        return new ApiResponseDTO<>(code, message, null);
    }
    
    /**
     * Crea una respuesta de error 404
     */
    public static <T> ApiResponseDTO<T> notFound(String message) {
        return new ApiResponseDTO<>(404, message, null);
    }
    
    /**
     * Crea una respuesta de error 400
     */
    public static <T> ApiResponseDTO<T> badRequest(String message) {
        return new ApiResponseDTO<>(400, message, null);
    }
    
    /**
     * Crea una respuesta de error 403
     */
    public static <T> ApiResponseDTO<T> forbidden(String message) {
        return new ApiResponseDTO<>(403, message, null);
    }
}
