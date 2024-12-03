package GUI;

import Models.Usuario;
import dao.UsuarioDaoImpl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.EventListener;

import static Server.Docker.DockerLauncher;

public class V_Login {
    private JTextField userTxtFld;
    private JTextField passwordTxtFld;
    private JButton iniciarSesionButton;
    private JButton registrarseButton;
    private JPanel panelPrincipal;

    UsuarioDaoImpl dao = new UsuarioDaoImpl();

    public V_Login(JFrame frame) {
        iniciarSesionButton.addActionListener(e -> {
            try {
                Usuario current = dao.findByUsername(userTxtFld.getText());
                if (SendPPeticion(current.getPassword(), passwordTxtFld.getText())) {
                    JOptionPane.showMessageDialog(panelPrincipal, """
                            Validacion de la clave correcta
                            """, "Paso", JOptionPane.INFORMATION_MESSAGE);

                } else JOptionPane.showMessageDialog(panelPrincipal, """
                        La contraseña es incorrecta
                        """, "Error", JOptionPane.ERROR_MESSAGE);


            } catch (SQLException ex) {
                System.out.println("Error al obtener el usuario");
            }
        });

        registrarseButton.addActionListener(e -> {
            try {
                // Set System L&F
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException ex) {
                // handle exception
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }

            JFrame jframe = new JFrame("Registro de Usuario");
            frame.setContentPane(new V_Register(jframe).getPanelPrincipal());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - frame.getWidth()) / 2;
            int y = (screenSize.height - frame.getHeight()) / 2;
            frame.setLocation(x, y);

        });
    }

    private boolean SendPPeticion(byte[] guardada, String intento) {
        System.setProperty("javax.net.ssl.trustStore", ".\\Certificados\\AlmacenUsuarioSSL.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "1234567");
        int port = 8888;
        String host = "localhost";
        boolean result = false;

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            SSLSocket peticion = (SSLSocket) socketFactory.createSocket(host, port);

            ObjectOutputStream salida = new ObjectOutputStream(peticion.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(peticion.getInputStream());

            salida.writeObject(100);
            int confirmacion = (Integer) entrada.readObject();

            if (confirmacion == 100) {
                salida.writeObject(guardada);
                salida.writeObject(intento);
                boolean validacion = (boolean) entrada.readObject();

                if (validacion) {
                    result = true;
                }

                entrada.close();
                salida.close();
                peticion.close();


            } else JOptionPane.showMessageDialog(panelPrincipal, """
                    Error Inesperado con el servidor
                    """, "Error", JOptionPane.ERROR_MESSAGE);


        } catch (IOException e) {
            System.out.println("Error en la creación y comunicaciónd el Socket: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al castear: " + e.getMessage());
        }


        return result;

    }

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        JFrame frame = new JFrame("Inicio - Gestion Campeonato");
        frame.setContentPane(new V_Login(frame).panelPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        if (DockerLauncher()) System.out.println("Docker Launcher exitoso");
        else System.out.println("Problemas con el Docker");
    }
}
