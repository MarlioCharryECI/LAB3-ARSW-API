package edu.eci.arsw.blueprints.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlueprintTest {

    private Blueprint blueprint;
    private List<Point> points;

    @BeforeEach
    void setUp() {
        points = List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2));
        blueprint = new Blueprint("author", "test", points);
    }

    @Test
    void testBlueprintConstructor() {
        assertEquals("author", blueprint.getAuthor());
        assertEquals("test", blueprint.getName());
        assertEquals(points, blueprint.getPoints());
    }

    @Test
    void testBlueprintGettersAndSetters() {
        blueprint.setAuthor("newAuthor");
        blueprint.setName("newTest");
        
        assertEquals("newAuthor", blueprint.getAuthor());
        assertEquals("newTest", blueprint.getName());
    }

    @Test
    void testAddPoint() {
        Point newPoint = new Point(3, 3);
        blueprint.addPoint(newPoint);
        
        assertEquals(4, blueprint.getPoints().size());
        assertTrue(blueprint.getPoints().contains(newPoint));
    }

    @Test
    void testAddPointToEmptyBlueprint() {
        Blueprint emptyBlueprint = new Blueprint("author", "empty", null);
        Point point = new Point(0, 0);
        emptyBlueprint.addPoint(point);
        
        assertEquals(1, emptyBlueprint.getPoints().size());
        assertEquals(point, emptyBlueprint.getPoints().get(0));
    }

    @Test
    void testBlueprintEquals() {
        Blueprint blueprint1 = new Blueprint("author", "test", points);
        Blueprint blueprint2 = new Blueprint("author", "test", points);
        Blueprint blueprint3 = new Blueprint("author", "different", points);
        
        assertEquals(blueprint1, blueprint2);
        assertNotEquals(blueprint1, blueprint3);
        assertNotEquals(blueprint1, null);
        assertNotEquals(blueprint1, "not a blueprint");
    }

    @Test
    void testBlueprintHashCode() {
        Blueprint blueprint1 = new Blueprint("author", "test", points);
        Blueprint blueprint2 = new Blueprint("author", "test", points);
        
        assertEquals(blueprint1.hashCode(), blueprint2.hashCode());
    }

    @Test
    void testBlueprintToString() {
        String toString = blueprint.toString();

        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("Blueprint"));
    }

    @Test
    void testBlueprintWithNullPoints() {
        Blueprint blueprintWithNullPoints = new Blueprint("author", "test", null);
        assertTrue(blueprintWithNullPoints.getPoints().isEmpty());
    }

    @Test
    void testBlueprintWithEmptyPoints() {
        Blueprint emptyBlueprint = new Blueprint("author", "empty", List.of());
        assertTrue(emptyBlueprint.getPoints().isEmpty());
    }
}
