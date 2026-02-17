package edu.eci.arsw.blueprints.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void testPointConstructor() {
        Point point = new Point(5, 10);
        
        assertEquals(5, point.getX());
        assertEquals(10, point.getY());
    }

    @Test
    void testPointGettersAndSetters() {
        Point point = new Point(0, 0);
        point.setX(15);
        point.setY(20);
        
        assertEquals(15, point.getX());
        assertEquals(20, point.getY());
    }

    @Test
    void testPointEquals() {
        Point point1 = new Point(5, 10);
        Point point2 = new Point(5, 10);
        Point point3 = new Point(6, 10);
        
        assertEquals(point1, point2);
        assertNotEquals(point1, point3);
        assertNotEquals(point1, null);
        assertNotEquals(point1, "not a point");
    }

    @Test
    void testPointHashCode() {
        Point point1 = new Point(5, 10);
        Point point2 = new Point(5, 10);
        
        assertEquals(point1.hashCode(), point2.hashCode());
    }

    @Test
    void testPointToString() {
        Point point = new Point(5, 10);
        String toString = point.toString();

        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }

    @Test
    void testPointWithNegativeCoordinates() {
        Point point = new Point(-5, -10);
        
        assertEquals(-5, point.getX());
        assertEquals(-10, point.getY());
    }

    @Test
    void testPointWithZeroCoordinates() {
        Point point = new Point(0, 0);
        
        assertEquals(0, point.getX());
        assertEquals(0, point.getY());
    }
}
