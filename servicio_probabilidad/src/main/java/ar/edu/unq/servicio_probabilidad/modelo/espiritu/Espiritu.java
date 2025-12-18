package ar.edu.unq.servicio_probabilidad.modelo.espiritu;

import lombok.Getter;

@Getter
public class Espiritu {
    private Long id;
    private String nombre;
    private String tipo;
    private double hostilidadNormalizada;
    private double afinidadNormalziada;

    public Espiritu(Long id, String nombre, String tipo, double hostilidadNormalizada) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.hostilidadNormalizada = hostilidadNormalizada;
        this.afinidadNormalziada = afinidadNormalziada;
    }
}
