package edu.eci.arsw.blueprints;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class BlueprintsApplicationTest {

    @Test
    void contextLoads() {
        assertNotNull(this);
    }

    @Test
    void mainMethod() {
        assertDoesNotThrow(() -> {
            Class<?> mainClass = Class.forName("edu.eci.arsw.blueprints.BlueprintsApplication");
            assertNotNull(mainClass.getMethod("main", String[].class));
        });
    }
}
