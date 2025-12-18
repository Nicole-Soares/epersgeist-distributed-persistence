package ar.edu.unq.epersgeist.modelo;

import java.util.Random;

public class GeneradorNumeroAleatorio implements GeneradorAleatorio {

    private static GeneradorNumeroAleatorio instance = null;
    private Random random;
    private int min;
    private int max;

    private GeneradorNumeroAleatorio() {
        this.random = new Random();
        this.min = 0;
        this.max = 100;
    }

    /**
     * Obtiene la instancia única del generador de números aleatorios (patrón Singleton).
     * @return La instancia única del generador de números aleatorios.
     */
    public static GeneradorNumeroAleatorio getInstance() {
        if (instance == null) {
            instance = new GeneradorNumeroAleatorio();
        }
        return instance;
    }

    /**
     * Genera un número decimal aleatorio entre min y max, dividido por 100. Ejemplo: si min=0 y max=100, el resultado estará en el rango [0;1] . Por defecto, min=0 y max=100.
     * @return Número decimal aleatorio entre min y max, dividido por 100.
     */
    @Override
    public double numeroDoubleRandom() {
        return (random.nextInt((max - min) + 1) + min) * 0.01;
    }
}