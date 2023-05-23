package clases;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import conector.DatabaseConnector;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

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
    public static void crear_noticia(String nombre, String contenido, LocalDateTime fechaPublicacion, Usuario autor, Categoria categoria, boolean noticiaPremium) throws ConexionFallidaException {
        // Consulta SQL para insertar una nueva noticia
        String sql = "INSERT INTO noticia (titulo, contenido, fechaPublicacion, autor_id, categoria, noticiaPremium) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los valores de los parámetros de la consulta SQL
            stmt.setString(1, nombre);
            stmt.setString(2, contenido);
            stmt.setTimestamp(3, Timestamp.valueOf(fechaPublicacion));  // Transformar LocalDateTime a Timestamp
            stmt.setInt(4, Usuario.getIdPorNombreUsuario(autor.getNombreUsuario())); // Aquí se pasa el id del autor
            stmt.setString(5, categoria.name());
            stmt.setBoolean(6, noticiaPremium);

            // Ejecutar la consulta SQL
            int rowsAffected = stmt.executeUpdate();

            // Verificar si la inserción fue exitosa
            if (rowsAffected == 1) {
                System.out.println("Noticia creada exitosamente.");
            } else {
                System.out.println("No se pudo crear la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al crear la noticia: " + e.getMessage());
        }
    }
    public static List<Noticia> mostrar_noticias() throws ConexionFallidaException {
        List<Noticia> noticias = new ArrayList<>();

        // Consulta SQL para obtener todas las noticias
        String sql = "SELECT * FROM noticia";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Iterar a través de los resultados de la consulta
            while (rs.next()) {
                String nombre = rs.getString("titulo");
                String contenido = rs.getString("contenido");
                LocalDateTime fechaPublicacion = rs.getTimestamp("fechaPublicacion").toLocalDateTime();
                // Aquí asumimos que hay un método estático Usuario.getUsuarioPorId() que retorna un Usuario dado su ID.
                Usuario autor = Usuario.getUsuarioPorId(rs.getInt("autor_id"));  
                Categoria categoria = Categoria.valueOf(rs.getString("categoria"));
                boolean noticiaPremium = rs.getBoolean("noticiaPremium");

                // Crear una nueva noticia con los datos obtenidos y añadirla a la lista
                noticias.add(new Noticia(nombre, contenido, fechaPublicacion, autor, categoria, noticiaPremium));
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al mostrar noticias: " + e.getMessage());
        }

        return noticias;
    }
    
}
