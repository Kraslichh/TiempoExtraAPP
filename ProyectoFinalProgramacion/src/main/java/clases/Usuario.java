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
    
    private String nombreUsuario;
    private String contraseña;
    private boolean isEditor;
    private boolean isAdmin;
    private HashSet<Suscripcion> suscripcionesActivas;
    private List<Noticia> noticiasCreadas;

    public Usuario(String nombre, String nombreUsuario, String contraseña, boolean isEditor, boolean isAdmin) {
        super(nombre);
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.suscripcionesActivas = new HashSet<Suscripcion>() {
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

        this.noticiasCreadas = new ArrayList<Noticia>() {
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
    

    // Getters and Setters

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        String query = "SELECT * FROM usuario WHERE nombreUsuario = ? AND contraseña = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Inicio de sesión exitoso!");
                // Aquí puedes establecer las propiedades del usuario en la instancia actual
                // desde los resultados de la consulta a la base de datos.
            } else {
                System.out.println("Nombre de usuario o contraseña incorrectos!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
