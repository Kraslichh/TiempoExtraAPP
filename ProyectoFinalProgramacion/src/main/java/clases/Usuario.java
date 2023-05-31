package clases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private static int idUsuarioActual;
    private boolean premium;
    private int id;
    

    // Constructores
    public Usuario(String nombre, String nombreUsuario, String contraseña, boolean isEditor, boolean isAdmin) {
        super(nombre);
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.suscripcionesActivas = new HashSet<>();
        this.noticiasCreadas = new ArrayList<>();
        this.premium = false; // Valor inicial de premium
        this.id = -1; // Valor inicial de id
    }

    // Getters y Setters

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSuscripcionesActivas(HashSet<Suscripcion> suscripcionesActivas) {
		this.suscripcionesActivas = suscripcionesActivas;
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
    

    public static int getIdUsuarioActual() {
        return idUsuarioActual;
    }

    public static void setIdUsuarioActual(int id) {
        idUsuarioActual = id;
    }
    public static Usuario obtenerUsuarioActual() {
        return usuarioActual;
    }

    public static void establecerUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
    public boolean getPremium() {
        return premium;
    }

    
    public boolean isPremium() {
        try {
            // Obtener conexión a la base de datos
            Connection connection = DatabaseConnector.getConnection();

            // Preparar la consulta SQL
            String query = "SELECT * FROM suscripcion s JOIN usuario u ON s.usuario_id = u.id WHERE u.id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, this.getId()); // Utiliza this.getId() en lugar de this.id

            // Ejecutar la consulta
            ResultSet resultSet = stmt.executeQuery();

            // Comprobar si el usuario tiene una suscripción activa
            if (resultSet.next()) {
                // Si la suscripción existe, comprobar si sigue vigente
                Timestamp fechaFin = resultSet.getTimestamp("fechaFin");
                return fechaFin.toInstant().isAfter(Instant.now());
            }

        } catch (ConexionFallidaException | SQLException e) {
            // Manejar las excepciones que puedan ocurrir al conectar a la base de datos o ejecutar la consulta
            e.printStackTrace();
        }

        // Si llegamos a este punto, es porque el usuario no tiene una suscripción activa o ha ocurrido un error, por lo que retornamos false
        return false;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public void addSuscripcion(Suscripcion suscripcion) {
        this.suscripcionesActivas.add(suscripcion);
    }

    public void removeSuscripcion(Suscripcion suscripcion) {
        this.suscripcionesActivas.remove(suscripcion);
    }
    private static Usuario usuarioActual; // Variable estática para almacenar el usuario actual

    // Método para establecer el usuario actual
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean isUsuarioActualPremium() {
        // Verificar si el usuario actual es premium
        int usuarioId = Usuario.getIdUsuarioActual();
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
        } catch (ConexionFallidaException e) {
            e.printStackTrace();
        }

        String query = "SELECT isPremium FROM usuario WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBoolean("isPremium");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Si no se encontró el usuario o no se pudo obtener el valor de isPremium, se asume que no es premium
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
                // Establecer el id del usuario actual
                Usuario.setIdUsuarioActual(resultSet.getInt("id"));
                Usuario.usuarioActual = this;
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
    public static void escribirLog(String log) {
        String nombreArchivo = "logs.log"; // Nombre de tu archivo de log

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            writer.write(log);
            writer.newLine(); // Para agregar una nueva línea después de cada log
        } catch (IOException e) {
            System.out.println("Error al escribir el log: " + e.getMessage());
        }
    }
    
    

 // Método para eliminar un usuario
    public static void eliminarUsuario(Usuario admin, String nombreUsuario) throws ConexionFallidaException {
        // Verificar si el administrador es realmente un administrador
        if (!admin.isAdmin()) {
            throw new ConexionFallidaException("Error al eliminar el usuario: el usuario que intenta realizar la acción no es un administrador");
        }

        // Consulta SQL para eliminar un usuario
        String sql = "DELETE FROM usuario WHERE nombreUsuario = ?";

        try (Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el parámetro del nombre de usuario en la consulta SQL
            stmt.setString(1, nombreUsuario);

            // Ejecutar la consulta SQL
            int filasEliminadas = stmt.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Usuario eliminado con éxito.");
                Usuario.escribirLog("El administrador " + admin.getNombreUsuario() + " eliminó el usuario " + nombreUsuario + " el " + LocalDateTime.now());
            } else {
                System.out.println("No se pudo eliminar el usuario. Compruebe si el nombre de usuario es correcto.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    public static List<String> verRegistros() throws IOException {
        // Nombre del archivo de registro
        String nombreArchivo = "logs.log";

        List<String> registros = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                registros.add(linea);
            }
        } catch (IOException e) {
            throw new IOException("Error al leer los registros: " + e.getMessage());
        }

        return registros;
    }
 // Método para modificar un usuario
 public static void modificarUsuario(Usuario admin, String nombre, String nombreUsuario, String contraseña, boolean isEditor, boolean isAdmin) throws ConexionFallidaException {
	 if (!admin.isAdmin()) {
         throw new ConexionFallidaException("Error al eliminar el usuario: el usuario que intenta realizar la acción no es un administrador");
     }
     // Consulta SQL para modificar un usuario
     String sql = "UPDATE usuario SET nombreUsuario = ?, contraseña = ?, isEditor = ?, isAdmin = ? WHERE id = ?";

     try (Connection conn = DatabaseConnector.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {

         // Establecer los parámetros en la consulta SQL
         stmt.setString(2, nombreUsuario);
         stmt.setString(3, contraseña);
         stmt.setBoolean(4, isEditor);
         stmt.setBoolean(5, isAdmin);

         // Ejecutar la consulta SQL
         int filasModificadas = stmt.executeUpdate();

         if (filasModificadas > 0) {
             System.out.println("Usuario modificado con éxito.");
             Usuario.escribirLog("El administrador " + admin.getNombreUsuario() + " Modificó el usuario " + nombreUsuario + " el " + LocalDateTime.now());
         } else {
             System.out.println("No se pudo modificar el usuario. Compruebe si el ID es correcto.");
         }

     } catch (SQLException e) {
         throw new ConexionFallidaException("Error al modificar el usuario: " + e.getMessage());
     }
 }
 public static List<String> obtenerTodosLosUsuarios() throws ConexionFallidaException {
	    List<String> usuarios = new ArrayList<>();

	    // Consulta SQL para obtener todos los nombres de usuario
	    String sql = "SELECT nombreUsuario FROM usuario";

	    try (Connection conn = DatabaseConnector.getConnection();
	        PreparedStatement stmt = conn.prepareStatement(sql)) {

	        // Ejecutar la consulta SQL y obtener los resultados
	        ResultSet rs = stmt.executeQuery();

	        // Procesar el ResultSet y añadir cada nombre de usuario a la lista
	        while (rs.next()) {
	            String nombreUsuario = rs.getString("nombreUsuario");
	            usuarios.add(nombreUsuario);
	        }

	    } catch (SQLException e) {
	        throw new ConexionFallidaException("Error al obtener todos los usuarios: " + e.getMessage());
	    }

	    return usuarios;
	}
 
 
    
}


