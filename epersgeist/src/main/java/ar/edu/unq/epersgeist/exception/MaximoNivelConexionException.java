package ar.edu.unq.epersgeist.exception;

import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

public class MaximoNivelConexionException extends ConflictException{
    public MaximoNivelConexionException(){
        super("El espiritu no puede conectarse con el medium porque supera su nivel de conexion maximo permitido");
    }

}