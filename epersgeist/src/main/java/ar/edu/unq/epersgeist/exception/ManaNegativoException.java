package ar.edu.unq.epersgeist.exception;

public class ManaNegativoException extends ConflictException{

    public ManaNegativoException() {super("El mana y el manaMax no pueden ser negativos.");}
}
