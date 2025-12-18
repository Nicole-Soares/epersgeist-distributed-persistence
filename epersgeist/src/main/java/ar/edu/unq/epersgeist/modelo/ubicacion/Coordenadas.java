package ar.edu.unq.epersgeist.modelo.ubicacion;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Getter
public class Coordenadas {

    private Long id;
    private double latitud;
    private double longitud;
    private static final double RADIO_TIERRA_KM = 6371.0;

    public Coordenadas(double longitud, double latitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double calcularDistanciaA(Coordenadas destino) {
        // Coordenadas de origen
        Double latitudOrigen = this.latitud;
        Double longitudOrigen = this.longitud;
        // Coordenadas de destino
        Double latitudDestino = destino.getLatitud();
        Double longitudDestino = destino.getLongitud();

        // Fórmula de Haversine
        double dLat = Math.toRadians(latitudDestino - latitudOrigen);
        double dLon = Math.toRadians(longitudDestino - longitudOrigen);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(latitudOrigen))
                * Math.cos(Math.toRadians(latitudDestino))
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_KM * c;}
    }

