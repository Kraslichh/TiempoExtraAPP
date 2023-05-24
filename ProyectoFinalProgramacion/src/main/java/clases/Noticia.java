package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import conector.DatabaseConnector;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

public class Noticia extends ElementoConNombre {
    // Atributos
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private Usuario autor;
    private Categoria categoria;
    private boolean noticiaPremium;

    // Constructor
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

    // Los métodos crear_noticia(), mostrar_noticias(), actualizar_noticia() y eliminar_noticia() deben implementarse según la lógica de tu aplicación.

    public static void crear_noticia(String nombre, String contenido, LocalDateTime fechaPublicacion, Usuario autor, Categoria categoria, boolean noticiaPremium) throws ConexionFallidaException {
        // Consulta SQL para insertar una nueva noticia
        String sql = "INSERT INTO noticia (elementoConNombre_nombre, contenido, fechaPublicacion, autor_id, categoria, noticiaPremium) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los valores de los parámetros de la consulta SQL
            stmt.setString(1, nombre);
            stmt.setString(2, contenido);
            stmt.setTimestamp(3, Timestamp.valueOf(fechaPublicacion)); // Transformar LocalDateTime a Timestamp
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

    public static List<Noticia> getNoticiasCreadas(int autorId) throws ConexionFallidaException {
        // Lista para almacenar las noticias recuperadas
        List<Noticia> noticias = new ArrayList<>();

        // Consulta SQL para seleccionar noticias del autor
        String sql = "SELECT * FROM noticia WHERE autor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el valor del autor_id
            stmt.setInt(1, autorId);

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Procesar el ResultSet
            while (rs.next()) {
                Usuario autor = Usuario.getUsuarioPorId(rs.getInt("autor_id"));
                Noticia noticia = new Noticia(
                    rs.getString("elementoConNombre_nombre"), // nombre
                    rs.getString("contenido"), // contenido
                    rs.getTimestamp("fechaPublicacion").toLocalDateTime(), // fechaPublicacion
                    autor, // autor
                    Categoria.valueOf(rs.getString("categoria")), // categoria
                    rs.getBoolean("noticiaPremium") // noticiaPremium
                );
                
                // Añadir la noticia a la lista
                noticias.add(noticia);
                System.out.println("Noticia añadida: " + noticia.getNombre()); // Imprimir el nombre de la noticia añadida
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener noticias creadas: " + e.getMessage());
        }

        System.out.println("Se encontraron " + noticias.size() + " noticias para el autor con ID " + autorId); // Imprimir el número de noticias encontradas
        return noticias;
    }
    public void actualizar_noticia_completa(String nuevoNombre, String nuevoContenido, Categoria nuevaCategoria, boolean nuevaNoticiaPremium) throws ConexionFallidaException {
        // Consulta SQL para actualizar la noticia
        String sql = "UPDATE noticia SET elementoConNombre_nombre = ?, contenido = ?, categoria = ?, noticiaPremium = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los valores de los parámetros de la consulta SQL
            stmt.setString(1, nuevoNombre);
            stmt.setString(2, nuevoContenido);
            stmt.setString(3, nuevaCategoria.name());
            stmt.setBoolean(4, nuevaNoticiaPremium);
            stmt.setInt(5, this.getIdFromDatabase());

            // Ejecutar la consulta SQL
            int rowsAffected = stmt.executeUpdate();

            // Verificar si la actualización fue exitosa
            if (rowsAffected == 1) {
                System.out.println("Noticia actualizada exitosamente.");
            } else {
                System.out.println("No se pudo actualizar la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al actualizar la noticia: " + e.getMessage());
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
                String nombre = rs.getString("elementoConNombre_nombre");
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

    public void actualizar_noticia(String nuevoNombre) throws ConexionFallidaException {
        // Buscar la noticia en la lista de noticias creadas por el autor
        Optional<Noticia> noticiaOptional = autor.getNoticiasCreadas().stream()
                .filter(noticia -> noticia.getNombre().equals(this.getNombre()))
                .findFirst();

        if (noticiaOptional.isPresent()) {
            Noticia noticiaEncontrada = noticiaOptional.get();
            // Actualizar el nombre de la noticia
            noticiaEncontrada.setNombre(nuevoNombre);
            System.out.println("Noticia actualizada exitosamente.");
        } else {
            System.out.println("No se pudo encontrar la noticia.");
        }
    }
    public int getIdFromDatabase() throws ConexionFallidaException {
        // Consulta SQL para obtener el ID de la noticia desde la base de datos
        String sql = "SELECT id FROM noticia WHERE elementoConNombre_nombre = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.getNombre());

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Verificar si se encontró el ID de la noticia
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new ConexionFallidaException("No se pudo obtener el ID de la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener el ID de la noticia: " + e.getMessage());
        }
    }


    public static List<Noticia> getNoticiasCreadas(Usuario usuario) throws ConexionFallidaException {
        List<Noticia> noticias = new ArrayList<>();

        // Consulta SQL para obtener todas las noticias creadas por el usuario
        String sql = "SELECT * FROM noticia WHERE autor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        	stmt.setInt(1, Usuario.getIdPorNombreUsuario(usuario.getNombre()));

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Procesar el ResultSet
            while (rs.next()) {
                Noticia noticia = new Noticia(
                    rs.getString("elementoConNombre_nombre"),
                    rs.getString("contenido"),
                    rs.getTimestamp("fechaPublicacion").toLocalDateTime(),
                    usuario,  // Autor
                    Categoria.valueOf(rs.getString("categoria")),
                    rs.getBoolean("noticiaPremium")
                );
                noticias.add(noticia);
            }
        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener las noticias creadas: " + e.getMessage());
        }

        return noticias;
    }

    
    public void eliminar_noticia() throws ConexionFallidaException {
        // Consulta SQL para eliminar la noticia
        String sql = "DELETE FROM noticia WHERE elementoConNombre_nombre = ?";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el valor del parámetro de la consulta SQL
            stmt.setString(1, getNombre());

            // Ejecutar la consulta SQL
            int rowsAffected = stmt.executeUpdate();

            // Verificar si la eliminación fue exitosa
            if (rowsAffected == 1) {
                System.out.println("Noticia eliminada exitosamente.");
            } else {
                System.out.println("No se pudo eliminar la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al eliminar la noticia: " + e.getMessage());
        }
    }
}