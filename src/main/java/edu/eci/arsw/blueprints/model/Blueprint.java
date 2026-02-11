package edu.eci.arsw.blueprints.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "blueprints")
@Schema(description = "Representa un plano arquitectónico con sus puntos")
public class Blueprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del plano", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Autor del plano", example = "Marlio")
    private String author;
    
    @Column(nullable = false)
    @Schema(description = "Nombre del plano", example = "Edificio A")
    private String name;
    
    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Schema(description = "Lista de puntos que conforman el plano")
    private List<Point> points = new ArrayList<>();

    public Blueprint() {}

    public Blueprint(String author, String name, List<Point> pts) {
        this.author = author;
        this.name = name;
        if (pts != null) {
            for (Point p : pts) {
                addPoint(p);
            }
        }
    }

    public Long getId() { return id; }
    public String getAuthor() { return author; }
    public String getName() { return name; }
    public List<Point> getPoints() { return Collections.unmodifiableList(points); }

    public void setAuthor(String author) { this.author = author; }
    public void setName(String name) { this.name = name; }

    public void addPoint(Point p) {
        points.add(p);
        p.setBlueprint(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blueprint bp)) return false;
        return Objects.equals(author, bp.author) && Objects.equals(name, bp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }
}
