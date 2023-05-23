package excepciones;
//Creamos la excepcion llamada ConexionFallidaException y esta lo unico que contendra sera el mensaje que
//nosotros hayamos colocado como es en este caso en la clase DatabaseConnector ahi hemos creado un mensaje
//que usara esta funcion.
public class ConexionFallidaException extends Exception {
    public ConexionFallidaException(String mensaje) {
        super(mensaje);
    }
}
