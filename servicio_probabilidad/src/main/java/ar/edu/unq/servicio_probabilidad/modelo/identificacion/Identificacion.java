package ar.edu.unq.servicio_probabilidad.modelo.identificacion;

import ar.edu.unq.servicio_probabilidad.modelo.espiritu.Espiritu;
import ar.edu.unq.servicio_probabilidad.modelo.medium.ConjeturaMedium;

import java.util.List;

public class Identificacion {

    private final Espiritu espiritu;
    private final ConjeturaMedium conjetura;
    private final double corduraNormalizada;
    private final ProbabilidadIdentificacion probabilidad;

    public Identificacion(Espiritu espiritu, ConjeturaMedium conjetura, double corduraNormalizada, ProbabilidadIdentificacion probabilidad) {
        this.espiritu = espiritu;
        this.conjetura = conjetura;
        this.corduraNormalizada = corduraNormalizada;
        this.probabilidad = probabilidad;
    }

    public ResultadoIdentificacion resolver() {

        // Probabilidad en base a la cordura / hostilidad (para la muerte)
        double base = probabilidad.probabilidadPromedio(List.of(corduraNormalizada, 1 - espiritu.getHostilidadNormalizada()));

        // NO se usa más para decidir éxito, pero lo mantenemos porque
        // puede influir en el diseño futuro
        double bonusNombre = probabilidad.bonusPorNombre(espiritu.getNombre(), conjetura.getNombre());

        double probabilidadTotal = probabilidad.probabilidadPromedio(List.of(base, bonusNombre));

        boolean exito = espiritu.getNombre().equalsIgnoreCase(conjetura.getNombre());

        String mensaje = exito ? "¡Éxito! El espíritu ha sido identificado." : "Fracaso. El espíritu no ha sido identificado.";

        return new ResultadoIdentificacion(exito, corduraNormalizada, mensaje, espiritu.getId());
    }
}
