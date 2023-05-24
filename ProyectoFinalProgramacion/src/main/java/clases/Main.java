package clases;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

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

                    // Agregar un panel para contener los componentes
                    JPanel userPanel = new JPanel();
                    userPanel.setLayout(null);
                    userWindow.add(userPanel);

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

                    // Agregar un botón para refrescar las noticias
                    JButton refreshButton = new JButton("Refrescar Noticias");
                    refreshButton.setBounds(1000, 100, 150, 30);
                    userWindow.add(refreshButton);

                    refreshButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Aquí puedes implementar la lógica para refrescar las noticias
                            // Por ejemplo, puedes llamar a un método que actualice las noticias en el panel
                            // y luego llamar a userPanel.revalidate() y userPanel.repaint() para refrescar la ventana
                            // Como ejemplo, aquí mostraremos un mensaje de prueba
                            JOptionPane.showMessageDialog(userWindow, "Noticias refrescadas");
                        }
                    });

                    // Agregar el panel de usuario a la ventana
                    userPanel.setVisible(true);
                    userWindow.setLayout(null);
                    userWindow.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(loginFrame, "Error al iniciar sesión: " + ex.getMessage());
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
                    JOptionPane.showMessageDialog(registerFrame, "Usuario registrado exitosamente");
                    registerFrame.dispose(); // Cerrar la ventana de registro
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(registerFrame, "Error al registrar usuario: " + ex.getMessage());
                }
            });
            registerFrame.add(registerConfirmButton);

            registerFrame.setVisible(true);
        });

        frame.setVisible(true);
    }
}