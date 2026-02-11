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
}
