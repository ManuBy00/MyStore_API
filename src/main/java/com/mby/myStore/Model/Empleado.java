package com.mby.myStore.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 50)
    @Column(name = "especialidad", length = 50)
    private String especialidad;

    @ColumnDefault("1")
    @Column(name = "activo")
    private Boolean activo;

    @OneToMany
    @JoinColumn(name = "empleado_id")
    private Set<Cita> citas = new LinkedHashSet<>();

}