package edu.eci.arsw.blueprints.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

@Entity
@Schema(description = "Representa un punto en el plano 2D")
public class Point {
    
    @Id
    @Schema(description = "Coordenada X del punto", example = "10")
    private int x;
    
    @Schema(description = "Coordenada Y del punto", example = "20")
    private int y;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id")
    @JsonIgnore
    private Blueprint blueprint;
    
    public Point() {}
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public Blueprint getBlueprint() { return blueprint; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setBlueprint(Blueprint blueprint) { this.blueprint = blueprint; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
