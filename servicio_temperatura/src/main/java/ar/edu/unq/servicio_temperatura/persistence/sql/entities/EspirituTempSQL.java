package ar.edu.unq.servicio_temperatura.persistence.sql.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "espiritu")
public class EspirituTempSQL {
    @Id
    private Long id;
    private Double hostilidad;
}