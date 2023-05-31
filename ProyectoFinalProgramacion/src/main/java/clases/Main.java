package clases;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.swing.*;

import com.toedter.calendar.JCalendar;

import conector.DatabaseConnector;
import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;
import excepciones.RegistroException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import clases.Suscripcion;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;



public class Main {
    public static void main(String[] args) throws RegistroException {
    	JFrame frame = new JFrame("TiempoExtra inicio de sesión");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLayout(new GridLayout(1, 1)); // Configura el administrador de diseño GridLayout con 1 fila y 1 columna


        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        // Añade un JLabel para la imagen de fondo
        try {
            URL imageUrl = new URL("https://cdn.discordapp.com/attachments/965004632646615040/1096887621092913173/image.png");
            Image image = ImageIO.read(imageUrl);

            // Escala la imagen
            Image newImg = image.getScaledInstance(600, 600, Image.SCALE_SMOOTH);

            // Crea un ImageIcon con la imagen escalada
            ImageIcon imageIcon = new ImageIcon(newImg);

            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBounds(100, 50, 600, 600);
            panel.add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setBounds(100, 700, 200, 50);
        panel.add(loginButton);

        Color[] colors = {Color.RED,  Color.ORANGE, Color.GREEN};
        final int[] currentColorIndex = {0};

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Cambiamos el color del botón cuando el mouse pasa por encima
                loginButton.setBackground(colors[currentColorIndex[0]]);
                currentColorIndex[0] = (currentColorIndex[0] + 1) % colors.length;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Restauramos el color original del botón cuando el mouse deja de pasar por encima
                loginButton.setBackground(UIManager.getColor("Button.background"));
            }
        });


        loginButton.addActionListener(e -> {
            // Creamos e iniciamos un nuevo JFrame para el inicio de sesión
            JFrame loginFrame = new JFrame("Inicio de Sesión");

         // Campos de texto para el nombre de usuario y la contraseña
            JLabel usernameLabel = new JLabel("Nombre de usuario");
            usernameLabel.setBounds(50, 20, 200, 30);
            loginFrame.add(usernameLabel);

            JTextField usernameField = new JTextField();
            usernameField.setBounds(50, 50, 200, 30);
            loginFrame.add(usernameField);

            JLabel passwordLabel = new JLabel("Contraseña");
            passwordLabel.setBounds(50, 70, 200, 30);
            loginFrame.add(passwordLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(50, 100, 200, 30);
            loginFrame.add(passwordField);

            JButton loginConfirmButton = new JButton("Iniciar Sesión");
            loginConfirmButton.setBounds(50, 150, 200, 30);

            loginConfirmButton.addActionListener(e1 -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Verificar que los campos no estén vacíos
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, rellene todos los campos");
                    return;
                }
                try {
                    String soundFilePath = "iniciarsesion.wav";
                    File soundFile = new File(soundFilePath);

                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // Crear una nueva instancia de Usuario
                Usuario usuario = new Usuario(username, username, password, false, false);
                

                // Intentar iniciar sesión
                try {
                    usuario.iniciar_sesion(username, password);
                    int usuarioId = Usuario.getIdPorNombreUsuario(username);
                    usuario.setId(usuarioId);

                    // Si la línea anterior no lanza una excepción, la autenticación fue exitosa
                    loginFrame.dispose(); // Cerrar la ventana de inicio de sesión
                    Usuario.setUsuarioActual(usuario); 

                    // Crear una nueva ventana después de iniciar sesión
                    JFrame userWindow = new JFrame("Bienvenido, " + username);
                    userWindow.setSize(1200, 800);
                    userWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



                 // Cargar la imagen de fondo desde una URL
                 URL imageUrl = new URL("https://fondosmil.com/fondo/5782.jpg");  
                 Image image = ImageIO.read(imageUrl);
                 Image scaledImage = image.getScaledInstance(userWindow.getWidth(), userWindow.getHeight(), Image.SCALE_SMOOTH);

                 // Crear un JLabel para la imagen de fondo y añadir la imagen
                 JLabel backgroundLabel = new JLabel(new ImageIcon(scaledImage));
                 backgroundLabel.setBounds(0, 0, userWindow.getWidth(), userWindow.getHeight());
                 
              // Reproducir música con volumen ajustado
                 String musicFilePath = "himnochampions.wav";
                 File musicFile = new File(musicFilePath);
                 Clip clip = AudioSystem.getClip();
                 clip.open(AudioSystem.getAudioInputStream(musicFile));

                 // Ajustar el volumen al 50%
                 FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                 float volume = gainControl.getMinimum() + ((gainControl.getMaximum() - gainControl.getMinimum()) * 0.50f);
                 gainControl.setValue(volume);

                 // Agregar el LineListener para reiniciar la reproducción
                 clip.addLineListener(event -> {
                     if (event.getType() == LineEvent.Type.STOP) {
                         clip.setFramePosition(0); // Reiniciar la posición del clip al inicio
                         clip.start(); // Volver a reproducir el audio
                     }
                 });

                 clip.start(); // Iniciar la reproducción del audio

              // Añade el título y el subtítulo
                 JLabel titleLabel = new JLabel("TiempoExtra");
                 titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
                 titleLabel.setBounds(350, 10, 200, 30);
                 titleLabel.setForeground(Color.WHITE);

                 JLabel subtitleLabel = new JLabel("El Mejor periódico deportivo del mundo");
                 subtitleLabel.setFont(new Font("Serif", Font.PLAIN, 16));
                 subtitleLabel.setBounds(300, 40, 300, 30);
                 subtitleLabel.setForeground(Color.WHITE);

                    // Asegurar que los labels son transparentes para que la imagen se vea
                    titleLabel.setOpaque(false);
                    subtitleLabel.setOpaque(false);

                    backgroundLabel.add(titleLabel);
                    backgroundLabel.add(subtitleLabel);

                    
                 // Verificar si el usuario tiene una suscripción activa
                    boolean tieneSuscripcion = usuario.isPremium();

                    if (tieneSuscripcion) {
                        // Crear el botón PREMIUM
                        JButton premiumButton = new JButton("PREMIUM");
                        premiumButton.setBounds(50, 670, 180, 30);
                        premiumButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Obtener la información de la suscripción premium del usuario
                                int usuarioId;
                                try {
                                    usuarioId = Usuario.getIdPorNombreUsuario(username);
                                    String categoria = Suscripcion.obtenerCategoriaSuscripcion(usuarioId);
                                    LocalDate fechaInicio = Suscripcion.obtenerFechaInicioSuscripcion(usuarioId);
                                    LocalDate fechaFin = Suscripcion.obtenerFechaFinSuscripcion(usuarioId);

                                    // Mostrar la información de la suscripción premium en una ventana emergente
                                    JFrame suscripcionWindow = new JFrame("Información de Suscripción Premium");
                                    suscripcionWindow.setSize(400, 300);

                                    JPanel panel = new JPanel();
                                    suscripcionWindow.add(panel);

                                    //JLabel categoriaLabel = new JLabel("Categoría: " + categoria);
                                    JLabel fechaInicioLabel = new JLabel("Fecha de inicio: " + fechaInicio);
                                    JLabel fechaFinLabel = new JLabel("Fecha de fin: " + fechaFin);

                                   // panel.add(categoriaLabel);
                                    panel.add(fechaInicioLabel);
                                    panel.add(fechaFinLabel);
                                    
                                    // Crear botones
                                    JButton buttonEditar = new JButton("Editar Suscripción");
                                    JButton buttonEliminar = new JButton("Cancelar Suscripción");

                                    // Configurar las acciones de los botones
                                    buttonEditar.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            // Obtener todas las categorías disponibles de la base de datos
                                            List<Categoria> categoriasDisponibles = Suscripcion.obtenerCategoriasDisponibles();
                                            // Convertir la lista de categorías en un array
                                            Categoria[] categoriasArray = categoriasDisponibles.toArray(new Categoria[0]);

                                            // Permitir al usuario seleccionar una categoría de las disponibles
                                            Categoria nuevaCategoria = (Categoria) JOptionPane.showInputDialog(
                                                    null,
                                                    "Seleccione una nueva categoría:",
                                                    "Editar Suscripción",
                                                    JOptionPane.QUESTION_MESSAGE,
                                                    null,
                                                    categoriasArray,
                                                    categoriasArray[0]
                                            );

                                            if (nuevaCategoria != null) {
                                                // Aquí puedes realizar el cambio de la categoría de la suscripción
                                                Suscripcion.editarCategoriaSuscripcion(usuarioId, nuevaCategoria);

                                                // Mostrar un mensaje al usuario
                                                JOptionPane.showMessageDialog(null, "Suscripción editada correctamente.");
                                            }
                                        }
                                    });

                                    buttonEliminar.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            // Eliminar la suscripción
                                            Suscripcion.eliminarSuscripcion(usuarioId);

                                            // Crear un JDialog
                                            JDialog dialog = new JDialog();
                                            dialog.setSize(400, 200);  // Establecer el ancho y la altura de la ventana emergente.
                                            dialog.setLocationRelativeTo(null);  // Centra la ventana emergente en el medio de la pantalla.

                                            JLabel label = new JLabel("Suscripción Cancelada. Cierra la sesión para ver los cambios.", SwingConstants.CENTER);
                                            dialog.getContentPane().add(label);

                                            // Configurar el temporizador para cerrar el diálogo después de 3 segundos
                                            javax.swing.Timer timer = new javax.swing.Timer(3000, new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    dialog.dispose();
                                                    suscripcionWindow.dispose();  // Cierra la ventana principal.
                                                }
                                            });
                                            timer.setRepeats(false);
                                            timer.start();

                                            dialog.setVisible(true);  // Mostrar la ventana emergente.

                                            // Reproducir sonido
                                            try {
                                                String soundFilePath = "sad.wav";
                                                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFilePath));
                                                Clip clip = AudioSystem.getClip();
                                                clip.open(audioInputStream);
                                                clip.start();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                    // Añadir los botones al panel de botones


                                    JPanel buttonsPanel = new JPanel(); // panel para los botones
                                    buttonsPanel.setLayout(new FlowLayout());
                                    buttonsPanel.add(buttonEditar);
                                    buttonsPanel.add(buttonEliminar);

                                    // Añadir el panel de botones al panel principal
                                    panel.add(buttonsPanel);
                                    suscripcionWindow.setVisible(true);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Error al obtener la información de la suscripción premium: " + ex.getMessage());
                                }
                            }
                        });
                        userWindow.add(premiumButton);
                    } else {
                        // Crear el botón "Obten tu premium"
                        JButton sucripciones = new JButton("Obten tu premium");
                        sucripciones.setBounds(50, 670, 180, 30);
                        sucripciones.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Código para obtener la suscripción premium
                                try {
                                    String soundFilePath = "aplausos.wav";
                                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFilePath));
                                    Clip clip = AudioSystem.getClip();
                                    clip.open(audioInputStream);
                                    clip.start();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                // Creando una ventana emergente.
                                JFrame suscripcionWindow = new JFrame("Elige tu suscripción");
                                suscripcionWindow.setSize(400, 300);

                                // Agregando componentes a suscripcionWindow para permitir al usuario seleccionar una suscripción.
                                JComboBox<String> suscripcionBox = new JComboBox<>();
                                JComboBox<Categoria> categoriaBox = new JComboBox<>(Categoria.values());

                                // Añadiendo las opciones de suscripción a suscripcionBox.
                                suscripcionBox.addItem("Suscripción Mensual (10 euros/mes)");
                                suscripcionBox.addItem("Suscripción Anual (100 euros/año)");

                                JButton aceptarButton = new JButton("Aceptar");
                                aceptarButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        String tipoSuscripcion = (String) suscripcionBox.getSelectedItem();
                                        float precioPorMes = tipoSuscripcion.startsWith("Suscripción Mensual") ? 10.0f : 100.0f; // Obtener el precio según el tipo de suscripción
                                        Categoria categoria = (Categoria) categoriaBox.getSelectedItem();

                                        LocalDate fechaInicio = LocalDate.now();
                                        LocalDate fechaFin = tipoSuscripcion.startsWith("Suscripción Mensual") ? fechaInicio.plusMonths(1) : fechaInicio.plusYears(1);

                                        int usuarioId = 1; // Deberías obtener el ID del usuario actual
                                        try {
                                            usuarioId = Usuario.getIdPorNombreUsuario(username);
                                        } catch (Exception ex) {
                                            JOptionPane.showMessageDialog(null, "Error al obtener el ID del usuario: " + ex.getMessage());
                                        }

                                        Suscripcion suscripcion = new Suscripcion(tipoSuscripcion, precioPorMes, categoria, fechaInicio, fechaFin);

                                        Suscripcion.insertarSuscripcion(suscripcion, usuarioId);

                                        try {
                                            String soundFilePath = "cash-register-purchase-87313.wav";
                                            File soundFile = new File(soundFilePath);

                                            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                                            Clip clip = AudioSystem.getClip();
                                            clip.open(audioInputStream);
                                            clip.start();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                        JOptionPane.showMessageDialog(suscripcionWindow, "¡Gracias por obtener tu premium! En cuanto inicies sesion de nuevo veras los cambios aplicados.");
                                        suscripcionWindow.dispose();
                                    }
                                });

                                suscripcionWindow.setLayout(new FlowLayout());
                                suscripcionWindow.add(suscripcionBox);
                                suscripcionWindow.add(categoriaBox);
                                suscripcionWindow.add(aceptarButton);
                                suscripcionWindow.setVisible(true);
                            }
                        });
                        userWindow.add(sucripciones);
                    }
                    
                    
                    // Realizar una verificación si el usuario es premium
                    if (usuario.isPremium()) {
                        // Lógica para usuarios premium
                        System.out.println("El usuario es premium.");
                    } else {
                        // Lógica para usuarios no premium
                        System.out.println("El usuario no es premium.");
                    }

                    // Agregar un botón para actualizar la sesión
                    JButton refreshButton = new JButton("Actualizar");
                    refreshButton.setBounds(1000, 250, 150, 30);
                    userWindow.add(refreshButton);

                    // Agregar un temporizador para la actualización automática cada 10 segundos
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            Noticia.actualizarNoticias(userWindow);
                        }
                    }, 0, 10000);

                    refreshButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                        	Noticia.actualizarNoticias(userWindow);
                        }
                    });

                    // Agregar un botón para cerrar la sesión
                    JButton logoutButton = new JButton("Cerrar Sesión");
                    logoutButton.setBounds(1000, 200, 150, 30);
                    userWindow.add(logoutButton);

                    logoutButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Realizar acciones para cerrar la sesión
                            // Por ejemplo, volver a mostrar la ventana de inicio de sesión y cerrar la ventana actual

                            // Mostrar la ventana de inicio de sesión
                            frame.setVisible(true);

                            // Detener el temporizador
                            timer.cancel();

                            // Cerrar la ventana actual
                            userWindow.dispose();
                        }
                    });

                    // Agregar un botón para crear noticias solo para usuarios con isEditor activo
                    if (usuario.isEditor()) {
                    	// Agregar un botón para crear noticias solo para usuarios con isEditor activo
                    	if (usuario.isEditor()) {
                    	    JButton createNewsButton = new JButton("Crear Noticia");
                    	    createNewsButton.setBounds(1000, 300, 150, 30);
                    	    userWindow.add(createNewsButton);

                    	    createNewsButton.addActionListener(new ActionListener() {
                    	        @Override
                    	        public void actionPerformed(ActionEvent e) {
                    	            // Abrir una nueva ventana para introducir los detalles de la noticia
                    	            JFrame createNewsFrame = new JFrame("Crear Noticia");
                    	            createNewsFrame.setSize(500, 500);
                    	            createNewsFrame.setLayout(new GridBagLayout());

                    	            GridBagConstraints constraints = new GridBagConstraints();
                    	            constraints.fill = GridBagConstraints.HORIZONTAL;

                    	            // Etiquetas descriptivas
                    	            JLabel titleLabel = new JLabel("Título:");
                    	            JLabel contentLabel = new JLabel("Contenido:");
                    	            JLabel categoryLabel = new JLabel("Categoría:");
                    	            JLabel premiumLabel = new JLabel("Noticia Premium:");

                    	            // Campos de texto para los detalles de la noticia
                    	            JTextField titleField = new JTextField(20);
                    	            JTextArea contentArea = new JTextArea(5, 20);
                    	            contentArea.setLineWrap(true);
                    	            contentArea.setWrapStyleWord(true);
                    	            JScrollPane contentScrollPane = new JScrollPane(contentArea);
                    	            JComboBox<Categoria> categoryBox = new JComboBox<>(Categoria.values());
                    	            JCheckBox premiumCheckBox = new JCheckBox();

                    	            // Botón para confirmar la creación de la noticia
                    	            JButton confirmButton = new JButton("Crear Noticia");

                    	            // Configuración de los componentes y su colocación en la ventana
                    	            constraints.gridx = 0;
                    	            constraints.gridy = 0;
                    	            createNewsFrame.add(titleLabel, constraints);

                    	            constraints.gridx = 1;
                    	            createNewsFrame.add(titleField, constraints);

                    	            constraints.gridx = 0;
                    	            constraints.gridy = 1;
                    	            createNewsFrame.add(contentLabel, constraints);

                    	            constraints.gridx = 1;
                    	            constraints.gridy = 1;
                    	            constraints.gridwidth = 2;
                    	            constraints.fill = GridBagConstraints.BOTH;
                    	            createNewsFrame.add(contentScrollPane, constraints);

                    	            constraints.gridx = 0;
                    	            constraints.gridy = 2;
                    	            constraints.gridwidth = 1;
                    	            createNewsFrame.add(categoryLabel, constraints);

                    	            constraints.gridx = 1;
                    	            createNewsFrame.add(categoryBox, constraints);

                    	            constraints.gridx = 0;
                    	            constraints.gridy = 3;
                    	            createNewsFrame.add(premiumLabel, constraints);

                    	            constraints.gridx = 1;
                    	            createNewsFrame.add(premiumCheckBox, constraints);

                    	            constraints.gridx = 0;
                    	            constraints.gridy = 4;
                    	            constraints.gridwidth = 2;
                    	            createNewsFrame.add(confirmButton, constraints);

                    	            createNewsFrame.setVisible(true);

                    	            confirmButton.addActionListener(new ActionListener() {
                    	                @Override
                    	                public void actionPerformed(ActionEvent e) {
                    	                    // Llamar al método crear_noticia() con los valores introducidos
                    	                    try {
                    	                        Noticia.crear_noticia(
                    	                                titleField.getText(),
                    	                                contentArea.getText(),
                    	                                LocalDateTime.now(),
                    	                                usuario,
                    	                                (Categoria) categoryBox.getSelectedItem(),
                    	                                premiumCheckBox.isSelected()
                    	                        );
                    	                        JOptionPane.showMessageDialog(createNewsFrame, "Noticia creada exitosamente");
                    	                        createNewsFrame.dispose(); // Cerrar la ventana de creación de noticia
                    	                    } catch (Exception ex) {
                    	                        JOptionPane.showMessageDialog(createNewsFrame, "Error al crear noticia: " + ex.getMessage());
                    	                    }
                    	                }
                    	            });
                    	        }
                    	    });
                    	}

                    

                        // Agregar un botón para editar noticias solo para usuarios con isEditor activo
                        JButton editNewsButton = new JButton("Editar Noticia");
                        editNewsButton.setBounds(1000, 350, 150, 30);
                        userWindow.add(editNewsButton);

                        editNewsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Obtener la lista de noticias del usuario actual
                                List<Noticia> noticias = null;
                                try {
                                    noticias = usuario.getNoticiasCreadas();
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(userWindow, "Error al mostrar noticias: " + ex.getMessage());
                                    return;
                                }

                                // Si no hay noticias, informar al usuario y terminar la acción
                                if (noticias.isEmpty()) {
                                    JOptionPane.showMessageDialog(userWindow, "No hay noticias para editar");
                                    return;
                                }

                             // Crear un diálogo para seleccionar la noticia a editar
                                Object selectedNews = JOptionPane.showInputDialog(
                                    userWindow,
                                    "Selecciona la noticia a editar:",
                                    "Editar Noticia",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    noticias.toArray(),
                                    noticias.get(0));

                                // Verificar si se seleccionó una noticia
                                if (selectedNews == null) {
                                    JOptionPane.showMessageDialog(userWindow, "No se seleccionó ninguna noticia para editar");
                                    return;
                                }

                                if (selectedNews instanceof Noticia) {
                                    Noticia noticiaSeleccionada = (Noticia) selectedNews;

                                 // Crear una ventana para editar la noticia seleccionada
                                    JFrame editNewsFrame = new JFrame("Editar Noticia");
                                    editNewsFrame.setSize(500, 500);
                                    editNewsFrame.setLayout(new GridBagLayout());

                                    GridBagConstraints constraints = new GridBagConstraints();
                                    constraints.anchor = GridBagConstraints.WEST;
                                    constraints.insets = new Insets(5, 5, 5, 5);

                                    // Campo de texto para el título de la noticia
                                    JTextField titleField = new JTextField(noticiaSeleccionada.getNombre(), 20);
                                    constraints.gridx = 0;
                                    constraints.gridy = 0;
                                    editNewsFrame.add(new JLabel("Título:"), constraints);
                                    constraints.gridx = 1;
                                    constraints.gridy = 0;
                                    editNewsFrame.add(titleField, constraints);

                                    // Etiqueta y campo de texto para el contenido de la noticia
                                    JLabel contentLabel = new JLabel("Contenido:");
                                    constraints.gridx = 0;
                                    constraints.gridy = 1;
                                    editNewsFrame.add(contentLabel, constraints);
                                    constraints.gridx = 1;
                                    constraints.gridy = 1;
                                    constraints.gridwidth = 1;
                                    constraints.fill = GridBagConstraints.BOTH;
                                    JTextArea contentArea = new JTextArea(noticiaSeleccionada.getContenido(), 5, 20);
                                    contentArea.setLineWrap(true); // Habilitar el ajuste automático de línea
                                    JScrollPane scrollPane = new JScrollPane(contentArea);
                                    editNewsFrame.add(scrollPane, constraints);

                                    // Cuadro de selección para la categoría de la noticia
                                    JComboBox<Categoria> categoryBox = new JComboBox<>(Categoria.values());
                                    categoryBox.setSelectedItem(noticiaSeleccionada.getCategoria());
                                    constraints.gridx = 0;
                                    constraints.gridy = 3;
                                    constraints.gridwidth = 1;
                                    constraints.fill = GridBagConstraints.NONE;
                                    editNewsFrame.add(new JLabel("Categoría:"), constraints);
                                    constraints.gridx = 1;
                                    constraints.gridy = 3;
                                    editNewsFrame.add(categoryBox, constraints);

                                    // Casilla de verificación para la noticia premium
                                    JCheckBox premiumCheckBox = new JCheckBox("Noticia Premium", noticiaSeleccionada.isNoticiaPremium());
                                    constraints.gridx = 0;
                                    constraints.gridy = 4;
                                    constraints.gridwidth = 2;
                                    editNewsFrame.add(premiumCheckBox, constraints);

                                    // Botón para confirmar la edición de la noticia
                                    JButton confirmButton = new JButton("Actualizar Noticia");
                                    constraints.gridx = 0;
                                    constraints.gridy = 5;
                                    constraints.gridwidth = 2;
                                    constraints.anchor = GridBagConstraints.CENTER;
                                    editNewsFrame.add(confirmButton, constraints);

                                    confirmButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            // Actualizar los valores de la noticia seleccionada
                                            String nuevoNombre = titleField.getText();
                                            String nuevoContenido = contentArea.getText();
                                            Categoria nuevaCategoria = (Categoria) categoryBox.getSelectedItem();
                                            boolean nuevaNoticiaPremium = premiumCheckBox.isSelected();

                                            // Llamar al método actualizar_noticia_completa() con los valores actualizados
                                            try {
                                                noticiaSeleccionada.actualizar_noticia_completa(nuevoNombre, nuevoContenido, nuevaCategoria, nuevaNoticiaPremium);
                                                JOptionPane.showMessageDialog(editNewsFrame, "Noticia actualizada exitosamente");
                                                editNewsFrame.dispose(); // Cerrar la ventana de edición de noticia
                                            } catch (Exception ex) {
                                                JOptionPane.showMessageDialog(editNewsFrame, "Error al actualizar noticia: " + ex.getMessage());
                                            }
                                        }
                                    });

                                    editNewsFrame.setVisible(true);
                                }
                            }
                        });

                        // Agregar un botón para eliminar noticias solo para usuarios con isEditor activo
                        JButton deleteNewsButton = new JButton("Eliminar Noticia");
                        deleteNewsButton.setBounds(1000, 400, 150, 30);
                        userWindow.add(deleteNewsButton);

                        deleteNewsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Obtener la lista de noticias del usuario actual
                                List<Noticia> noticias = null;
                                try {
                                    noticias = Noticia.mostrar_noticias();
                                } catch (ConexionFallidaException ex) {
                                    JOptionPane.showMessageDialog(userWindow, "Error al mostrar noticias: " + ex.getMessage());
                                    return;
                                }

                                // Crear un diálogo para seleccionar la noticia a eliminar
                                Object selectedNews = JOptionPane.showInputDialog(
                                        userWindow,
                                        "Selecciona la noticia a eliminar:",
                                        "Eliminar Noticia",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        noticias.toArray(),
                                        noticias.get(0));

                                // Verificar si se seleccionó una noticia
                                if (selectedNews instanceof Noticia) {
                                    Noticia noticiaSeleccionada = (Noticia) selectedNews;

                                    // Confirmar la eliminación de la noticia
                                    int option = JOptionPane.showConfirmDialog(
                                            userWindow,
                                            "¿Estás seguro de que deseas eliminar la noticia?",
                                            "Confirmar Eliminación",
                                            JOptionPane.YES_NO_OPTION);

                                    if (option == JOptionPane.YES_OPTION) {
                                        // Eliminar la noticia llamando al método eliminar_noticia()
                                        try {
                                            noticiaSeleccionada.eliminar_noticia();
                                            JOptionPane.showMessageDialog(userWindow, "Noticia eliminada exitosamente");
                                        } catch (Exception ex) {
                                            JOptionPane.showMessageDialog(userWindow, "Error al eliminar noticia: " + ex.getMessage());
                                        }
                                    }
                                }
                            }
                        });
                    }
                 // Después de iniciar sesión
                    if(usuario.isAdmin()) {
                    	// Agregar un botón para administrar usuarios
                    	JButton manageUsersButton = new JButton("Eliminar Usuario");
                    	manageUsersButton.setBounds(1000, 300, 150, 30);
                    	userWindow.add(manageUsersButton);

                    	// Crear una etiqueta con el texto deseado
                    	JLabel seleccionarUsuarioLabel = new JLabel("Selecciona el usuario a eliminar");
                    	seleccionarUsuarioLabel.setBounds(1000, 325, 200, 30); // Ajusta las coordenadas y tamaño según tus necesidades
                    	seleccionarUsuarioLabel.setForeground(Color.WHITE);
                    	seleccionarUsuarioLabel.setFont(seleccionarUsuarioLabel.getFont().deriveFont(Font.BOLD));
                    	userWindow.add(seleccionarUsuarioLabel);
                    	
                    	// Crear un JComboBox para mostrar todos los usuarios
                    	List<String> listaUsuarios = Usuario.obtenerTodosLosUsuarios();
                    	JComboBox<String> listaUsuariosComboBox = new JComboBox<>(listaUsuarios.toArray(new String[0]));
                    	listaUsuariosComboBox.setBounds(1000, 350, 150, 30);
                    	userWindow.add(listaUsuariosComboBox);

                    	manageUsersButton.addActionListener(new ActionListener() {
                    	    @Override
                    	    public void actionPerformed(ActionEvent e) {
                    	        // Acciones para administrar usuarios
                    	        String nombreUsuario = (String) listaUsuariosComboBox.getSelectedItem(); // Obtener el usuario seleccionado
                    	        try {
                    	            Usuario.eliminarUsuario(usuario, nombreUsuario); // Llamar al método eliminarUsuario
                    	            listaUsuariosComboBox.removeItem(nombreUsuario); // Eliminar el usuario del JComboBox
                    	            JOptionPane.showMessageDialog(null, "Usuario eliminado exitosamente!");
                    	        } catch (ConexionFallidaException ex) {
                    	            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario: " + ex.getMessage());
                    	        }
                    	    }
                    	});

                    	JButton reviewLogsButton = new JButton("Revisar Registros");
                    	reviewLogsButton.setBounds(1000, 400, 150, 30);
                    	userWindow.add(reviewLogsButton);

                    	reviewLogsButton.addActionListener(new ActionListener() {
                    	    @Override
                    	    public void actionPerformed(ActionEvent e) {
                    	        // Crear una nueva ventana para los registros
                    	        JFrame logsFrame = new JFrame("Registros");
                    	        logsFrame.setSize(500, 500);
                    	        
                    	        // Crear un JTextArea para mostrar los registros
                    	        JTextArea logsArea = new JTextArea();
                    	        logsArea.setEditable(false); // Para que el usuario no pueda modificar los registros

                    	        // Obtener los registros y mostrarlos en el JTextArea
                    	        try {
                    	            List<String> registros = Usuario.verRegistros();
                    	            for (String registro : registros) {
                    	                logsArea.append(registro + "\n");
                    	            }
                    	        } catch (IOException ex) {
                    	            logsArea.append("Error al leer los registros: " + ex.getMessage());
                    	        }

                    	        // Agregar un JScrollPane para permitir el desplazamiento si hay muchos registros
                    	        JScrollPane scrollPane = new JScrollPane(logsArea);
                    	        logsFrame.add(scrollPane);

                    	        // Mostrar la ventana de registros
                    	        logsFrame.setVisible(true);
                    	    }
                    	});

                    	JButton manageContentButton = new JButton("Modificar Usuario");
                    	manageContentButton.setBounds(1000, 450, 150, 30);
                    	userWindow.add(manageContentButton);

                    	manageContentButton.addActionListener(new ActionListener() {
                    	    @Override
                    	    public void actionPerformed(ActionEvent e) {
                    	        JFrame modificarUsuarioWindow = new JFrame("Modificar Usuario");
                    	        modificarUsuarioWindow.setSize(500, 500); 
                    	        modificarUsuarioWindow.setLayout(null);
                    	        modificarUsuarioWindow.setVisible(true);

                    	        JLabel seleccionarUsuarioLabel = new JLabel("Selecciona el usuario a modificar");
                    	        seleccionarUsuarioLabel.setBounds(100, 30, 200, 30);
                    	        modificarUsuarioWindow.add(seleccionarUsuarioLabel);

                    	        List<String> listaUsuarios = new ArrayList<>();
                    	        JComboBox<String> listaUsuariosComboBox;
                    	        try {
                    	            listaUsuarios = Usuario.obtenerTodosLosUsuarios();
                    	            listaUsuariosComboBox = new JComboBox<>(listaUsuarios.toArray(new String[0]));
                    	        } catch (Exception ex) {
                    	            JOptionPane.showMessageDialog(null, "Error al obtener la lista de usuarios: " + ex.getMessage());
                    	            return;
                    	        }
                    	        listaUsuariosComboBox.setBounds(100, 60, 200, 30);
                    	        modificarUsuarioWindow.add(listaUsuariosComboBox);

                    	        JLabel nombreUsuarioLabel = new JLabel("Nombre de Usuario");
                    	        nombreUsuarioLabel.setBounds(100, 170, 200, 30);
                    	        modificarUsuarioWindow.add(nombreUsuarioLabel);

                    	        JTextField nombreUsuarioTextField = new JTextField();
                    	        nombreUsuarioTextField.setBounds(100, 200, 200, 30);
                    	        modificarUsuarioWindow.add(nombreUsuarioTextField);

                    	        JLabel contraseñaLabel = new JLabel("Contraseña");
                    	        contraseñaLabel.setBounds(100, 240, 200, 30);
                    	        modificarUsuarioWindow.add(contraseñaLabel);

                    	        JTextField contraseñaTextField = new JTextField();
                    	        contraseñaTextField.setBounds(100, 270, 200, 30);
                    	        modificarUsuarioWindow.add(contraseñaTextField);

                    	        JCheckBox isEditorCheckBox = new JCheckBox("Es Editor");
                    	        isEditorCheckBox.setBounds(100, 310, 200, 30);
                    	        modificarUsuarioWindow.add(isEditorCheckBox);

                    	        JCheckBox isAdminCheckBox = new JCheckBox("Es Administrador");
                    	        isAdminCheckBox.setBounds(100, 350, 200, 30);
                    	        modificarUsuarioWindow.add(isAdminCheckBox);

                    	        JButton modificarUsuarioButton = new JButton("Confirmar modificación");
                    	        modificarUsuarioButton.setBounds(100, 400, 200, 30);
                    	        modificarUsuarioWindow.add(modificarUsuarioButton);

                    	        modificarUsuarioButton.addActionListener(new ActionListener() {
                    	            @Override
                    	            public void actionPerformed(ActionEvent e) {
                    	            	
                    	                String nombreUsuario = (String) listaUsuariosComboBox.getSelectedItem();
                    	                String nuevoNombreUsuario = nombreUsuarioTextField.getText();
                    	                String nuevaContraseña = contraseñaTextField.getText();
                    	                boolean esEditor = isEditorCheckBox.isSelected();
                    	                boolean esAdmin = isAdminCheckBox.isSelected();


                    	                try {
                    	                    Connection connection = DatabaseConnector.getConnection();
                    	                    Statement statement = connection.createStatement();

                    	                    String sql = "UPDATE usuario SET nombreUsuario = '" + nuevoNombreUsuario + "', contraseña = '" + nuevaContraseña + "', isEditor = " + (esEditor ? 1 : 0) + ", isAdmin = " + (esAdmin ? 1 : 0) + " WHERE nombreUsuario = '" + nombreUsuario + "'";
                    	                    statement.executeUpdate(sql);

                    	                    statement.close();
                    	                    connection.close();

                    	                    System.out.println("El usuario ha sido modificado exitosamente.");
                    	                    Usuario.escribirLog("El administrador " + usuario.getNombreUsuario() + " eliminó el usuario " + nombreUsuario + " el " + LocalDateTime.now());
                    	                    JOptionPane.showMessageDialog(null, "Usuario modificado exitosamente!");
                    	                } catch (SQLException | ConexionFallidaException ex) {
                    	                    ex.printStackTrace();
                    	                    JOptionPane.showMessageDialog(null, "Error al modificar el usuario: " + ex.getMessage());
                    	                }
                    	            }
                    	        });
                    	    }
                    	});
                    }
                    

                 // Agregar un panel para mostrar las noticias
                    JPanel newsPanel = new JPanel();
                    newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
                    newsPanel.setPreferredSize(new Dimension(900, 600)); // Establecer el tamaño preferido del panel de noticias

                    JScrollPane scrollPane = new JScrollPane(newsPanel);
                    scrollPane.setBounds(50, 200, 900, 450);
                    userWindow.add(scrollPane);

                 // Agregar un JLabel para el título del calendario
                    JLabel calendarTitle = new JLabel("Calendario Deportivo");
                    calendarTitle.setForeground(Color.WHITE); // Establecer el color del texto en blanco
                    calendarTitle.setBounds(960, 520, 200, 30); // Ajustar la posición y el tamaño según sea necesario
                    backgroundLabel.add(calendarTitle);

                    // Agregar un JCalendar
                    JCalendar jCalendar = new JCalendar();
                    jCalendar.setBounds(960, 550, 200, 200); // Ajustar el tamaño y la posición según sea necesario
                    backgroundLabel.add(jCalendar);

                 // Crear una lista ficticia de eventos para cada día del año
                    List<String> eventDates = new ArrayList<>();
                    Map<String, String> eventMessages = new HashMap<>();

                    // Categorías de eventos
                    List<String> categories = Arrays.asList("Partido de fútbol nacional", "Partido de fútbol internacional", "Baloncesto nacional", "Baloncesto internacional", "UFC");

                    Random random = new Random();

                    for (int month = 1; month <= 12; month++) {
                        for (int day = 1; day <= 15; day++) {
                            String date = String.format("2023-%02d-%02d", month, day);
                            String category = categories.get(random.nextInt(categories.size()));
                            String message = "Evento del día " + date + ": " + category;
                            eventDates.add(date);
                            eventMessages.put(date, message);
                        }
                    }

                    // Cuando se selecciona una fecha en el JCalendar...
                    jCalendar.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals("calendar")) {
                                // Convertir la fecha seleccionada a una cadena en el formato "YYYY-MM-DD"
                                String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(((Calendar) evt.getNewValue()).getTime());

                                // Verificar si la fecha seleccionada es una fecha de evento
                                if (eventDates.contains(selectedDate)) {
                                    // Obtener el mensaje del evento
                                    String message = eventMessages.get(selectedDate);
                                    if (message != null) {
                                        // Si hay un mensaje para la fecha seleccionada, mostrarlo
                                        JOptionPane.showMessageDialog(jCalendar, message);
                                    } else {
                                        // Si no hay mensaje, mostrar un mensaje genérico
                                        JOptionPane.showMessageDialog(jCalendar, "Evento en esta fecha!");
                                    }
                                }
                            }
                        }
                    });

                    // Obtener y mostrar las noticias
                    Noticia.actualizarNoticias(userWindow);
                    //backgroundLabel.add(sucripciones);
                    backgroundLabel.setLayout(null);

                    // Añadir el JLabel con la imagen de fondo a la ventana
                    userWindow.add(backgroundLabel);
                    userWindow.setLayout(null);
                    userWindow.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(loginFrame, "Error en el inicio de sesión: " + ex.getMessage());
                }
            });
            loginFrame.add(loginConfirmButton);

            loginFrame.setSize(300, 300);
            loginFrame.setLayout(null);
            loginFrame.setVisible(true);
        });

        JButton registerButton = new JButton("Registrate");
        registerButton.setBounds(500, 700, 200, 50);
        panel.add(registerButton);

        Color[] colores = {Color.RED, Color.BLUE, Color.BLACK, Color.WHITE};
        final int[] currentColoreIndex = {0};

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Cambiamos el color del botón cuando el mouse pasa por encima
                registerButton.setBackground(colors[currentColorIndex[0]]);
                currentColorIndex[0] = (currentColorIndex[0] + 1) % colors.length;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Restauramos el color original del botón cuando el mouse deja de pasar por encima
                registerButton.setBackground(UIManager.getColor("Button.background"));
            }
        });
        registerButton.addActionListener(e -> {
            JFrame registerFrame = new JFrame("Registro");
            registerFrame.setSize(300, 300);
            registerFrame.setLayout(null);

            // Campo para el nombre de usuario
            JLabel usernameLabel = new JLabel("Nombre de usuario");
            usernameLabel.setBounds(50, 50, 200, 30);
            registerFrame.add(usernameLabel);

            JTextField usernameField = new JTextField();
            usernameField.setBounds(50, 80, 200, 30);
            registerFrame.add(usernameField);

            // Campo para la contraseña
            JLabel passwordLabel = new JLabel("Contraseña");
            passwordLabel.setBounds(50, 120, 200, 30);
            registerFrame.add(passwordLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(50, 150, 200, 30);
            registerFrame.add(passwordField);

            // Botón de confirmación de registro
            JButton registerConfirmButton = new JButton("Registrarse");
            registerConfirmButton.setBounds(50, 190, 200, 30);
            registerConfirmButton.addActionListener(e1 -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Verificar que los campos no estén vacíos
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, rellene todos los campos");
                    return;
                }

                // Verificar que la contraseña contenga al menos una letra mayúscula, un número o un carácter especial
                Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[.,]).+$");
                Matcher matcher = pattern.matcher(password);
                if (!matcher.find()) {
                    JOptionPane.showMessageDialog(null, "La contraseña debe contener al menos una letra mayúscula, un número o un carácter especial como punto o coma");
                    return;
                }
                // Aquí llamamos al método de registro
                try {
                    Usuario.registrar_usuario(username, password);
                    JOptionPane.showMessageDialog(null, "Usuario registrado correctamente");
                    registerFrame.dispose(); // Cerrar la ventana de registro
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
                } catch (ConexionFallidaException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la conexión: " + ex.getMessage());
                }
            });
            registerFrame.add(registerConfirmButton);

            registerFrame.setVisible(true);
        });
        frame.setMinimumSize(new Dimension(800, 800)); // Establece el tamaño mínimo del marco a 800x800
        frame.pack(); // Ajusta el tamaño del marco según los componentes dentro de él
        frame.setResizable(false); // Desactiva la capacidad de cambiar el tamaño del marco
        frame.setVisible(true);
        Noticia.actualizarNoticias(frame);
    }

}
