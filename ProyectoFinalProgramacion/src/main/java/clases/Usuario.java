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
import excepciones.ConexionFallidaException;

public class Usuario extends ElementoConNombre {
    //Atributos
    private String nombreUsuario;
    private String contraseña;
    private boolean isEditor;
    private boolean isAdmin;
    private HashSet<Suscripcion> suscripcionesActivas;
    private List<Noticia> noticiasCreadas;
//Constructores junto a la creacion de la escritura del fichero DatosColecciones.log.
    public Usuario(String nombre, String nombreUsuario, String contraseña, boolean isEditor, boolean isAdmin) {
        super(nombre);
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.suscripcionesActivas = new HashSet<Suscripcion>() {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean add(Suscripcion suscripcion) {
                boolean added = super.add(suscripcion);
                if (added) {
                    try (PrintWriter out = new PrintWriter(new FileWriter("DatosColecciones.log", true))) {
                        out.println("Nueva suscripcion agregada: " + suscripcion.toString());
                    } catch (IOException e) {
                        System.out.println("Error escribiendo en el archivo");
                        e.printStackTrace();
                    }
                }
                return added;
            }
            
        };

//Aqui creamos que el programa pueda escribir en el fichero cada vez que un usuario cree una noticia
        this.noticiasCreadas = new ArrayList<Noticia>() {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean add(Noticia noticia) {
                boolean added = super.add(noticia);
                if (added) {
                    try (PrintWriter out = new PrintWriter(new FileWriter("DatosColecciones.log", true))) {
                        out.println("Nueva noticia agregada: " + noticia.toString());
                    } catch (IOException e) {
                        System.out.println("Error escribiendo en el archivo");
                        e.printStackTrace();
                    }
                }
                return added;
            }
        };
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
    
    public List<Noticia> getNoticiasCreadas() {
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

    public static int getIdPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT id FROM usuario WHERE nombreUsuario = ?";
        int id = -1;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el parámetro del nombre de usuario en la consulta SQL
            stmt.setString(1, nombreUsuario);

            // Ejecutar la consulta SQL y obtener los resultados
            ResultSet rs = stmt.executeQuery();

            // Si hay un resultado, obtener el id del usuario
            if (rs.next()) {
                id = rs.getInt("id");
            }

        } catch (SQLException | ConexionFallidaException e) {
            System.out.println("Error al obtener el ID del usuario: " + e.getMessage());
        }

        // Si no se encontró ningún usuario, retornar -1
        return id;
    }
}

