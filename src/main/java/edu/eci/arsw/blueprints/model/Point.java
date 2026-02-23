package edu.eci.arsw.blueprints.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

@Entity
@Schema(description = "Representa un punto en el plano 2D")
public class Point {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del punto", example = "1")
    private Long id;
    
    @Schema(description = "Coordenada X del punto", example = "10")
    private int x;
    
    @Schema(description = "Coordenada Y del punto", example = "20")
    private int y;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id")
    @JsonBackReference
    @JsonIgnore
    private Blueprint blueprint;
    
    public Point() {}
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Long getId() { return id; }
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
        return id != null && id.equals(point.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
