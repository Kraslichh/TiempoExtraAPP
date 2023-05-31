package clases;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import conector.DatabaseConnector;

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
    
    //Metodos

    public static void insertarSuscripcion(Suscripcion suscripcion, int usuarioId) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "INSERT INTO suscripcion (nombre, precioPorMes, categoria, fechaInicio, fechaFin, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, suscripcion.getNombre());
            statement.setFloat(2, suscripcion.getPrecioPorMes());
            statement.setString(3, suscripcion.getCategoria().toString());
            statement.setDate(4, java.sql.Date.valueOf(suscripcion.getFechaInicio()));
            statement.setDate(5, java.sql.Date.valueOf(suscripcion.getFechaFin()));
            statement.setInt(6, usuarioId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 1) {
                // Actualizar el estado premium del usuario
                boolean esPremium = verificarSuscripcionActiva(usuarioId);
                actualizarEstadoPremium(usuarioId, esPremium);
            }
        } catch (SQLException | ConexionFallidaException ex) {
            ex.printStackTrace();
        }
    }
    public static boolean verificarSuscripcionActiva(int usuarioId) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Consulta SQL para verificar si el usuario tiene una suscripción activa
            String sql = "SELECT COUNT(*) FROM suscripcion WHERE usuario_id = ? AND fechaFin >= CURDATE()";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, usuarioId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException | ConexionFallidaException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public static void actualizarEstadoPremium(int usuarioId, boolean esPremium) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "UPDATE usuario SET isPremium = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setBoolean(1, esPremium);
            statement.setInt(2, usuarioId);

            statement.executeUpdate();
        } catch (SQLException | ConexionFallidaException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public static String obtenerCategoriaSuscripcion(int userId) {
        String categoria = null;
        String sql = "SELECT categoria, fechaInicio, fechaFin FROM suscripcion WHERE usuario_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Date fechaInicio = rs.getDate("fechaInicio");
                    Date fechaFin = rs.getDate("fechaFin");
                    LocalDate fechaInicioLocal = fechaInicio.toLocalDate();
                    LocalDate fechaFinLocal = fechaFin.toLocalDate();
                    LocalDate ahora = LocalDate.now();

                    if (ahora.isAfter(fechaInicioLocal) && ahora.isBefore(fechaFinLocal)) {
                        String categoriaDB = rs.getString("categoria");
                        if (Arrays.stream(Categoria.values()).anyMatch(c -> c.name().equals(categoriaDB))) {
                            categoria = categoriaDB;
                            break;
                        }
                    }
                }
            }

        } catch (SQLException | ConexionFallidaException e) {
            e.printStackTrace();
        }

        return categoria;
    }

public static LocalDate obtenerFechaInicioSuscripcion(int usuarioId) {
    try (Connection connection = DatabaseConnector.getConnection()) {
        String sql = "SELECT fechaInicio FROM suscripcion WHERE usuario_id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, usuarioId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            Date fechaInicio = resultSet.getDate("fechaInicio");
            return fechaInicio.toLocalDate();
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error al obtener la fecha de inicio de la suscripción: " + ex.getMessage());
    }

    return null;
}

public static LocalDate obtenerFechaFinSuscripcion(int usuarioId) {
    try (Connection connection = DatabaseConnector.getConnection()) {
        String sql = "SELECT fechaFin FROM suscripcion WHERE usuario_id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, usuarioId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            Date fechaFin = resultSet.getDate("fechaFin");
            return fechaFin.toLocalDate();
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error al obtener la fecha de fin de la suscripción: " + ex.getMessage());
    }

    return null;
}

public static void editarSuscripcion(int usuarioId, Suscripcion nuevaSuscripcion) {
    try (Connection connection = DatabaseConnector.getConnection()) {
        String sql = "UPDATE suscripcion SET nombre = ?, precioPorMes = ?, categoria = ?, fechaInicio = ?, fechaFin = ? WHERE usuario_id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, nuevaSuscripcion.getNombre());
        statement.setFloat(2, nuevaSuscripcion.getPrecioPorMes());
        statement.setString(3, nuevaSuscripcion.getCategoria().toString());
        statement.setDate(4, java.sql.Date.valueOf(nuevaSuscripcion.getFechaInicio()));
        statement.setDate(5, java.sql.Date.valueOf(nuevaSuscripcion.getFechaFin()));
        statement.setInt(6, usuarioId);

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "La suscripción se actualizó con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al actualizar la suscripción");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
    } catch (ConexionFallidaException ex) {
        JOptionPane.showMessageDialog(null, "Error de conexión: " + ex.getMessage());
    }
}

public static void eliminarSuscripcion(int usuarioId) {
    try (Connection connection = DatabaseConnector.getConnection()) {
        String sql = "DELETE FROM suscripcion WHERE usuario_id = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, usuarioId);

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "La suscripción se eliminó con éxito");
        } else {
            JOptionPane.showMessageDialog(null, "Error al eliminar la suscripción");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
    } catch (ConexionFallidaException ex) {
        JOptionPane.showMessageDialog(null, "Error de conexión: " + ex.getMessage());
    }
}
public static List<Categoria> obtenerCategoriasDisponibles() {
    List<Categoria> categorias = new ArrayList<>();

    try (Connection connection = DatabaseConnector.getConnection()) {
        String query = "SHOW COLUMNS FROM suscripcion LIKE 'categoria'";
        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String enumType = resultSet.getString("Type");

            String[] enumValues = enumType.substring(5, enumType.length() - 1).split(",");

            for (String value : enumValues) {
                String categoriaSinComillas = value.substring(1, value.length() - 1); // Quita las comillas
                categorias.add(Categoria.valueOf(categoriaSinComillas));
            }
        }
    } catch (SQLException | ConexionFallidaException ex) {
        ex.printStackTrace();
    }

    return categorias;
}
public static void editarCategoriaSuscripcion(int usuarioId, Categoria nuevaCategoria) {
    try (Connection connection = DatabaseConnector.getConnection()) {
        // Query para actualizar la categoría de la suscripción del usuario
        String query = "UPDATE suscripcion SET categoria = ? WHERE usuario_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        // Establecer los parámetros para el statement
        statement.setString(1, nuevaCategoria.name());
        statement.setInt(2, usuarioId);

        // Ejecutar la actualización
        statement.executeUpdate();
    } catch (SQLException | ConexionFallidaException ex) {
        ex.printStackTrace();
    }
}
}
