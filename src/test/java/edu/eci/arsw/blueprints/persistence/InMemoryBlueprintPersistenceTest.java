package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBlueprintPersistenceTest {

    private InMemoryBlueprintPersistence persistence;

    @BeforeEach
    void setUp() {
        persistence = new InMemoryBlueprintPersistence();
    }

    @Test
    void testSaveAndGetBlueprint() throws BlueprintPersistenceException, BlueprintNotFoundException {
        List<Point> points = List.of(new Point(0, 0), new Point(1, 1));
        Blueprint blueprint = new Blueprint("author", "test", points);
        
        persistence.saveBlueprint(blueprint);
        Blueprint retrieved = persistence.getBlueprint("author", "test");
        
        assertEquals(blueprint, retrieved);
        assertEquals("author", retrieved.getAuthor());
        assertEquals("test", retrieved.getName());
        assertEquals(points, retrieved.getPoints());
    }

    @Test
    void testSaveDuplicateBlueprint() {
        List<Point> points = List.of(new Point(0, 0));
        Blueprint blueprint1 = new Blueprint("author", "test", points);
        Blueprint blueprint2 = new Blueprint("author", "test", points);
        
        assertThrows(BlueprintPersistenceException.class, () -> {
            persistence.saveBlueprint(blueprint1);
            persistence.saveBlueprint(blueprint2);
        });
    }

    @Test
    void testGetNonExistentBlueprint() {
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.getBlueprint("unknown", "unknown");
        });
    }

    @Test
    void testGetAllBlueprints() throws BlueprintPersistenceException {
        Set<Blueprint> additionalBlueprints = new HashSet<>();
        additionalBlueprints.add(new Blueprint("author1", "test1", List.of(new Point(0, 0))));
        additionalBlueprints.add(new Blueprint("author2", "test2", List.of(new Point(1, 1))));
        
        for (Blueprint bp : additionalBlueprints) {
            persistence.saveBlueprint(bp);
        }
        
        Set<Blueprint> retrieved = persistence.getAllBlueprints();
        assertEquals(5, retrieved.size());
        assertTrue(retrieved.containsAll(additionalBlueprints));
    }

    @Test
    void testGetBlueprintsByAuthor() throws BlueprintPersistenceException, BlueprintNotFoundException {
        Blueprint bp1 = new Blueprint("author", "test1", List.of(new Point(0, 0)));
        Blueprint bp2 = new Blueprint("author", "test2", List.of(new Point(1, 1)));
        Blueprint bp3 = new Blueprint("other", "test3", List.of(new Point(2, 2)));
        
        persistence.saveBlueprint(bp1);
        persistence.saveBlueprint(bp2);
        persistence.saveBlueprint(bp3);
        
        Set<Blueprint> authorBlueprints = persistence.getBlueprintsByAuthor("author");
        assertEquals(2, authorBlueprints.size());
        assertTrue(authorBlueprints.contains(bp1));
        assertTrue(authorBlueprints.contains(bp2));
        assertFalse(authorBlueprints.contains(bp3));
    }

    @Test
    void testGetBlueprintsByNonExistentAuthor() {
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.getBlueprintsByAuthor("unknown");
        });
    }

    @Test
    void testAddPoint() throws BlueprintPersistenceException, BlueprintNotFoundException {
        Blueprint blueprint = new Blueprint("author", "test", List.of(new Point(0, 0)));
        persistence.saveBlueprint(blueprint);
        
        persistence.addPoint("author", "test", 5, 5);
        
        Blueprint updated = persistence.getBlueprint("author", "test");
        assertEquals(2, updated.getPoints().size());
        assertTrue(updated.getPoints().contains(new Point(5, 5)));
    }

    @Test
    void testAddPointToNonExistentBlueprint() {
        assertThrows(BlueprintNotFoundException.class, () -> {
            persistence.addPoint("unknown", "unknown", 5, 5);
        });
    }
}
