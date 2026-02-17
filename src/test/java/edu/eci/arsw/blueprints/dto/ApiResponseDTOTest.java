package edu.eci.arsw.blueprints.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseDTOTest {

    @Test
    void testSuccessWithCode() {
        String data = "test data";
        ApiResponseDTO<String> response = ApiResponseDTO.success(200, "Success", data);
        
        assertEquals(200, response.code());
        assertEquals("Success", response.message());
        assertEquals(data, response.data());
    }

    @Test
    void testSuccessWithoutCode() {
        String data = "test data";
        ApiResponseDTO<String> response = ApiResponseDTO.success("Success", data);
        
        assertEquals(200, response.code());
        assertEquals("Success", response.message());
        assertEquals(data, response.data());
    }

    @Test
    void testError() {
        ApiResponseDTO<String> response = ApiResponseDTO.error(400, "Bad Request");
        
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.data());
    }

    @Test
    void testNotFound() {
        ApiResponseDTO<String> response = ApiResponseDTO.notFound("Not Found");
        
        assertEquals(404, response.code());
        assertEquals("Not Found", response.message());
        assertNull(response.data());
    }

    @Test
    void testBadRequest() {
        ApiResponseDTO<String> response = ApiResponseDTO.badRequest("Bad Request");
        
        assertEquals(400, response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.data());
    }

    @Test
    void testForbidden() {
        ApiResponseDTO<String> response = ApiResponseDTO.forbidden("Forbidden");
        
        assertEquals(403, response.code());
        assertEquals("Forbidden", response.message());
        assertNull(response.data());
    }

    @Test
    void testRecordEqualsAndHashCode() {
        ApiResponseDTO<String> response1 = ApiResponseDTO.success(200, "Success", "data");
        ApiResponseDTO<String> response2 = ApiResponseDTO.success(200, "Success", "data");
        ApiResponseDTO<String> response3 = ApiResponseDTO.success(201, "Created", "data");
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        ApiResponseDTO<String> response = ApiResponseDTO.success(200, "Success", "data");
        String toString = response.toString();
        
        assertTrue(toString.contains("200"));
        assertTrue(toString.contains("Success"));
        assertTrue(toString.contains("data"));
    }
}
