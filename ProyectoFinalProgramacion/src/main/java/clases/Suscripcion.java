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
            String query = "INSERT INTO suscripcion (nombre, precioPorMes, fechaInicio, fechaFin, usuario_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, suscripcion.getNombre());
            statement.setFloat(2, suscripcion.getPrecioPorMes());
            statement.setDate(3, java.sql.Date.valueOf(suscripcion.getFechaInicio()));
            statement.setDate(4, java.sql.Date.valueOf(suscripcion.getFechaFin()));
            statement.setInt(5, usuarioId);

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
                    categoria = rs.getString("categoria");
                    break;
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



}
