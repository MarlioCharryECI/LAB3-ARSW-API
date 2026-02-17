package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default filter: returns the blueprint unchanged.
 * This matches the baseline behavior of the reference lab before students implement custom filters.
 * Activated when no specific filter profile is set.
 */
@Component
@Profile("!redundancy & !undersampling")
public class IdentityFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint bp) { return bp; }
}
