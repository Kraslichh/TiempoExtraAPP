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

public class SuscripcionAnual extends Suscripcion {
    // Atributo
    private float descuento;

    // Constructor
    public SuscripcionAnual(String nombre, float precioPorMes, Categoria categoria, LocalDate fechaInicio, LocalDate fechaFin, float descuento) {
        super(nombre, precioPorMes, categoria, fechaInicio, fechaFin);
        this.descuento = descuento;
    }

    // Getters y Setters
    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    // Sobreescribir getPrecio()
    @Override
    public float getPrecioPorMes() {
        return super.getPrecioPorMes() * 12 - descuento;
    }

    // Método para buscar las suscripciones anuales en la base de datos
    public static List<SuscripcionAnual> buscarSuscripcionesAnuales() throws SQLException, ConexionFallidaException {
        List<SuscripcionAnual> suscripcionesAnuales = new ArrayList<>();

        Connection conexion = null;
        PreparedStatement consulta = null;
        ResultSet resultado = null;

        try {
            conexion = DatabaseConnector.getConnection();
            String consultaSQL = "SELECT * FROM suscripcion WHERE nombre = 'Suscripción Anual'";
            consulta = conexion.prepareStatement(consultaSQL);
            resultado = consulta.executeQuery();

            while (resultado.next()) {
                int id = resultado.getInt("id");
                float precioPorMes = resultado.getFloat("precioPorMes");
                Categoria categoria = Categoria.valueOf(resultado.getString("categoria"));
                LocalDate fechaInicio = resultado.getDate("fechaInicio").toLocalDate();
                LocalDate fechaFin = resultado.getDate("fechaFin").toLocalDate();
                float descuento = resultado.getFloat("descuento");

                SuscripcionAnual suscripcionAnual = new SuscripcionAnual("Suscripción Anual", precioPorMes, categoria, fechaInicio, fechaFin, descuento);
                suscripcionesAnuales.add(suscripcionAnual);
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

        return suscripcionesAnuales;
    }
}