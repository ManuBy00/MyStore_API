package com.mby.myStore.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 150)
    @NotNull
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_registro")
    private Instant fechaRegistro;

    @OneToMany
    @JoinColumn(name = "cliente_id")
    private Set<Cita> citas = new LinkedHashSet<>();

    public Cliente (String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.fechaRegistro = Instant.now();
    }

    public Cliente() {

    }
}