package clases;

import javax.imageio.ImageIO;
import javax.swing.*;

import enumeraciones.Categoria;
import excepciones.ConexionFallidaException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TiempoExtra inicio de sesion");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        // Botón para iniciar sesión
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setBounds(100, 700, 200, 50);
        panel.add(loginButton);
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

            // Botón de inicio de sesión en la nueva ventana
            JButton loginConfirmButton = new JButton("Iniciar Sesión");
            loginConfirmButton.setBounds(50, 150, 200, 30);
            loginConfirmButton.addActionListener(e1 -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Crear una nueva instancia de Usuario
                Usuario usuario = new Usuario(username, username, password, false, false);

                // Intentar iniciar sesión
                usuario.iniciar_sesion(username, password);

                // Si la línea anterior no lanza una excepción, la autenticación fue exitosa
                loginFrame.dispose(); // Cerrar la ventana de inicio de sesión

                // Crear una nueva ventana después de iniciar sesión
                JFrame userWindow = new JFrame("Bienvenido, " + username);
                userWindow.setSize(1200, 800);
                userWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Agregar un botón para cerrar la sesión
                JButton logoutButton = new JButton("Cerrar Sesión");
                logoutButton.setBounds(1000, 50, 150, 30);
                userWindow.add(logoutButton);

                logoutButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Realizar acciones para cerrar la sesión
                        // Por ejemplo, volver a mostrar la ventana de inicio de sesión y cerrar la ventana actual

                        // Mostrar la ventana de inicio de sesión
                        frame.setVisible(true);

                        // Cerrar la ventana actual
                        userWindow.dispose();
                    }
                });

                // Agregar un botón para crear noticias solo para usuarios con isEditor activo
                if (usuario.isEditor()) {
                    JButton createNewsButton = new JButton("Crear Noticia");
                    createNewsButton.setBounds(1000, 100, 150, 30);
                    userWindow.add(createNewsButton);

                    createNewsButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Abrir una nueva ventana para introducir los detalles de la noticia
                            JFrame createNewsFrame = new JFrame("Crear Noticia");
                            createNewsFrame.setSize(500, 500);
                            createNewsFrame.setLayout(new FlowLayout());

                            // Campos de texto para los detalles de la noticia
                            JTextField titleField = new JTextField(20);
                            JTextArea contentArea = new JTextArea(5, 20);
                            JComboBox<Categoria> categoryBox = new JComboBox<>(Categoria.values());
                            JCheckBox premiumCheckBox = new JCheckBox("Noticia Premium");

                            // Añadir los campos a la ventana
                            createNewsFrame.add(new JLabel("Título:"));
                            createNewsFrame.add(titleField);
                            createNewsFrame.add(new JLabel("Contenido:"));
                            createNewsFrame.add(contentArea);
                            createNewsFrame.add(new JLabel("Categoría:"));
                            createNewsFrame.add(categoryBox);
                            createNewsFrame.add(premiumCheckBox);

                            // Botón para confirmar la creación de la noticia
                            JButton confirmButton = new JButton("Crear Noticia");
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

                            createNewsFrame.add(confirmButton);
                            createNewsFrame.setVisible(true);
                        }
                    });

                 // Agregar un botón para editar noticias solo para usuarios con isEditor activo
                    JButton editNewsButton = new JButton("Editar Noticia");
                    editNewsButton.setBounds(1000, 150, 150, 30);
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
                            if (selectedNews instanceof Noticia) {
                                Noticia noticiaSeleccionada = (Noticia) selectedNews;

                                // Abrir una nueva ventana para editar la noticia seleccionada
                                JFrame editNewsFrame = new JFrame("Editar Noticia");
                                editNewsFrame.setSize(500, 500);
                                editNewsFrame.setLayout(new FlowLayout());

                                // Campos de texto para los detalles de la noticia
                                JTextField titleField = new JTextField(noticiaSeleccionada.getNombre(), 20);
                                JTextArea contentArea = new JTextArea(noticiaSeleccionada.getContenido(), 5, 20);
                                JComboBox<Categoria> categoryBox = new JComboBox<>(Categoria.values());
                                categoryBox.setSelectedItem(noticiaSeleccionada.getCategoria());
                                JCheckBox premiumCheckBox = new JCheckBox("Noticia Premium", noticiaSeleccionada.isNoticiaPremium());

                                // Añadir los campos a la ventana
                                editNewsFrame.add(new JLabel("Título:"));
                                editNewsFrame.add(titleField);
                                editNewsFrame.add(new JLabel("Contenido:"));
                                editNewsFrame.add(contentArea);
                                editNewsFrame.add(new JLabel("Categoría:"));
                                editNewsFrame.add(categoryBox);
                                editNewsFrame.add(premiumCheckBox);

                                // Botón para confirmar la edición de la noticia
                                JButton confirmButton = new JButton("Actualizar Noticia");
                                confirmButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        // Actualizar los valores de la noticia seleccionada
                                        noticiaSeleccionada.setNombre(titleField.getText());
                                        noticiaSeleccionada.setContenido(contentArea.getText());
                                        noticiaSeleccionada.setCategoria((Categoria) categoryBox.getSelectedItem());
                                        noticiaSeleccionada.setNoticiaPremium(premiumCheckBox.isSelected());

                                        // Llamar al método actualizar_noticia() con la noticia actualizada
                                        try {
                                            noticiaSeleccionada.actualizar_noticia(titleField.getText());
                                            JOptionPane.showMessageDialog(editNewsFrame, "Noticia actualizada exitosamente");
                                            editNewsFrame.dispose(); // Cerrar la ventana de edición de noticia
                                        } catch (Exception ex) {
                                            JOptionPane.showMessageDialog(editNewsFrame, "Error al actualizar noticia: " + ex.getMessage());
                                        }
                                    }
                                });

                                editNewsFrame.add(confirmButton);
                                editNewsFrame.setVisible(true);
                            }
                        }
                    });

                    // Agregar un botón para eliminar noticias solo para usuarios con isEditor activo
                    JButton deleteNewsButton = new JButton("Eliminar Noticia");
                    deleteNewsButton.setBounds(1000, 200, 150, 30);
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

                // Agregar un panel para mostrar las noticias
                JPanel newsPanel = new JPanel();
                newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
                JScrollPane scrollPane = new JScrollPane(newsPanel);
                scrollPane.setBounds(50, 200, 900, 450);
                userWindow.add(scrollPane);

                // Obtener la lista de noticias para mostrar
                List<Noticia> noticias = null;
                try {
                    noticias = Noticia.mostrar_noticias();
                } catch (ConexionFallidaException ex) {
                    JOptionPane.showMessageDialog(userWindow, "Error al mostrar noticias: " + ex.getMessage());
                    return;
                }

                // Mostrar las noticias en el panel
                for (Noticia noticia : noticias) {
                    JPanel noticiaPanel = new JPanel();
                    noticiaPanel.setBorder(BorderFactory.createEtchedBorder());
                    noticiaPanel.setLayout(new BoxLayout(noticiaPanel, BoxLayout.Y_AXIS));

                    JLabel titleLabel = new JLabel(noticia.getNombre());
                    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noticiaPanel.add(titleLabel);

                    JLabel authorLabel = new JLabel("Autor: " + noticia.getAutor().getNombreUsuario());
                    authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noticiaPanel.add(authorLabel);

                    JLabel dateLabel = new JLabel("Fecha de publicación: " + noticia.getFechaPublicacion().toString());
                    dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noticiaPanel.add(dateLabel);

                    JLabel categoryLabel = new JLabel("Categoría: " + noticia.getCategoria().toString());
                    categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noticiaPanel.add(categoryLabel);

                    JLabel premiumLabel = new JLabel("Premium: " + (noticia.isNoticiaPremium() ? "Sí" : "No"));
                    premiumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noticiaPanel.add(premiumLabel);

                    // Usar un JLabel para mostrar el contenido centrado
                    JLabel contentLabel = new JLabel(noticia.getContenido());
                    contentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    contentLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    noticiaPanel.add(contentLabel);

                    newsPanel.add(noticiaPanel);
                    newsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre noticias
                }

                userWindow.setLayout(null);
                userWindow.setVisible(true);
            });
            loginFrame.add(loginConfirmButton);

            loginFrame.setSize(300, 300);
            loginFrame.setLayout(null);
            loginFrame.setVisible(true);
        });

        // Botón para registrarse
        JButton registerButton = new JButton("Registrate");
        registerButton.setBounds(500, 700, 200, 50);
        panel.add(registerButton);
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

                // Aquí llamamos al método de registro
                try {
                    Usuario.registrar_usuario(username, password);
                    JOptionPane.showMessageDialog(null, "Usuario registrado correctamente");
                    registerFrame.dispose(); // Cerrar la ventana de registro
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error en el registro: " + ex.getMessage());
                }
            });
            registerFrame.add(registerConfirmButton);

            registerFrame.setVisible(true);
        });

        frame.setVisible(true);
    }
}
