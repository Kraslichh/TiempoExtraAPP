package clases;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

import conector.DatabaseConnector;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

public class Noticia extends ElementoConNombre {
    // Atributos
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private Usuario autor;
    private Categoria categoria;
    private boolean noticiaPremium;

    // Constructor
    public Noticia(String nombre, String contenido, LocalDateTime fechaPublicacion, Usuario autor, Categoria categoria, boolean noticiaPremium) {
        super(nombre);
        this.contenido = contenido;
        this.fechaPublicacion = fechaPublicacion;
        this.autor = autor;
        this.categoria = categoria;
        this.noticiaPremium = noticiaPremium;
    }

    // Getters and Setters
    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean isNoticiaPremium() {
        return noticiaPremium;
    }

    public void setNoticiaPremium(boolean noticiaPremium) {
        this.noticiaPremium = noticiaPremium;
    }
    

    // Los métodos crear_noticia(), mostrar_noticias(), actualizar_noticia() y eliminar_noticia() deben implementarse según la lógica de tu aplicación.

    public static void crear_noticia(String nombre, String contenido, LocalDateTime fechaPublicacion, Usuario autor, Categoria categoria, boolean noticiaPremium) throws ConexionFallidaException {
        // Consulta SQL para insertar una nueva noticia
        String sql = "INSERT INTO noticia (elementoConNombre_nombre, contenido, fechaPublicacion, autor_id, categoria, noticiaPremium) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los valores de los parámetros de la consulta SQL
            stmt.setString(1, nombre);
            stmt.setString(2, contenido);
            stmt.setTimestamp(3, Timestamp.valueOf(fechaPublicacion)); // Transformar LocalDateTime a Timestamp
            stmt.setInt(4, Usuario.getIdPorNombreUsuario(autor.getNombreUsuario())); // Aquí se pasa el id del autor
            stmt.setString(5, categoria.name());
            stmt.setBoolean(6, noticiaPremium);

            // Ejecutar la consulta SQL
            int rowsAffected = stmt.executeUpdate();

            // Verificar si la inserción fue exitosa
            if (rowsAffected == 1) {
                System.out.println("Noticia creada exitosamente.");
                Usuario.escribirLog("El usuario " + autor.getNombreUsuario() +" ha creado una noticia el " + LocalDateTime.now()+" Con el Titulo:"+nombre);
            } else {
                System.out.println("No se pudo crear la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al crear la noticia: " + e.getMessage());
        }
    }

    public static List<Noticia> getTodasLasNoticias() throws ConexionFallidaException {
        // Lista para almacenar las noticias recuperadas
        List<Noticia> noticias = new ArrayList<>();

        // Consulta SQL para seleccionar todas las noticias
        String sql = "SELECT * FROM noticia";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Procesar el ResultSet
            while (rs.next()) {
                Usuario autor = Usuario.getUsuarioPorId(rs.getInt("autor_id"));
                Noticia noticia = new Noticia(
                    rs.getString("elementoConNombre_nombre"), // nombre
                    rs.getString("contenido"), // contenido
                    rs.getTimestamp("fechaPublicacion").toLocalDateTime(), // fechaPublicacion
                    autor, // autor
                    Categoria.valueOf(rs.getString("categoria")), // categoria
                    rs.getBoolean("noticiaPremium") // noticiaPremium
                );

                // Añadir la noticia a la lista
                noticias.add(noticia);
                System.out.println("Noticia añadida: " + noticia.getNombre()); // Imprimir el nombre de la noticia añadida
            }

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            throw new ConexionFallidaException("Error al obtener todas las noticias: " + e.getMessage());
        }

