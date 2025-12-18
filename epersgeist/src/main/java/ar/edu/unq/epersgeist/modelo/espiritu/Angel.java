package ar.edu.unq.epersgeist.modelo.espiritu;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Angel extends Espiritu {

    public Angel(String nombre, Ubicacion ubicacion, Double hostilidad) {
        super(nombre, ubicacion, hostilidad);
    }

    @Override
    public void atacarA(Espiritu espiritu) {
        espiritu.defenderContra(this, limiteMaximoDeAtaque(calculoDeAtaque()));
    }

    private int calculoDeAtaque() {
        return numeroRandomEntre1y10() + getNivelDeConexion();
    }

    private int limiteMaximoDeAtaque(int ataque) {
        final int NUMERO_MAXIMO_POSIBLE = 100;
        return Math.min(ataque, NUMERO_MAXIMO_POSIBLE);
    }

    private int numeroRandomEntre1y10() {
        return (int) (GeneradorNumeroAleatorio.getInstance().numeroDoubleRandom() * 9) + 1;
    }


    public int dañoDeAtaqueCausado() {
        return this.getNivelDeConexion() / 2;
    }


    public void penalizarFalloDeAtaque() {
        this.perderConexion(5);
    }

    @Override
    public boolean esUbicacionDominante(Ubicacion ubicacion) {
        return ubicacion.esSantuario();
    }

    @Override
    protected int perdidaDeConexionMovimiento(){
        return 5;
    }

    @Override
    public String getRef() {
        return "Angel";
    }
}
