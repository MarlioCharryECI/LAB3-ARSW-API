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
            Blueprint existing = getBlueprint(author, name);
            
            if (!blueprint.getAuthor().equals(author) || !blueprint.getName().equals(name)) {
                TypedQuery<Blueprint> query = entityManager.createQuery(
                    "SELECT b FROM Blueprint b WHERE b.author = :author AND b.name = :name", Blueprint.class);
                query.setParameter("author", blueprint.getAuthor());
                query.setParameter("name", blueprint.getName());
                
                try {
                    Blueprint duplicate = query.getSingleResult();
                    throw new BlueprintPersistenceException("Blueprint with new author/name already exists: " + blueprint.getAuthor() + "/" + blueprint.getName());
                } catch (NoResultException e) {
                    // No duplicate found, proceed with update
                }
            }
            
            existing.setAuthor(blueprint.getAuthor());
            existing.setName(blueprint.getName());
            existing.getPoints().clear();
            for (Point point : blueprint.getPoints()) {
                existing.addPoint(new Point(point.getX(), point.getY()));
            }
            
            entityManager.merge(existing);
        } catch (BlueprintNotFoundException | BlueprintPersistenceException e) {
            throw e;
        } catch (Exception e) {
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
