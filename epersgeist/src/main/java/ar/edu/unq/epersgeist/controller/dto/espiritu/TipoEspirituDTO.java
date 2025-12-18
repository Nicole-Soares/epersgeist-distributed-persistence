package ar.edu.unq.epersgeist.controller.dto.espiritu;

import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;

public enum TipoEspirituDTO {
    ANGELICAL {
        @Override
        public Espiritu crear(String nombre, Ubicacion ubicacion, Double hostilidad) {
            return new Angel(nombre, ubicacion, hostilidad);
        }
    },

    DEMONIO {
        @Override
        public Espiritu crear(String nombre, Ubicacion ubicacion, Double hostilidad) {
            return new Demonio(nombre, ubicacion, hostilidad);
        }
    };

    public abstract Espiritu crear(String nombre, Ubicacion ubicacion, Double hostilidad);
}
