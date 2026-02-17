package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentityFilterTest {

    @Test
    void testIdentityFilterReturnsSameBlueprint() {
        IdentityFilter filter = new IdentityFilter();
        List<Point> points = List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2));
        Blueprint original = new Blueprint("author", "test", points);
        
        Blueprint result = filter.apply(original);
        
        assertSame(original, result);
        assertEquals(original.getPoints(), result.getPoints());
    }

    @Test
    void testIdentityFilterWithEmptyBlueprint() {
        IdentityFilter filter = new IdentityFilter();
        Blueprint empty = new Blueprint("author", "empty", List.of());
        
        Blueprint result = filter.apply(empty);
        
        assertSame(empty, result);
        assertTrue(result.getPoints().isEmpty());
    }

    @Test
    void testIdentityFilterWithNullPoints() {
        IdentityFilter filter = new IdentityFilter();
        Blueprint nullPoints = new Blueprint("author", "null", null);
        
        Blueprint result = filter.apply(nullPoints);
        
        assertSame(nullPoints, result);
        assertTrue(result.getPoints().isEmpty());
    }

    @Test
    void testIdentityFilterWithSinglePoint() {
        IdentityFilter filter = new IdentityFilter();
        List<Point> points = List.of(new Point(5, 5));
        Blueprint single = new Blueprint("author", "single", points);
        
        Blueprint result = filter.apply(single);
        
        assertSame(single, result);
        assertEquals(1, result.getPoints().size());
        assertEquals(new Point(5, 5), result.getPoints().get(0));
    }
}
