package org.example.model;

import jakarta.persistence.*;
import org.hibernate.boot.model.source.spi.FetchCharacteristics;

@Entity
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 200)
    private String titulo;

    private int duracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    public Episodio() {
    }

    public Episodio(String titulo, int duracion) {
        this.titulo = titulo;
        this.duracion = duracion;
    }

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

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    @Override
    public String toString() {
        return "Episodio: " +
                "Id: " + id +
                ", Titulo: '" + titulo + '\'' +
                ", Duración: " + duracion +
                ", Serie: " + (serie != null ? serie : "No hay serie asociada");
    }
}
