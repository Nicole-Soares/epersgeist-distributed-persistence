package ar.edu.unq.servicio_probabilidad.modelo.medium;

import lombok.Getter;

@Getter
public class ConjeturaMedium {

    private String nombre;
    private String tipo;

    public ConjeturaMedium(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

}
