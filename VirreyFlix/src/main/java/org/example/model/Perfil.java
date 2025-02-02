package org.example.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100)
    private String nombre;

    private int edad;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Historial> historiales = new HashSet<>();

    public Perfil() {
    }

    public Perfil(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Set<Historial> getHistoriales() {
        return historiales;
    }

    public void setHistoriales(Set<Historial> historiales) {
        this.historiales = historiales;
    }

    @Override
    public String toString() {
        return "Perfil: " +
                "Id: " + id +
                ", Nombre: '" + nombre + '\'' +
                ", Edad: " + edad +
                ", Usuario: " + (usuario != null ? "ID: " + usuario.getId() : "Sin usuario asociado") +
                ", Historiales: " + (!historiales.isEmpty() ? historiales : "Sin historial");
    }
}
