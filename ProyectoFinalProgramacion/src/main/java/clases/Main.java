package clases;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Inicio de Sesión");
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
            // Aquí creas e inicias un nuevo JFrame para el inicio de sesión
            JFrame loginFrame = new JFrame("Inicio de Sesión");

            // Aquí puedes agregar campos de texto para la entrada de usuario y contraseña
            JTextField usernameField = new JTextField();
            usernameField.setBounds(50, 50, 200, 30);
            loginFrame.add(usernameField);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(50, 100, 200, 30);
            loginFrame.add(passwordField);

            // Botón de inicio de sesión en la nueva ventana
            JButton loginConfirmButton = new JButton("Iniciar Sesión");
            loginConfirmButton.setBounds(50, 150, 200, 30);
            loginConfirmButton.addActionListener(e1 -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Aquí llamas a tu método de inicio de sesión
                Usuario usuario = new Usuario(password, password, password, false, false);
                usuario.iniciar_sesion(username, password);
            });
            loginFrame.add(loginConfirmButton);

            loginFrame.setSize(300, 300);
            loginFrame.setLayout(null);
            loginFrame.setVisible(true);
        });
        // Botón para registrarse
        JButton registerButton = new JButton("Registrarse");
        registerButton.setBounds(500, 700, 200, 50);
        panel.add(registerButton);
        registerButton.addActionListener(e -> {
            // Acción al presionar el botón
            // Por ejemplo, abrir el diálogo de registro
            // ...
        });

        frame.setVisible(true);
    }
}