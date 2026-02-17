package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedundancyFilterTest {

    @Test
    void testRedundancyFilterRemovesConsecutiveDuplicates() {
        RedundancyFilter filter = new RedundancyFilter();
        
        List<Point> points = Arrays.asList(
            new Point(0, 0),
            new Point(0, 0),  // duplicate
            new Point(1, 1),
            new Point(2, 2),
            new Point(2, 2),  // duplicate
            new Point(2, 2),  // duplicate
            new Point(3, 3),
            new Point(1, 1),  // not consecutive duplicate
            new Point(1, 1)   // duplicate
        );
        
        Blueprint original = new Blueprint("author", "test", points);
        Blueprint filtered = filter.apply(original);
        
        List<Point> filteredPoints = filtered.getPoints();
        assertEquals(5, filteredPoints.size());
        
        assertEquals(new Point(0, 0), filteredPoints.get(0));
        assertEquals(new Point(1, 1), filteredPoints.get(1));
        assertEquals(new Point(2, 2), filteredPoints.get(2));
        assertEquals(new Point(3, 3), filteredPoints.get(3));
        assertEquals(new Point(1, 1), filteredPoints.get(4));
    }

    @Test
    void testRedundancyFilterEmptyBlueprint() {
        RedundancyFilter filter = new RedundancyFilter();
        Blueprint empty = new Blueprint("author", "empty", List.of());
        
        Blueprint filtered = filter.apply(empty);
        assertEquals(0, filtered.getPoints().size());
    }

    @Test
    void testRedundancyFilterNoDuplicates() {
        RedundancyFilter filter = new RedundancyFilter();
        
        List<Point> points = Arrays.asList(
            new Point(0, 0),
            new Point(1, 1),
            new Point(2, 2)
        );
        
        Blueprint original = new Blueprint("author", "test", points);
        Blueprint filtered = filter.apply(original);
        
        assertEquals(3, filtered.getPoints().size());
        assertEquals(original.getPoints(), filtered.getPoints());
    }
}
