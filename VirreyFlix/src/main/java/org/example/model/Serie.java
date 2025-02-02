package org.example.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 200)
    private String titulo;

    @Column(name = "calificacion_edad")
    private int calificacionEdad;

    @ManyToMany
    @JoinTable(
            name = "serie_genero",  // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "serie_id"), // Columna de la serie
            inverseJoinColumns = @JoinColumn(name = "genero_id") // Columna del genero
    )
    private Set<Genero> generos = new HashSet<>();

    public Serie() {
    }

    public Serie(String titulo, int calificacionEdad) {
        this.titulo = titulo;
        this.calificacionEdad = calificacionEdad;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCalificacionEdad() {
        return calificacionEdad;
    }

    public void setCalificacionEdad(int calificacionEdad) {
        this.calificacionEdad = calificacionEdad;
    }

    public Set<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(Set<Genero> generos) {
        this.generos = generos;
    }

    @Override
    public String toString() {
        return "Serie: " +
                "ID: " + id +
                ", Titulo: '" + titulo + '\'' +
                ", calificacion de edad: " + calificacionEdad +
                ", generos: " + (generos != null && !generos.isEmpty() ? generos.size() + " géneros asociados" : "No hay géneros asociados.");
    }
}
