package clases;

import java.util.HashSet;

public class Usuario extends ElementoConNombre {
    
    private String nombreUsuario;
    private String contraseña;
    private boolean isEditor;
    private boolean isAdmin;
    private HashSet<Suscripcion> suscripcionesActivas;

    public Usuario(String nombre, String nombreUsuario, String contraseña, boolean isEditor, boolean isAdmin) {
        super(nombre);
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.isEditor = isEditor;
        this.isAdmin = isAdmin;
        this.suscripcionesActivas = new HashSet<>();
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

    // Los métodos iniciar_sesion(), cerrar_sesion() y registrar_usuario() deben implementarse según la lógica de tu aplicación.

}
