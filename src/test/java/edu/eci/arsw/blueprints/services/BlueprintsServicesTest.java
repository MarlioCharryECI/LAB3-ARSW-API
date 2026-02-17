package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filters.BlueprintsFilter;
import edu.eci.arsw.blueprints.filters.IdentityFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlueprintsServicesTest {

    @Mock
    private BlueprintPersistence persistence;

    @Mock
    private BlueprintsFilter filter;

    @InjectMocks
    private BlueprintsServices services;

    private Blueprint testBlueprint;
    private Set<Blueprint> testBlueprints;

    @BeforeEach
    void setUp() {
        List<Point> points = List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2));
        testBlueprint = new Blueprint("author", "test", points);
        testBlueprints = new HashSet<>();
        testBlueprints.add(testBlueprint);
    }

    @Test
    void testAddNewBlueprint() throws BlueprintPersistenceException {
        services.addNewBlueprint(testBlueprint);
        verify(persistence, times(1)).saveBlueprint(testBlueprint);
    }

    @Test
    void testAddNewBlueprintThrowsException() throws BlueprintPersistenceException {
        doThrow(new BlueprintPersistenceException("Error")).when(persistence).saveBlueprint(any());
        
        assertThrows(BlueprintPersistenceException.class, () -> {
            services.addNewBlueprint(testBlueprint);
        });
    }

    @Test
    void testGetAllBlueprints() {
        when(persistence.getAllBlueprints()).thenReturn(testBlueprints);
        
        Set<Blueprint> result = services.getAllBlueprints();
        
        assertEquals(testBlueprints, result);
        verify(persistence, times(1)).getAllBlueprints();
    }

    @Test
    void testGetBlueprintsByAuthor() throws BlueprintNotFoundException {
        when(persistence.getBlueprintsByAuthor("author")).thenReturn(testBlueprints);
        
        Set<Blueprint> result = services.getBlueprintsByAuthor("author");
        
        assertEquals(testBlueprints, result);
        verify(persistence, times(1)).getBlueprintsByAuthor("author");
    }

    @Test
    void testGetBlueprintsByAuthorThrowsException() throws BlueprintNotFoundException {
        when(persistence.getBlueprintsByAuthor("unknown")).thenThrow(new BlueprintNotFoundException("Not found"));
        
        assertThrows(BlueprintNotFoundException.class, () -> {
            services.getBlueprintsByAuthor("unknown");
        });
    }

    @Test
    void testGetBlueprint() throws BlueprintNotFoundException {
        Blueprint filteredBlueprint = new Blueprint("author", "test", List.of(new Point(0, 0)));
        when(persistence.getBlueprint("author", "test")).thenReturn(testBlueprint);
        when(filter.apply(testBlueprint)).thenReturn(filteredBlueprint);
        
        Blueprint result = services.getBlueprint("author", "test");
        
        assertEquals(filteredBlueprint, result);
        verify(persistence, times(1)).getBlueprint("author", "test");
        verify(filter, times(1)).apply(testBlueprint);
    }

    @Test
    void testGetBlueprintThrowsException() throws BlueprintNotFoundException {
        when(persistence.getBlueprint("author", "unknown")).thenThrow(new BlueprintNotFoundException("Not found"));
        
        assertThrows(BlueprintNotFoundException.class, () -> {
            services.getBlueprint("author", "unknown");
        });
    }

    @Test
    void testAddPoint() throws BlueprintNotFoundException {
        services.addPoint("author", "test", 5, 5);
        
        verify(persistence, times(1)).addPoint("author", "test", 5, 5);
    }

    @Test
    void testAddPointThrowsException() throws BlueprintNotFoundException {
        doThrow(new BlueprintNotFoundException("Not found")).when(persistence).addPoint(any(), any(), anyInt(), anyInt());
        
        assertThrows(BlueprintNotFoundException.class, () -> {
            services.addPoint("author", "unknown", 5, 5);
        });
    }
}
