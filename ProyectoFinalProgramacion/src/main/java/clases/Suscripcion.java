package clases;
import enumeraciones.Categoria;
import java.time.LocalDate;

public class Suscripcion extends ElementoConNombre {
	//Atributos
    private float precioPorMes;
    private Categoria categoria;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    //Constructor
    public Suscripcion(String nombre, float precioPorMes, Categoria categoria, LocalDate fechaInicio, LocalDate fechaFin) {
        super(nombre);
        this.precioPorMes = precioPorMes;
        this.categoria = categoria;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public float getPrecioPorMes() {
        return precioPorMes;
    }

    public void setPrecioPorMes(float precioPorMes) {
        this.precioPorMes = precioPorMes;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
