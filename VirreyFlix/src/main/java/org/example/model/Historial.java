package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime fecha_reproduccion;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "episodio_id")
    private Episodio episodio;

    public Historial() {
        this.fecha_reproduccion = LocalDateTime.now();
    }

    public Historial(LocalDateTime fecha_reproduccion) {
        this.fecha_reproduccion = fecha_reproduccion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getFecha_reproduccion() {
        return fecha_reproduccion;
    }

    public void setFecha_reproduccion(LocalDateTime fecha_reproduccion) {
        this.fecha_reproduccion = fecha_reproduccion;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Episodio getEpisodio() {
        return episodio;
    }

    public void setEpisodio(Episodio episodio) {
        this.episodio = episodio;
    }

    @Override
    public String toString() {
        return "Historial: " +
                "Id: " + id +
                ", Fecha de construccion: " + fecha_reproduccion +
                ", Perfil: " + (perfil != null ? "Id: " + perfil.getId() + ", Nombre: " + perfil.getNombre() : "No hay perfil asociado") +
                ", Episodio: " + (episodio != null ? "Id: " + episodio.getId() + ", Titulo: " + episodio.getTitulo() : "No hay episodios asociados");
    }
}
