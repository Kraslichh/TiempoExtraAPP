package clases;

import java.time.LocalDate;
import enumeraciones.Categoria;

public class SuscripcionAnual extends Suscripcion {
	//Atributo
    private float descuento;
//Constructor
    public SuscripcionAnual(String nombre, float precioPorMes, Categoria categoria, LocalDate fechaInicio, LocalDate fechaFin, float descuento) {
        super(nombre, precioPorMes, categoria, fechaInicio, fechaFin);
        this.descuento = descuento;
    }

    // Getters y Setters
    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    // Sobreescribir getPrecio()
    @Override
    public float getPrecioPorMes() {
        return super.getPrecioPorMes() * 12 - descuento;
    }
}