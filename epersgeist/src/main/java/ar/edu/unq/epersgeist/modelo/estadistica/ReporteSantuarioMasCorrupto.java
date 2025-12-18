package ar.edu.unq.epersgeist.modelo.estadistica;

import ar.edu.unq.epersgeist.modelo.Medium;

public record ReporteSantuarioMasCorrupto(String nombreSantuario,
                                          Medium mediumConMasDemonios,
                                          Integer cantidadTotalDemonios,
                                          Integer cantidadDemoniosLibres) {}
