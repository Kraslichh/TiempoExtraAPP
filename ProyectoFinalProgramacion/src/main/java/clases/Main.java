package clases;

import javax.imageio.ImageIO;
import javax.swing.*;

import enumeraciones.Categoria;

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
                try {
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
                    }

                    userWindow.setLayout(null);
                    userWindow.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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