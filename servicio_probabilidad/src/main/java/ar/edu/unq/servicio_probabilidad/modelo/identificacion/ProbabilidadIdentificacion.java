package ar.edu.unq.servicio_probabilidad.modelo.identificacion;

import java.util.List;

public class ProbabilidadIdentificacion {


    // ----------- Probabilidad Promedio ---------------


    public double probabilidadPromedio(List<Double> listaDeValoresNormalizados) {
        if (listaDeValoresNormalizados == null || listaDeValoresNormalizados.isEmpty()) {return 0.0;}

        return listaDeValoresNormalizados.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }


    // ----------------- Nombre ------------------

    /** Más parecido el nombre = más probabilidad */
    public double bonusPorNombre(String real, String conjetura) {
        real = real.toLowerCase().trim();
        conjetura = conjetura.toLowerCase().trim();

        if (real.equals(conjetura)) return 1;

        // similitud con Levenshtein
        int max = Math.max(real.length(), conjetura.length());
        int dist = levenshtein(real, conjetura);

        double similitud = 1 - (dist / (double) max);

        return similitud;
    }

    private int levenshtein(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        int n = a.length();
        int m = b.length();
        if (n == 0) return m;
        if (m == 0) return n;

        if (m > n) {
            String tmp = a; a = b; b = tmp;
            int tmpLen = n; n = m; m = tmpLen;
        }

        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;

        for (int i = 1; i <= n; i++) {
            curr[0] = i;
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int cost = (ca == b.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(prev[j] + 1, curr[j - 1] + 1), prev[j - 1] + cost);
            }
            int[] tmp = prev; prev = curr; curr = tmp;
        }
        return prev[m];
    }


    // ----------------- Tipo ------------------

    public double bonusPorTipo(String real, String conjetura) {
        if (real == null || conjetura == null) return 0;
        real = real.trim().toLowerCase();
        conjetura = conjetura.trim().toLowerCase();
        return real.equals(conjetura) ? 1 : 0;
    }
}
