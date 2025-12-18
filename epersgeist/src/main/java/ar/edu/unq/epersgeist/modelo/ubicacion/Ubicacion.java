package ar.edu.unq.epersgeist.modelo.ubicacion;
import ar.edu.unq.epersgeist.exception.CostoFueraDeRango;
import ar.edu.unq.epersgeist.exception.FlujoFueraDeRangoException;
import ar.edu.unq.epersgeist.exception.RelacionCircularInvalida;
import lombok.Data;

import java.util.*;

@Data
public class Ubicacion {

    private Long id;
    private String nombre;
    private Integer flujoEnergia;
    private Integer temperatura;
    private TipoUbicacion tipo;
    private Map<Long, Long> conexiones = new HashMap<>();
    final private Set<Coordenadas> vertices;

    public Ubicacion(String nombre, Integer flujoEnergia, TipoUbicacion tipo, Set<Coordenadas> vertices) {
        validarFlujoDeEnergia(flujoEnergia);
        this.nombre = nombre;
        this.flujoEnergia = flujoEnergia;
        this.temperatura = 20;
        this.tipo = tipo;
        validarVertices(vertices);
        this.vertices = vertices;
    }

    public Ubicacion() {
        this.vertices = new HashSet<>();
    }

    private void validarVertices(Set<Coordenadas> vertices) {
        if (vertices == null || vertices.size() != 3) {
            throw new IllegalArgumentException("El área debe tener exactamente 3 vértices");
        }
    }

    private static void validarFlujoDeEnergia(Integer flujoEnergia) {
        if (esFlujoDeEnergiaFueraDeRango(flujoEnergia)) {
            throw new FlujoFueraDeRangoException();
        }
    }

    private static boolean esFlujoDeEnergiaFueraDeRango(Integer flujoEnergia) {
        return flujoEnergia < 0 || flujoEnergia > 100;
    }

    public boolean esCementerio() {
        return tipo.equals(TipoUbicacion.CEMENTERIO);
    }

    public boolean esSantuario() {
        return tipo.equals(TipoUbicacion.SANTUARIO);
    }

    public void conectarCon(Ubicacion destino, Long costo) {
        validarConexionCircular(destino);
        validarCostoFueraDeRango(costo);
        conexiones.put(destino.getId(), costo);
    }

    private void validarConexionCircular(Ubicacion destino) {
        if(esConexionCircularCon(destino)) {
            throw new RelacionCircularInvalida();
        }
    }

    private void validarCostoFueraDeRango(Long costo) {
        if(esCostoFueraDeRango(costo)) {
            throw new CostoFueraDeRango();
        }
    }

    private boolean esCostoFueraDeRango(Long costo) {
        return costo < 0 || costo > 100;
    }

    private boolean esConexionCircularCon(Ubicacion destino) {
        return this.getId().equals(destino.getId());
    }

    public boolean estaConectadaCon(Ubicacion ubicacionDestino) {
        return this.conexiones.keySet().stream()
                .anyMatch(idUbicacionDestno -> idUbicacionDestno.equals(ubicacionDestino.getId()));
    }

    public int costoHacia(Ubicacion ubicacionDestino) {
        return this.conexiones.get(ubicacionDestino.getId()).intValue();
    }


    /**
     * Genera coordenadas aleatorias dentro del área definida por los vértices de la ubicación. Excluyendo los bordes y vertices.
     * @return Coordenadas aleatorias dentro del área.
     */
    public Coordenadas generarCoordenadasAleatorias() {
        List<Coordenadas> lista = new ArrayList<>(vertices);

        Coordenadas A = lista.get(0);
        Coordenadas B = lista.get(1);
        Coordenadas C = lista.get(2);

        // Genera 3 números aleatorios que cumplan u + v + w = 1 y u, v, w != 0
        double r1, r2;
        do {
            r1 = Math.random();
            r2 = Math.random();
            if (r1 + r2 > 1) {
                r1 = 1 - r1;
                r2 = 1 - r2;
            }
        } while (r1 == 0 || r2 == 0 || r1 + r2 == 1);

        double r3 = 1 - r1 - r2;

        // Fórmula para generar punto dentro del triángulo
        double lat = r1 * A.getLatitud() + r2 * (B.getLatitud()) + r3 * (C.getLatitud());
        double lon = r1 * A.getLongitud() + r2 * (B.getLongitud()) + r3 * (C.getLongitud());

        return new Coordenadas(lon, lat);
    }
}