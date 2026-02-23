package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Repository
@Primary
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = Logger.getLogger(PostgresBlueprintPersistence.class.getName());

    @Override
    @Transactional
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        try {
            entityManager.persist(bp);
            logger.info("Blueprint saved: " + bp.getAuthor() + "/" + bp.getName());
        } catch (Exception e) {
            throw new BlueprintPersistenceException("Error saving blueprint: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        try {
            TypedQuery<Blueprint> query = entityManager.createQuery(
                "SELECT b FROM Blueprint b WHERE b.author = :author AND b.name = :name", Blueprint.class);
            query.setParameter("author", author);
            query.setParameter("name", name);
            
            Blueprint bp = query.getSingleResult();
            if (bp == null) {
                throw new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name);
            }
            return bp;
        } catch (NoResultException e) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name);
        } catch (Exception e) {
            throw new BlueprintNotFoundException("Error getting blueprint: " + e.getMessage());
        }
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        try {
            TypedQuery<Blueprint> query = entityManager.createQuery(
                "SELECT b FROM Blueprint b WHERE b.author = :author", Blueprint.class);
            query.setParameter("author", author);
            
            List<Blueprint> blueprints = query.getResultList();
            if (blueprints.isEmpty()) {
                throw new BlueprintNotFoundException("No blueprints found for author: " + author);
            }
            return new HashSet<>(blueprints);
        } catch (Exception e) {
            throw new BlueprintNotFoundException("Error getting blueprints by author: " + e.getMessage());
        }
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        try {
            TypedQuery<Blueprint> query = entityManager.createQuery(
                "SELECT b FROM Blueprint b", Blueprint.class);
            List<Blueprint> blueprints = query.getResultList();
            return new HashSet<>(blueprints);
        } catch (Exception e) {
            logger.warning("Error getting all blueprints: " + e.getMessage());
            return new HashSet<>();
        }
    }

    @Override
    @Transactional
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        try {
            Blueprint bp = getBlueprint(author, name);
            Point point = new Point(x, y);
            bp.addPoint(point);
            entityManager.merge(bp);
        } catch (Exception e) {
            throw new BlueprintNotFoundException("Error adding point: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateBlueprint(String author, String name, Blueprint blueprint) throws BlueprintNotFoundException, BlueprintPersistenceException {
        try {
            logger.info("Updating blueprint: " + author + "/" + name + " -> " + blueprint.getAuthor() + "/" + blueprint.getName());
            
            // Usar consulta directa para evitar conflictos de sesión
            TypedQuery<Blueprint> query = entityManager.createQuery(
                "SELECT b FROM Blueprint b LEFT JOIN FETCH b.points WHERE b.author = :author AND b.name = :name", Blueprint.class);
            query.setParameter("author", author);
            query.setParameter("name", name);
            
            Blueprint existing;
            try {
                existing = query.getSingleResult();
            } catch (NoResultException e) {
                throw new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name);
            }
            
            if (!blueprint.getAuthor().equals(author) || !blueprint.getName().equals(name)) {
                logger.info("Checking for duplicate: " + blueprint.getAuthor() + "/" + blueprint.getName());
                TypedQuery<Blueprint> duplicateQuery = entityManager.createQuery(
                    "SELECT b FROM Blueprint b WHERE b.author = :author AND b.name = :name", Blueprint.class);
                duplicateQuery.setParameter("author", blueprint.getAuthor());
                duplicateQuery.setParameter("name", blueprint.getName());
                
                try {
                    Blueprint duplicate = duplicateQuery.getSingleResult();
                    logger.warning("Duplicate found: " + duplicate.getAuthor() + "/" + duplicate.getName());
                    throw new BlueprintPersistenceException("Blueprint with new author/name already exists: " + blueprint.getAuthor() + "/" + blueprint.getName());
                } catch (NoResultException e) {
                    logger.info("No duplicate found, proceeding with update");
                }
            }
            
            logger.info("Clearing existing points, count: " + existing.getPoints().size());
            existing.setAuthor(blueprint.getAuthor());
            existing.setName(blueprint.getName());
            
            // Usar el método replacePoints que ya maneja correctamente la limpieza
            existing.replacePoints(blueprint.getPoints());
            
            entityManager.merge(existing);
            logger.info("Blueprint updated successfully");
        } catch (BlueprintNotFoundException | BlueprintPersistenceException e) {
            logger.warning("Business exception: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Unexpected error in updateBlueprint: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new BlueprintPersistenceException("Error updating blueprint: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        try {
            Blueprint bp = getBlueprint(author, name);
            entityManager.remove(bp);
        } catch (BlueprintNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BlueprintNotFoundException("Error deleting blueprint: " + e.getMessage());
        }
    }
}
