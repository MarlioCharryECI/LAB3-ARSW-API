package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UndersamplingFilterTest {

    @Test
    void testUndersamplingFilterKeepsEveryOtherPoint() {
        UndersamplingFilter filter = new UndersamplingFilter();
        
        List<Point> points = Arrays.asList(
            new Point(0, 0),  // index 0 - keep
            new Point(1, 1),  // index 1 - skip
            new Point(2, 2),  // index 2 - keep
            new Point(3, 3),  // index 3 - skip
            new Point(4, 4),  // index 4 - keep
            new Point(5, 5)   // index 5 - skip
        );
        
        Blueprint original = new Blueprint("author", "test", points);
        Blueprint filtered = filter.apply(original);
        
        List<Point> filteredPoints = filtered.getPoints();
        assertEquals(3, filteredPoints.size());
        
        assertEquals(new Point(0, 0), filteredPoints.get(0));
        assertEquals(new Point(2, 2), filteredPoints.get(1));
        assertEquals(new Point(4, 4), filteredPoints.get(2));
    }

    @Test
    void testUndersamplingFilterSmallBlueprint() {
        UndersamplingFilter filter = new UndersamplingFilter();
        
        List<Point> points = Arrays.asList(
            new Point(0, 0),
            new Point(1, 1)
        );
        
        Blueprint original = new Blueprint("author", "small", points);
        Blueprint filtered = filter.apply(original);

        assertEquals(2, filtered.getPoints().size());
        assertEquals(original.getPoints(), filtered.getPoints());
    }

    @Test
    void testUndersamplingFilterSinglePoint() {
        UndersamplingFilter filter = new UndersamplingFilter();
        
        List<Point> points = Arrays.asList(
            new Point(0, 0)
        );
        
        Blueprint original = new Blueprint("author", "single", points);
        Blueprint filtered = filter.apply(original);
        
        assertEquals(1, filtered.getPoints().size());
        assertEquals(original.getPoints(), filtered.getPoints());
    }

    @Test
    void testUndersamplingFilterEmptyBlueprint() {
        UndersamplingFilter filter = new UndersamplingFilter();
        Blueprint empty = new Blueprint("author", "empty", List.of());
        
        Blueprint filtered = filter.apply(empty);
        assertEquals(0, filtered.getPoints().size());
    }
}
