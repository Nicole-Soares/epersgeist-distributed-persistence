package ar.edu.unq.epersgeist.controller.dto.ubicacion;

import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;

public enum TipoUbicacionDTO {
    CEMENTERIO, SANTUARIO;

    public static TipoUbicacionDTO desdeModelo(TipoUbicacion tipoUbicacion){
        return TipoUbicacionDTO.valueOf(tipoUbicacion.name());
    }

    public TipoUbicacion aModelo(){
        return TipoUbicacion.valueOf(this.name());
    }
}
