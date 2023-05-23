package clases;
import java.time.LocalDateTime;
import enumeraciones.Categoria;

public class Noticia extends ElementoConNombre {
//Atributos
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private Usuario autor;
    private Categoria categoria;
    private boolean noticiaPremium;
//Constructor
    public Noticia(String nombre, String contenido, LocalDateTime fechaPublicacion, Usuario autor, Categoria categoria, boolean noticiaPremium) {
        super(nombre);
        this.contenido = contenido;
        this.fechaPublicacion = fechaPublicacion;
        this.autor = autor;
        this.categoria = categoria;
        this.noticiaPremium = noticiaPremium;
    }

    // Getters and Setters

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean isNoticiaPremium() {
        return noticiaPremium;
    }

    public void setNoticiaPremium(boolean noticiaPremium) {
        this.noticiaPremium = noticiaPremium;
    }

    // Los métodos crear_noticia(), actualizar_noticia() y eliminar_noticia() deben implementarse según la lógica de tu aplicación.

}
