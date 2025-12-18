package ar.edu.unq.servicio_probabilidad.modelo.identificacion;

import lombok.Getter;

@Getter
public class ResultadoIdentificacion {
    private boolean exito;
    private boolean muere;
    private String mensaje;
    private Long espirituId;

    public ResultadoIdentificacion(boolean exito, double corduraNormalizada, String mensaje, Long espirituId) {
        this.exito = exito;
        this.muere = !exito && muerePorCordura(corduraNormalizada);
        this.mensaje = mensaje;
        this.espirituId = espirituId;
    }

    private boolean muerePorCordura(double corduraNormalizada) {
        return corduraNormalizada == 0 || corduraNormalizada < Math.random();
    }
}
