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
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.EventListener;

import static Server.Docker.DockerLauncher;
import static Server.Utils.*;

public class V_Login {
    private JTextField userTxtFld;
    private JTextField passwordTxtFld;
    private JButton iniciarSesionButton;
    private JButton registrarseButton;
    private JPanel panelPrincipal;

    UsuarioDaoImpl dao = new UsuarioDaoImpl();
    private KeyPair keysLogin = null;

    public V_Login(JFrame frame) {
        keysLogin = GenerarLLaves();

        /**
         * iniciarSesionButton.addActionListener:
         * Este bloque se encarga de consultar a la base de datos el usuario que intenta entrar, en caso de
         * existir devuelve el objeto y con este se procede a ser las diferentes verificaciones de acceso,
         * en caso de ser validas este objeto se envia a la ventana de Incidencia.
         */
        iniciarSesionButton.addActionListener(e -> {
            try {
                Usuario current = dao.findByUsername(userTxtFld.getText());
                if (current != null) {
                    if (SendPPeticion(current.getPassword(), passwordTxtFld.getText())) {
                        try {
                            // Set System L&F
                            UIManager.setLookAndFeel(
                                    UIManager.getSystemLookAndFeelClassName());
                        } catch (UnsupportedLookAndFeelException ex) {
                            // handle exception
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }

                        JFrame jframe = new JFrame("Panel Incidencia");
                        jframe.setContentPane(new V_Incidencia(current, jframe).getPanelPrincipal());
                        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        jframe.pack();
                        jframe.setVisible(true);
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Dimension screenSize = toolkit.getScreenSize();
                        int x = (screenSize.width - jframe.getWidth()) / 2;
                        int y = (screenSize.height - jframe.getHeight()) / 2;
                        jframe.setLocation(x, y);
                        frame.dispose();

                    } else JOptionPane.showMessageDialog(panelPrincipal, """
                            La contraseña es incorrecta
                            """, "Error", JOptionPane.ERROR_MESSAGE);
                } else JOptionPane.showMessageDialog(panelPrincipal, """
                        Usuario incorrecto
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
            jframe.setContentPane(new V_Register(jframe).getPanelPrincipal());
            jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jframe.pack();
            jframe.setVisible(true);
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - jframe.getWidth()) / 2;
            int y = (screenSize.height - jframe.getHeight()) / 2;
            jframe.setLocation(x, y);

        });
    }

    /**
     * Codigo de procedimiento en el servidor: 100
     * Se encarga de enviar al servidor el hash de la contraseña del usuario que intenta entrar, y la contraseña que esta
     * probando, se comunica con el servidor para que este se encargue de validar si es corrrecta o no la contraseña.
     * Uso de cifrado asimetrico para la comunicacion con el servidor.
     * Conexion usando certificados mediante SSLSockets.
     *
     * @param guardada byte Array con el hash de la contraseña almacenado en Base de datos
     * @param intento  intento de contraseña
     * @return true: en caso de ser valida la contraseña, false: en caso de no serlo
     */
    private boolean SendPPeticion(byte[] guardada, String intento) {
        System.setProperty("javax.net.ssl.trustStore", ".\\Certificados\\AlmacenUsuarioSSL.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "1234567");
        int port = 8888;
        String host = "localhost";
        boolean result = false;
        PublicKey serverPublicKey = null;

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            SSLSocket peticion = (SSLSocket) socketFactory.createSocket(host, port);

            ObjectOutputStream salida = new ObjectOutputStream(peticion.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(peticion.getInputStream());

            salida.writeObject(keysLogin.getPublic());
            serverPublicKey = (PublicKey) entrada.readObject();

            int mensaje = 100;
            byte[] peticionbytes = ByteBuffer.allocate(4).putInt(mensaje).array();
            salida.writeObject(cifrarConClavePublica(peticionbytes, serverPublicKey));

            byte[] confirmacionRaw = (byte[]) entrada.readObject();
            confirmacionRaw = descifrarConClavePrivada(confirmacionRaw, keysLogin.getPrivate());
            int confirmacion = ByteBuffer.wrap(confirmacionRaw).getInt();

            if (confirmacion == 100) {
                salida.writeObject(cifrarConClavePublica(guardada, serverPublicKey));
                salida.writeObject(cifrarConClavePublica(intento.getBytes(), serverPublicKey));
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

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    //    public static void main(String[] args) {
//        try {
//            // Set System L&F
//            UIManager.setLookAndFeel(
//                    UIManager.getSystemLookAndFeelClassName());
//        } catch (UnsupportedLookAndFeelException e) {
//            // handle exception
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//        JFrame frame = new JFrame("Inicio - Gestion Campeonato");
//        frame.setContentPane(new V_Login(frame).panelPrincipal);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        Dimension screenSize = toolkit.getScreenSize();
//        int x = (screenSize.width - frame.getWidth()) / 2;
//        int y = (screenSize.height - frame.getHeight()) / 2;
//        frame.setLocation(x, y);
//        if (DockerLauncher()) System.out.println("Docker Launcher exitoso");
//        else System.out.println("Problemas con el Docker");
//    }
}
