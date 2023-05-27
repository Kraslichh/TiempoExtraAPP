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

            statement.executeUpdate();
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




}
