package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import conector.DatabaseConnector;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

public class SuscripcionMensual extends Suscripcion {

    // Constructor
    public SuscripcionMensual(String nombre, float precioPorMes, Categoria categoria, LocalDate fechaInicio, LocalDate fechaFin) {
        super(nombre, precioPorMes, categoria, fechaInicio, fechaFin);
    }

    // Método para buscar las suscripciones mensuales activas en la base de datos
    public static List<SuscripcionMensual> buscarSuscripcionesMensualesActivas() throws SQLException, ConexionFallidaException {
        List<SuscripcionMensual> suscripcionesMensuales = new ArrayList<>();

        Connection conexion = null;
        PreparedStatement consulta = null;
        ResultSet resultado = null;

        try {
            conexion = DatabaseConnector.getConnection();
            String consultaSQL = "SELECT * FROM suscripcion WHERE nombre = 'Suscripción Mensual' AND fechaFin >= ?";
            consulta = conexion.prepareStatement(consultaSQL);
            consulta.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            resultado = consulta.executeQuery();

            while (resultado.next()) {
                int id = resultado.getInt("id");
                float precioPorMes = resultado.getFloat("precioPorMes");
                Categoria categoria = Categoria.valueOf(resultado.getString("categoria"));
                LocalDate fechaInicio = resultado.getDate("fechaInicio").toLocalDate();
                LocalDate fechaFin = resultado.getDate("fechaFin").toLocalDate();

                SuscripcionMensual suscripcionMensual = new SuscripcionMensual("Suscripción Mensual", precioPorMes, categoria, fechaInicio, fechaFin);
                suscripcionesMensuales.add(suscripcionMensual);
            }
        } finally {
            if (resultado != null) {
                resultado.close();
            }
            if (consulta != null) {
                consulta.close();
            }
            if (conexion != null) {
                conexion.close();
            }
        }

        return suscripcionesMensuales;
    }
}
