package ar.edu.unq.epersgeist.controller.dto.medium;
import ar.edu.unq.epersgeist.modelo.Medium;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ActualizarMediumDTO(
        String nombre,
        Integer manaMax,
        Integer mana,
        Long ubicacionId,
        Integer cordura
) {

    public void sobrescribir(Medium medium) {
        if (this.nombre() != null)
            medium.setNombre(this.nombre());

        if (this.manaMax() != null)
            medium.setManaMax(this.manaMax());

        if (this.mana() != null)
            medium.setMana(this.mana());
    }
}