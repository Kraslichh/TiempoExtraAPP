package clases;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import conector.DatabaseConnector;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

public class Usuario extends ElementoConNombre {
    // Atributos
    private String nombreUsuario;
    private String contraseña;
    private boolean isEditor;
    private boolean isAdmin;
    private HashSet<Suscripcion> suscripcionesActivas;
    private List<Noticia> noticiasCreadas;

    // Constructores
    public Usuario(String nombre, String nombreUsuario, String contraseña, boolean isEditor, boolean isAdmin) {
        super(nombre);
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.suscripcionesActivas = new HashSet<>();
        this.noticiasCreadas = new ArrayList<>();
    }

    // Getters y Setters

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean isEditor) {
        this.isEditor = isEditor;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public HashSet<Suscripcion> getSuscripcionesActivas() {
        return suscripcionesActivas;
    }

    public void addSuscripcion(Suscripcion suscripcion) {
        this.suscripcionesActivas.add(suscripcion);
    }

    public void removeSuscripcion(Suscripcion suscripcion) {
        this.suscripcionesActivas.remove(suscripcion);
    }

    public List<Noticia> getNoticiasCreadas() throws ConexionFallidaException {
        // Consulta SQL para obtener las noticias creadas por el usuario
        String sql = "SELECT * FROM noticia WHERE autor_id IN (SELECT id FROM usuario WHERE nombreUsuario = ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.nombreUsuario);

            ResultSet rs = stmt.executeQuery();

            // Limpiar la lista actual antes de cargar las noticias desde la base de datos
            noticiasCreadas.clear();

            // Procesar el ResultSet y cargar las noticias en la lista
            while (rs.next()) {
                Noticia noticia = new Noticia(
                    rs.getString("elementoConNombre_nombre"),
                    rs.getString("contenido"),
                    rs.getTimestamp("fechaPublicacion").toLocalDateTime(),
                    this,  // Autor
                    Categoria.valueOf(rs.getString("categoria")),
                    rs.getBoolean("noticiaPremium")
                );
                noticiasCreadas.add(noticia);
            }
        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener las noticias creadas: " + e.getMessage());
        }

        return noticiasCreadas;
    }

    public void setNoticiasCreadas(List<Noticia> noticiasCreadas) {
        this.noticiasCreadas = noticiasCreadas;
    }

    // Los métodos iniciar_sesion(), cerrar_sesion() y registrar_usuario() deben implementarse según la lógica de tu aplicación.
    public void iniciar_sesion(String username, String password) {
        // Supongamos que tienes un objeto de conexión a la base de datos
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
        } catch (ConexionFallidaException e1) {
            e1.printStackTrace();
        }

        // Aquí almacenamos en una variable String llamada query la línea de SQL donde modificaremos
        // o en este caso seleccionaremos
        String query = "SELECT * FROM usuario WHERE nombreUsuario = ? AND contraseña = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("¡Inicio de sesión exitoso!");
                // Establecer las propiedades del usuario según los resultados de la consulta
                this.isEditor = resultSet.getBoolean("isEditor");
                this.isAdmin = resultSet.getBoolean("isAdmin");
            } else {
                System.out.println("¡Nombre de usuario o contraseña incorrectos!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registrar_usuario(String nombreUsuario, String contraseña) throws SQLException, ConexionFallidaException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Verifica si el nombre de usuario ya existe
            String checkQuery = "SELECT * FROM Usuario WHERE nombreUsuario = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, nombreUsuario);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                throw new SQLException("El nombre de usuario ya existe");
            }

            String query = "INSERT INTO Usuario (nombreUsuario, contraseña, isAdmin, isEditor) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombreUsuario);
            statement.setString(2, contraseña);
            statement.setBoolean(3, false); // isAdmin siempre se establece en false al registrarse
            statement.setBoolean(4, false); // isEditor siempre se establece en false al registrarse
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Usuario registrado exitosamente!");
            }
            connection.close();
        }
    }

    public static int getIdPorNombreUsuario(String nombreUsuario) throws ConexionFallidaException {
        // Consulta SQL para obtener el ID del usuario por su nombre de usuario
        String sql = "SELECT id FROM usuario WHERE nombreUsuario = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Verificar si se encontró el ID del usuario
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new ConexionFallidaException("No se pudo obtener el ID del usuario.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener el ID del usuario: " + e.getMessage());
        }
    }
    
    // Método para obtener un Usuario dado su ID
    public static Usuario getUsuarioPorId(int id) {
        // Consulta SQL para obtener el usuario con el ID dado
        String sql = "SELECT * FROM usuario WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el parámetro del ID en la consulta SQL
            stmt.setInt(1, id);

            // Ejecutar la consulta SQL y obtener los resultados
            ResultSet rs = stmt.executeQuery();

            // Si hay un resultado, crear un nuevo Usuario y retornarlo
            if (rs.next()) {
                return new Usuario(
                        rs.getString("elementoConNombre_nombre"),
                        rs.getString("nombreUsuario"),
                        rs.getString("contraseña"),
                        rs.getBoolean("isEditor"),
                        rs.getBoolean("isAdmin")
                );
            }

        } catch (SQLException | ConexionFallidaException e) {
            System.out.println("Error al obtener el usuario: " + e.getMessage());
        }

        // Si no se encontró ningún usuario, retornar null
        return null;
    }
    public static boolean esAdmin(String nombreUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Obtener la conexión a la base de datos
            conn = DatabaseConnector.getConnection();

            // Consultar la base de datos para verificar si el usuario es administrador
            String query = "SELECT isAdmin FROM usuario WHERE nombreUsuario = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nombreUsuario);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int isAdmin = rs.getInt("isAdmin");
                return isAdmin == 1; // Si isAdmin es igual a 1, el usuario es administrador
            }
        } catch (SQLException | ConexionFallidaException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false; // Si no se encontró al usuario o ocurrió un error, asumimos que no es administrador
    }
}


