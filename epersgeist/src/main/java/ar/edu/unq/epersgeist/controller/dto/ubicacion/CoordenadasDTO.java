package ar.edu.unq.epersgeist.controller.dto.ubicacion;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoordenadasDTO(@NotNull
                             double longitud,
                             @NotNull
                             double latitud) {

    /**
     * Convierte el DTO en el objeto de Dominio (Modelo puro).
     * Nota: El ID no se incluye al crear el modelo, ya que lo asigna la persistencia.
     */
    public Coordenadas aModelo() {
        return new Coordenadas(this.longitud, this.latitud);
    }

    /**
     * Crea un DTO a partir del objeto de Dominio (Modelo puro).
     */
    public static CoordenadasDTO desdeModelo(Coordenadas coordenadas) {
        return new CoordenadasDTO(coordenadas.getLongitud(), coordenadas.getLatitud());
    }
}