        System.out.println("Se encontraron " + noticias.size() + " noticias"); // Imprimir el número de noticias encontradas
        return noticias;
    }
    
    public void actualizar_noticia_completa(String nuevoNombre, String nuevoContenido, Categoria nuevaCategoria, boolean nuevaNoticiaPremium) throws ConexionFallidaException {
        // Consulta SQL para actualizar la noticia
        String sql = "UPDATE noticia SET elementoConNombre_nombre = ?, contenido = ?, categoria = ?, noticiaPremium = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los valores de los parámetros de la consulta SQL
            stmt.setString(1, nuevoNombre);
            stmt.setString(2, nuevoContenido);
            stmt.setString(3, nuevaCategoria.name());
            stmt.setBoolean(4, nuevaNoticiaPremium);
            stmt.setInt(5, this.getIdFromDatabase());

            // Ejecutar la consulta SQL
            int rowsAffected = stmt.executeUpdate();

            // Verificar si la actualización fue exitosa
            if (rowsAffected == 1) {
                System.out.println("Noticia actualizada exitosamente.");
            } else {
                System.out.println("No se pudo actualizar la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al actualizar la noticia: " + e.getMessage());
        }
    }
    
    public static List<Noticia> mostrar_noticias() throws ConexionFallidaException {
        List<Noticia> noticias = new ArrayList<>();

        // Consulta SQL para obtener todas las noticias
        String sql = "SELECT * FROM noticia";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Iterar a través de los resultados de la consulta
            while (rs.next()) {
                String nombre = rs.getString("elementoConNombre_nombre");
                String contenido = rs.getString("contenido");
                LocalDateTime fechaPublicacion = rs.getTimestamp("fechaPublicacion").toLocalDateTime();
                Usuario autor = Usuario.getUsuarioPorId(rs.getInt("autor_id"));
                Categoria categoria = Categoria.valueOf(rs.getString("categoria"));
                boolean noticiaPremium = rs.getBoolean("noticiaPremium");

                // Crear una nueva noticia con los datos obtenidos y añadirla a la lista
                Noticia noticia = new Noticia(nombre, contenido, fechaPublicacion, autor, categoria, noticiaPremium);
                noticias.add(noticia);
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al mostrar noticias: " + e.getMessage());
        }

        return noticias;
    }

    public static void visualizarNoticia(List<Noticia> noticias) {
        for (Noticia noticia : noticias) {
            System.out.println("Título: " + noticia.getNombre());
            System.out.println("Contenido: " + noticia.getContenido());
            System.out.println("Fecha de publicación: " + noticia.getFechaPublicacion());
            System.out.println("Autor: " + noticia.getAutor().getNombre());
            System.out.println("Categoría: " + noticia.getCategoria());
            System.out.println("Noticia Premium: " + noticia.isNoticiaPremium());
            System.out.println("------------------------");
        }
    }

    public int getIdFromDatabase() throws ConexionFallidaException {
        // Consulta SQL para obtener el ID de la noticia desde la base de datos
        String sql = "SELECT id FROM noticia WHERE elementoConNombre_nombre = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.getNombre());

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Verificar si se encontró el ID de la noticia
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new ConexionFallidaException("No se pudo obtener el ID de la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener el ID de la noticia: " + e.getMessage());
        }
    }
    public static List<Noticia> actualizarNoticias() throws ConexionFallidaException {
        List<Noticia> noticiasActualizadas = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM noticia");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("elementoConNombre_nombre");
                String contenido = rs.getString("contenido");
                LocalDateTime fechaPublicacion = rs.getTimestamp("fechaPublicacion").toLocalDateTime();
                Usuario autor = Usuario.getUsuarioPorId(rs.getInt("autor_id"));
                Categoria categoria = Categoria.valueOf(rs.getString("categoria"));
                boolean noticiaPremium = rs.getBoolean("noticiaPremium");

                Noticia noticia = new Noticia(nombre, contenido, fechaPublicacion, autor, categoria, noticiaPremium);
                noticiasActualizadas.add(noticia);
            }

            System.out.println("Se han actualizado las noticias");
        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al actualizar las noticias: " + e.getMessage());
        }

        return noticiasActualizadas;
    }
    public static List<Noticia> getNoticiasCreadas(Usuario usuario) throws ConexionFallidaException {
        List<Noticia> noticias = new ArrayList<>();

        // Consulta SQL para obtener todas las noticias creadas por el usuario
        String sql = "SELECT * FROM noticia WHERE autor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        	stmt.setInt(1, Usuario.getIdPorNombreUsuario(usuario.getNombre()));

            // Ejecutar la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Procesar el ResultSet
            while (rs.next()) {
                Noticia noticia = new Noticia(
                    rs.getString("elementoConNombre_nombre"),
                    rs.getString("contenido"),
                    rs.getTimestamp("fechaPublicacion").toLocalDateTime(),
                    usuario,  // Autor
                    Categoria.valueOf(rs.getString("categoria")),
                    rs.getBoolean("noticiaPremium")
                );
                noticias.add(noticia);
            }
        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al obtener las noticias creadas: " + e.getMessage());
        }

        return noticias;
    }

    
    public void eliminar_noticia() throws ConexionFallidaException {
        // Consulta SQL para eliminar la noticia
        String sql = "DELETE FROM noticia WHERE elementoConNombre_nombre = ?";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer el valor del parámetro de la consulta SQL
            stmt.setString(1, getNombre());

            // Ejecutar la consulta SQL
            int rowsAffected = stmt.executeUpdate();

            // Verificar si la eliminación fue exitosa
            if (rowsAffected == 1) {
                System.out.println("Noticia eliminada exitosamente.");
            } else {
                System.out.println("No se pudo eliminar la noticia.");
            }

        } catch (SQLException e) {
            throw new ConexionFallidaException("Error al eliminar la noticia: " + e.getMessage());
        }
    }

    public static void actualizarNoticias(JFrame userWindow) {
        try {
            // Obtener el panel de contenido
            Container contentPane = userWindow.getContentPane();

            // Obtener el JScrollPane dentro del contentPane
            JScrollPane scrollPane = null;
            for (Component component : contentPane.getComponents()) {
                if (component instanceof JScrollPane) {
                    scrollPane = (JScrollPane) component;
                    break;
                }
            }

            if (scrollPane != null) {
                // Obtener el viewport del JScrollPane
                JViewport viewport = scrollPane.getViewport();

                // Obtener el panel de noticias del viewport
                JPanel newsPanel = (JPanel) viewport.getView();

                // Limpiar el panel de noticias
                newsPanel.removeAll();

                // Crear paneles separados para las noticias premium y las demás noticias
                JPanel premiumNewsPanel = new JPanel();
                premiumNewsPanel.setLayout(new BoxLayout(premiumNewsPanel, BoxLayout.Y_AXIS));

                JPanel regularNewsPanel = new JPanel();
                regularNewsPanel.setLayout(new BoxLayout(regularNewsPanel, BoxLayout.Y_AXIS));

                // Mostrar las noticias actualizadas en los paneles correspondientes
                List<Noticia> noticiasActualizadas = Noticia.mostrar_noticias();
                System.out.println("Total de noticias: " + noticiasActualizadas.size());
                for (Noticia noticia : noticiasActualizadas) {
                    System.out.println("Noticia: " + noticia.getNombre());
                    System.out.println("Es premium: " + noticia.isNoticiaPremium());
                    // Configurar el panel de la noticia y sus componentes
                    JPanel noticiaPanel = new JPanel();
                    noticiaPanel.setLayout(new BorderLayout());
                    noticiaPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    noticiaPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    // Obtener la imagen desde una URL
                    try {
                        URL imageUrl = new URL("https://cdn.discordapp.com/attachments/1042159188127797288/1108685609297530950/48cbb86e-752c-471a-8805-f56856a0ea2d.png");
                        Image image = ImageIO.read(imageUrl);

                        // Escala la imagen
                        Image newImg = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);

                        // Crea un ImageIcon con la imagen escalada
                        ImageIcon imageIcon = new ImageIcon(newImg);

                        // Crea una etiqueta para mostrar la imagen
                        JLabel imageLabel = new JLabel(imageIcon);
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

                        // Agrega la etiqueta de la imagen al panel principal
                        noticiaPanel.add(imageLabel, BorderLayout.WEST);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Panel para el contenido, la fecha y el autor de la noticia
                    JPanel infoPanel = new JPanel();
                    infoPanel.setLayout(new GridLayout(0, 2));

                    // Título de la noticia
                    JLabel tituloLabel = new JLabel(noticia.getNombre());
                    tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    tituloLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

                    // Contenido de la noticia
                    JTextArea contenidoTextArea = new JTextArea(noticia.getContenido());
                    contenidoTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
                    contenidoTextArea.setEditable(false);
                    contenidoTextArea.setLineWrap(true);
                    contenidoTextArea.setWrapStyleWord(true);

                    // Fecha de publicación
                    JLabel fechaLabel = new JLabel("Fecha: " + noticia.getFechaPublicacion());
                    fechaLabel.setFont(new Font("Arial", Font.PLAIN, 12));

                    // Autor
                    JLabel autorLabel = new JLabel("Autor: " + noticia.getAutor().getNombreUsuario());
                    autorLabel.setFont(new Font("Arial", Font.PLAIN, 12));

                    // Agregar componentes al panel de información
                    infoPanel.add(new JLabel("Título:"));
                    infoPanel.add(tituloLabel);
                    infoPanel.add(new JLabel("Contenido:"));
                    infoPanel.add(contenidoTextArea);
                    infoPanel.add(new JLabel("Fecha:"));
                    infoPanel.add(fechaLabel);
                    infoPanel.add(new JLabel("Autor:"));
                    infoPanel.add(autorLabel);

                    // Agregar el panel de información al panel de la noticia correspondiente
                    noticiaPanel.add(infoPanel, BorderLayout.CENTER);

                    Usuario usuarioActual = Usuario.obtenerUsuarioActual();

                    // Verificar si el usuario actual es premium
                    boolean esPremium = esUsuarioPremium(usuarioActual.getNombreUsuario());
                    if (noticia.isNoticiaPremium()) {
                        if (esPremium) {
                            // Si la noticia es premium y el usuario es premium, agregamos la noticia al panel premium
                            premiumNewsPanel.add(noticiaPanel);
                        }
                    } else {
                        // Si la noticia no es premium, independientemente del estado de premium del usuario, agregamos la noticia al panel regular
                        regularNewsPanel.add(noticiaPanel);
                    }
                }

                // Crear un panel contenedor para los dos paneles de noticias
                JPanel newsContainerPanel = new JPanel();
                newsContainerPanel.setLayout(new GridLayout(1, 2));
                newsContainerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

                // Agregar el panel de noticias premium al panel contenedor
                JPanel premiumPanelWrapper = new JPanel(new BorderLayout());
                premiumPanelWrapper.add(new JLabel("Noticias Premium"), BorderLayout.NORTH);
                premiumPanelWrapper.add(new JScrollPane(premiumNewsPanel), BorderLayout.CENTER);
                newsContainerPanel.add(premiumPanelWrapper);

                // Agregar el panel de noticias regulares al panel contenedor
                JPanel regularPanelWrapper = new JPanel(new BorderLayout());
                regularPanelWrapper.add(new JLabel("Otras Noticias"), BorderLayout.NORTH);
                regularPanelWrapper.add(new JScrollPane(regularNewsPanel), BorderLayout.CENTER);
                newsContainerPanel.add(regularPanelWrapper);

                // Agregar el panel contenedor al panel de noticias principal
                newsPanel.add(newsContainerPanel);

                // Actualizar el panel de noticias principal
                newsPanel.revalidate();
                newsPanel.repaint();
            }
        } catch (ConexionFallidaException ex) {
            JOptionPane.showMessageDialog(userWindow, "Error al actualizar las noticias: " + ex.getMessage());
        }
    }

    private static boolean esUsuarioPremium(String nombreUsuario) throws ConexionFallidaException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT isPremium FROM usuario WHERE nombreUsuario = ?")) {

            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int isPremiumValue = rs.getInt("isPremium");
                return isPremiumValue == 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}