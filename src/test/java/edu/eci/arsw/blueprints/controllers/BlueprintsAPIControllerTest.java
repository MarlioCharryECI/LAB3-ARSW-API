package edu.eci.arsw.blueprints.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlueprintsAPIController.class)
@Import(BlueprintsAPIControllerTest.TestConfig.class)
class BlueprintsAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /* =========================
       GET /api/v1/blueprints
       ========================= */
    @Test
    void should_return_all_blueprints() throws Exception {
        mockMvc.perform(get("/api/v1/blueprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    /* =========================
       GET /api/v1/blueprints/{author}
       ========================= */
    @Test
    void should_return_blueprints_by_author() throws Exception {
        mockMvc.perform(get("/api/v1/blueprints/marlio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].author").value("marlio"));
    }

    @Test
    void should_return_404_when_author_not_found() throws Exception {
        mockMvc.perform(get("/api/v1/blueprints/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    /* =========================
       GET /api/v1/blueprints/{author}/{name}
       ========================= */
    @Test
    void should_return_blueprint_by_author_and_name() throws Exception {
        mockMvc.perform(get("/api/v1/blueprints/marlio/casa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("casa"));
    }

    @Test
    void should_return_404_when_blueprint_not_found() throws Exception {
        mockMvc.perform(get("/api/v1/blueprints/marlio/inexistente"))
                .andExpect(status().isNotFound());
    }

    /* =========================
       POST /api/v1/blueprints
       ========================= */
    @Test
    void should_create_blueprint() throws Exception {
        var request = new BlueprintsAPIController.NewBlueprintRequest(
                "marlio",
                "edificio",
                List.of(new Point(1, 1))
        );

        mockMvc.perform(post("/api/v1/blueprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201));
    }

    @Test
    void should_return_403_when_blueprint_already_exists() throws Exception {
        var request = new BlueprintsAPIController.NewBlueprintRequest(
                "exists",
                "bp",
                List.of()
        );

        mockMvc.perform(post("/api/v1/blueprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    /* =========================
       PUT /api/v1/blueprints/{author}/{name}/points
       ========================= */
    @Test
    void should_add_point_to_blueprint() throws Exception {
        mockMvc.perform(put("/api/v1/blueprints/marlio/casa/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Point(5, 5))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.code").value(202));
    }

    @Test
    void should_return_404_when_adding_point_to_nonexistent_blueprint() throws Exception {
        mockMvc.perform(put("/api/v1/blueprints/marlio/nope/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Point(1, 1))))
                .andExpect(status().isNotFound());
    }

    /* ==========================================================
       CONFIGURACIÓN DE TEST (Fake Service, NO Mockito)
       ========================================================== */
    @TestConfiguration
    static class TestConfig {

        @Bean
        public BlueprintsServices blueprintsServices() {
            return new BlueprintsServices(null, null) {

                @Override
                public Set<Blueprint> getAllBlueprints() {
                    return Set.of(
                            new Blueprint("marlio", "casa", List.of(new Point(0, 0)))
                    );
                }

                @Override
                public Set<Blueprint> getBlueprintsByAuthor(String author)
                        throws BlueprintNotFoundException {
                    if (author.equals("unknown")) {
                        throw new BlueprintNotFoundException("Author not found");
                    }
                    return getAllBlueprints();
                }

                @Override
                public Blueprint getBlueprint(String author, String name)
                        throws BlueprintNotFoundException {
                    if (name.equals("inexistente")) {
                        throw new BlueprintNotFoundException("Blueprint not found");
                    }
                    return new Blueprint(author, name, List.of(new Point(0, 0)));
                }

                @Override
                public void addNewBlueprint(Blueprint bp)
                        throws BlueprintPersistenceException {
                    if (bp.getAuthor().equals("exists")) {
                        throw new BlueprintPersistenceException("Already exists");
                    }
                }

                @Override
                public void addPoint(String author, String name, int x, int y)
                        throws BlueprintNotFoundException {
                    if (name.equals("nope")) {
                        throw new BlueprintNotFoundException("Blueprint not found");
                    }
                }
            };
        }
    }
}