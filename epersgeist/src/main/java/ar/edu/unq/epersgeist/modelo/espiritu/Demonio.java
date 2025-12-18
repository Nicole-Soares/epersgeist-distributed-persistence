package ar.edu.unq.epersgeist.modelo.espiritu;

import ar.edu.unq.epersgeist.modelo.GeneradorNumeroAleatorio;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Demonio extends Espiritu{

    public Demonio(String nombre, Ubicacion ubicacion, Double hostilidad) {
        super(nombre, ubicacion, hostilidad);
    }

    /**
     * Calcula el valor de defensa del defensor sumando un número aleatorio generado por el generador
     * @return el valor de defensa calculado
     */
    public int calcularDefensa() {
        return (int) (GeneradorNumeroAleatorio.getInstance().numeroDoubleRandom()*99) + 1 ;
    }

    @Override
    public void defenderContra(Angel atacante, int porcentajeAtaque) {
        if(esExitosoElAtaque(porcentajeAtaque)) {
            this.perderConexion(atacante.dañoDeAtaqueCausado());
        } else {
            atacante.penalizarFalloDeAtaque();
        }
    }

    private boolean esExitosoElAtaque(int porcentajeAtaque) {
        return porcentajeAtaque > this.calcularDefensa();
    }

    @Override
    public boolean esUbicacionDominante(Ubicacion ubicacion) {
        return ubicacion.esCementerio();
    }

    @Override
    protected int perdidaDeConexionMovimiento(){
        return 10;
    }

    @Override
    public String getRef() {
        return "Demonio";
    }
}
