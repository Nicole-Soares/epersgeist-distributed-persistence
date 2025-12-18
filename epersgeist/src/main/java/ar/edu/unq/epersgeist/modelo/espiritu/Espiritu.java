package ar.edu.unq.epersgeist.modelo.espiritu;

import ar.edu.unq.epersgeist.exception.DominacionInvalidaException;
import ar.edu.unq.epersgeist.exception.DominioCircularException;
import ar.edu.unq.epersgeist.exception.EspirituNoPuedeMoverseSoloException;
import ar.edu.unq.epersgeist.exception.MaximoNivelConexionException;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.EntidadCoordenadaInfo;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import lombok.*;

@Data
@NoArgsConstructor
public abstract class Espiritu implements EntidadCoordenadaInfo {

    public final static int NIVEL_BASE = 0;
    public final static int NIVEL_MAXIMO = 100;

    public final static int HOSTILIDAD_MIN = 0;
    public final static int HOSTILIDAD_MAX = 100; // MIN < MAX SIEMPRE

    private Long id;
    private Integer nivelDeConexion;
    private String nombre;
    private Ubicacion ubicacion;
    private Medium medium;
    private Coordenadas coordenadas;
    private Espiritu espirituDominante;
    private Double hostilidad;

    public Espiritu(@NonNull String nombre, @NonNull Ubicacion ubicacion, @NonNull Double hostilidad) {
        this.nivelDeConexion = NIVEL_BASE;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.coordenadas = ubicacion.generarCoordenadasAleatorias();
        this.hostilidad = Math.max(HOSTILIDAD_MIN, Math.min(HOSTILIDAD_MAX, hostilidad));
    }

    public void conectarConMedium(Medium medium) {
        final double PORCENTAJE_MANA_FORTALECIMIENTO = 0.20;
        final int NIVEL_FORTALECIMIENTO = (int) (medium.getMana() * PORCENTAJE_MANA_FORTALECIMIENTO);
        this.validarMaximoNivelConexion(NIVEL_FORTALECIMIENTO, medium);
        this.nivelDeConexion += NIVEL_FORTALECIMIENTO;
        this.medium = medium;
    }

    private void validarMaximoNivelConexion(int nivelFortalecimiento, Medium medium) {
        if ((this.nivelDeConexion + nivelFortalecimiento) > NIVEL_MAXIMO){
            throw new MaximoNivelConexionException();
        }
    }

    public boolean esLibre() {
        return this.medium == null;
    }

    public boolean estaDominado(){ return this.espirituDominante != null; }

    public void recuperarConexion(int recuperacion) {
        this.nivelDeConexion += recuperacion;
    }

    public void serInvocadoPor(Medium medium){
        ubicacion = medium.getUbicacion();
        coordenadas = medium.getCoordenadas();
    }

    /**
     * El espiritu es invocado en la ubicacion dada y deja de ser libre.
     * @param cantidad La cantidad de nivel de conexion que pierde el espiritu.
     */
    public void perderConexion(int cantidad) {
        this.nivelDeConexion -= cantidad;
        if (this.nivelDeConexion <= 0) {
            this.nivelDeConexion = 0;
            this.desconectarDelMediumActual();
        }
    }

    /**
     * El espiritu es desvinculado del medium con el que estaba conectado. Si no estaba conectado a ningun medium, no hace nada.
     */
    public void desconectarDelMediumActual() {
        if (this.medium != null) {
            this.medium.desconectarEspiritu(this);
            this.medium = null;
        }
    }

    public abstract boolean esUbicacionDominante(Ubicacion ubicacion);

    public  void atacarA(Espiritu espiritu){};

    public void defenderContra(Angel atacante, int poderOfensivo){};

    public void moverA(Ubicacion ubicacion, Double latitud, Double longitud) {
        this.validarMovimientoCorrecto();
        this.ubicacion = ubicacion;
        this.coordenadas = new Coordenadas(longitud, latitud);
        if (!this.esUbicacionDominante(ubicacion)){
            this.perderConexion(this.perdidaDeConexionMovimiento());
        }
    }

    private void validarMovimientoCorrecto() {
        if (this.esLibre()){
            throw new EspirituNoPuedeMoverseSoloException();
        }
    }

    public void dominar(Espiritu espirituADominar){
        this.validarDominable(espirituADominar);
        this.validarDominioCircular(espirituADominar);
        espirituADominar.serDominadoPor(this);
    }

    private void validarDominable(Espiritu espirituADominar) {
        if (!this.estaEnRango(espirituADominar)){
            throw new DominacionInvalidaException("El espíritu a dominar está fuera de rango.");
        }
        if(!espirituADominar.esLibre()){
            throw new DominacionInvalidaException("El espíritu a dominar está conectado a un medium.");
        }
        if(espirituADominar.getNivelDeConexion() >= 50){
            throw new DominacionInvalidaException("El espíritu a dominar tiene una energía mayor o igual a 50.");
        }
    }

    private void validarDominioCircular(Espiritu espirituADominar){
        if(this.equals(espirituADominar.getEspirituDominante())){
            throw new DominioCircularException();
        }
    }

    public void serDominadoPor(Espiritu espirituDominante){
        this.espirituDominante = espirituDominante;
    }

    private boolean estaEnRango(Espiritu espirituADominar){
        double distancia = this.getCoordenadas().calcularDistanciaA(espirituADominar.getCoordenadas());

        final double RANGO_MINIMO = 2.0;
        final double RANGO_MAXIMO = 5.0;

        return distancia >= RANGO_MINIMO && distancia <= RANGO_MAXIMO;
    }

    protected int perdidaDeConexionMovimiento(){
        return 0;
    }


    public double getHostilidadNormalizada() {
        return ((Math.max(HOSTILIDAD_MIN, Math.min(HOSTILIDAD_MAX, hostilidad))) - HOSTILIDAD_MIN) / (HOSTILIDAD_MAX - HOSTILIDAD_MIN);
    }
}