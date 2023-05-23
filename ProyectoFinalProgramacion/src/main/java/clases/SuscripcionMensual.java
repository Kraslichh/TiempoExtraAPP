package clases;
import enumeraciones.Categoria;
import java.time.LocalDate;

public class SuscripcionMensual extends Suscripcion {

	//Constructor
    public SuscripcionMensual(String nombre, float precioPorMes, Categoria categoria, LocalDate fechaInicio, LocalDate fechaFin) {
        super(nombre, precioPorMes, categoria, fechaInicio, fechaFin);
    }
}
