package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.exception.*;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.EntidadCoordenadaInfo;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class Medium implements Serializable, EntidadCoordenadaInfo {
  
    final static int COSTO_MANA_INVOCACION = 10;

    final static double CORDURA_MAXIMA = 100; // MIN < MAX SIEMPRE
    final static double CORDURA_MINIMA = 0;

    private Long id;
    private String nombre;
    private Integer manaMax;
    private Integer mana;
    private Double cordura;
    private List<Espiritu> espiritus;
    private Ubicacion ubicacion;
    private Coordenadas coordenadas;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana, @NonNull Ubicacion ubicacion) {
        this.verificarManayManaMax(manaMax, mana);
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.cordura = 100d;
        this.ubicacion = ubicacion;
        this.espiritus = new ArrayList<>();
        this.coordenadas = ubicacion.generarCoordenadasAleatorias();
    }

    public void verificarManayManaMax(Integer manaMax, Integer mana) {
        if (esManaNegativo(manaMax, mana)) {
            throw new ManaNegativoException();
        }
        if (elManaEsMayorAlMaximo(manaMax, mana)) {
            throw new ManaSuperaManaMaxException();
        }
    }

    private static boolean elManaEsMayorAlMaximo(Integer manaMax, Integer mana) {
        return manaMax < mana;
    }

    private static boolean esManaNegativo(Integer manaMax, Integer mana) {
        return manaMax < 0 || mana < 0;
    }

    public void conectarseAEspiritu(Espiritu espiritu) {
        this.validarEspirituLibre(espiritu);
        this.validarEspirituNoDominado(espiritu);
        this.validarUbicacionEspirituConMedium(espiritu, this);
        espiritu.conectarConMedium(this);
        espiritus.add(espiritu);
    }

    public void descansar(){
        this.recuperarMana(this.cantManaARecuperar());
        if (noTieneEspiritus()) {
            this.recuperarConexionDeLosEspiritus();
        }
    }

    private boolean noTieneEspiritus() {
        return !espiritus.isEmpty();
    }

    private int cantManaARecuperar() {
        if (this.ubicacion.esCementerio()){
            return this.ubicacion.getFlujoEnergia() / 2;
        } else {
            return (int) (this.ubicacion.getFlujoEnergia() * 1.5);
        }
    }

    public void recuperarConexionDeLosEspiritus() {
        for (Espiritu espiritu : espiritus) {
            recuperarSiPuede(espiritu);
        }
    }

    private void recuperarSiPuede(Espiritu espiritu) {
        if (espiritu.esUbicacionDominante(this.ubicacion)){
            int conexionARecuperar = Math.min(this.ubicacion.getFlujoEnergia(),
                    Espiritu.NIVEL_MAXIMO - espiritu.getNivelDeConexion());
            espiritu.recuperarConexion(conexionARecuperar);
        }
    }

    private void recuperarMana(int nivelRecuperacion) {
        int manaARecuperar = Math.min(nivelRecuperacion, this.manaMax - this.mana);
        this.mana += manaARecuperar;
    }

    public void invocarA(Espiritu espiritu) {
        this.validarEspirituLibre(espiritu);
        if(this.puedeInvocar(espiritu)) {
            espiritu.serInvocadoPor(this);
            this.perderMana(COSTO_MANA_INVOCACION);
        }
    }

    private void validarEspirituLibre(Espiritu espiritu) {
        if (!espiritu.esLibre()) {
            throw new EspirituConectadoException();
        }
    }

    private void validarEspirituNoDominado(Espiritu espiritu) {
        if (espiritu.estaDominado()) {
            throw new EspirituDominadoException();
        }
    }

    public boolean puedeInvocar(Espiritu espiritu){
        this.validarTipoUbicacion(espiritu);
        return this.getMana() >= COSTO_MANA_INVOCACION;
    }

    private void validarTipoUbicacion(Espiritu espiritu) {
        if (!espiritu.esUbicacionDominante(this.ubicacion)){
            throw new DiferenteUbicacionException("El espiritu no puede ser invocado porque la ubicacion no es de su tipo correspondiente.");
        }
    }

    public void perderMana(int costoAPerder) {
        this.mana = Math.max(0, this.mana - costoAPerder);
    }

    private void validarUbicacionEspirituConMedium(Espiritu espiritu, Medium medium) {
        boolean tienenMismaUbicacion = espiritu.getUbicacion().getId().equals(medium.getUbicacion().getId());
        if (!tienenMismaUbicacion) {
            throw new DiferenteUbicacionException("La ubicacion del Medium es distinta a la del Espiritu");
        }
    }

    /**
     * Realiza el exorcismo sobre el Medium indicado, usando atacantes para intentar eliminar sus defensores.
     * @param victima El Medium a ser exorcizado.
     */
    public void exorcizar(List<Angel> angelesExorcista, Medium victima ){
        validarQueHayaAngeles(angelesExorcista);
        validarMismaUbicacion(this,victima);
        List<Espiritu> espiritusDeLaVictima = victima.getEspiritus();
        for (Espiritu angel : angelesExorcista) {
            if(espiritusDeLaVictima.isEmpty()) break;
            procesarAtaqueDeAtacante(angel, espiritusDeLaVictima);
        }
    }

    private void validarMismaUbicacion(Medium exorcista, Medium victima ) {
        if (!laUbicacionDelExorcistaEsLaMismaQueLaDeLaVictima(exorcista, victima)) {
            throw new DiferenteUbicacionException("El exorcista y su victima se encuentran en diferentes ubicaciones.");
        }
    }

    private static boolean laUbicacionDelExorcistaEsLaMismaQueLaDeLaVictima(Medium exorcista, Medium victima) {
        return exorcista.getUbicacion().getId().equals(victima.getUbicacion().getId());
    }

    private void validarQueHayaAngeles(List<Angel> angelesExorcista) {
        if (angelesExorcista.isEmpty()) {
            throw new ExorcistaSinAtacantesException();
        }
    }

    private void procesarAtaqueDeAtacante(Espiritu angel, List<Espiritu> demoniosVictima) {
        Espiritu defensorObjetivo = demoniosVictima.getFirst();
        angel.atacarA(defensorObjetivo);
    }

    /**
     * Desconecta al espiritu del medium actual.
     * @param espiritu El espiritu a desconectar.
     */
    public void desconectarEspiritu(Espiritu espiritu) {
        espiritus.remove(espiritu);
    }

    public void moverA(Ubicacion ubicacionDestino, Double latitud, Double longitud) {
        this.validarQuePuedaLlegarALaUbicacionDesdeLaUbicacionActual(ubicacionDestino);
        this.validarDistanciaARecorrerMenorA30(latitud, longitud);
        int costo = this.ubicacion.costoHacia(ubicacionDestino);
        // Actualizo ubicación y coordenadas
        this.ubicacion = ubicacionDestino;
        this.coordenadas = new Coordenadas(longitud, latitud);
        this.moverEspiritus(ubicacionDestino, latitud, longitud);
        this.perderMana(costo);
    }

    private void validarDistanciaARecorrerMenorA30(Double latitud, Double longitud) {
        Coordenadas coordenadasDestino = new Coordenadas(latitud, longitud);
        double distancia = this.getCoordenadas().calcularDistanciaA(coordenadasDestino);

        if (distancia > 30) {
            throw new UbicacionLejanaException("La ubicación destino está a más de 30 km de la actual.");
        }
    }

    private void validarQuePuedaLlegarALaUbicacionDesdeLaUbicacionActual(Ubicacion ubicacionDestino) {
        if(!(this.ubicacion.estaConectadaCon(ubicacionDestino))){
            throw new UbicacionLejanaException();
        }
    }

    private void moverEspiritus(Ubicacion ubicacion, Double latitud, Double longitud) {
        for(Espiritu espiritu : List.copyOf(this.espiritus)){
            espiritu.moverA(ubicacion, latitud, longitud);
        }
    }

    public boolean tieneMana() {
        return mana > 0;
    }

    @Override
    public String getRef() {
        return "Medium";
    }

    public List<Espiritu> desconectarTodosLosEspiritus() {
        List<Espiritu> liberados = new ArrayList<>(this.espiritus);


        for (Espiritu e : liberados) {
            e.setMedium(null); // el Espíritu queda “libre”
        }

        // El Medium limpia su lista
        this.espiritus.clear();

        return liberados;
    }


    public double getCorduraNormalizada() {
        return ((Math.max(CORDURA_MINIMA, Math.min(CORDURA_MAXIMA, cordura))) - CORDURA_MINIMA) / (CORDURA_MAXIMA - CORDURA_MINIMA);
    }

    public void reducirCorduraPorHostilidadDe(Espiritu espiritu) {
        double h = Math.min(CORDURA_MAXIMA, Math.max(CORDURA_MINIMA, espiritu.getHostilidad()));

        double perdida = h / 3.0;

        this.reducirCordura(perdida);
    }

    public void reducirCordura(double corduraAReducir) {
        this.setCordura(Math.max(CORDURA_MINIMA, this.getCordura() - corduraAReducir));
    }
}