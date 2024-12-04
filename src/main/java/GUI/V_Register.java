package GUI;

import Models.Usuario;
import dao.UsuarioDaoImpl;

import javax.crypto.Cipher;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class V_Register {
    private JPanel panelPrincipal;
    private JTextField nombreTxtFld;
    private JTextField apellidosTxtFld;
    private JTextField dateTxtFld;
    private JTextField mailTxtFld;
    private JTextField userTxtFld;
    private JTextField passwTxtFld;
    private JButton infoUser;
    private JButton infoPassword;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton infoDate;

    private UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();

    //regex usuario [a-zA-Z\d_$@ñÑ]{8}
    //regex password [a-zA-Z\d_$@ñÑ]{12}
    //regex email public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
    //    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //
    //public static boolean validate(String emailStr) {
    //        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    //        return matcher.matches();
    //}


    public V_Register() {
    }

    public V_Register(JFrame frame) {

        guardarButton.addActionListener(e -> {
            if (ValidarCampos()) {
                Date fecha = CrearFecha(dateTxtFld.getText());
                if (ValidarRegex() && fecha != null) {
                    Usuario current = new Usuario();
                    current.setNombre(nombreTxtFld.getText());
                    current.setApellido(apellidosTxtFld.getText());
                    current.setEmail(mailTxtFld.getText());
                    current.setfNacimiento(fecha);
                    current.setUsername(userTxtFld.getText());
                    byte[] hashedPassword = HashPassword(passwTxtFld.getText());
                    if (hashedPassword != null) {
                        current.setPassword(hashedPassword);
                        UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
                        try {
                            KeyPair keys = GenerarLLaves();
                            if (keys != null) {
                                current.setPrivateKey(keys.getPrivate().getEncoded());
                                current.setPublicKey(keys.getPublic().getEncoded());
                                current.setIncidencias(new ArrayList<>());
                                Usuario created = usuarioDao.create(current);
                                if (created != null) {
                                    JOptionPane.showMessageDialog(panelPrincipal, """
                                            El usuario se ha creado Exitosamente
                                            """, "Usuario Creado Exitosamente", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else System.out.println("Error"); //salida.writeObject(-101);
                        } catch (SQLException ex) {
                            System.out.println("Error al hacer la comprobación: " + ex.getMessage());
                        }


                    } else JOptionPane.showMessageDialog(panelPrincipal, """
                            Error Inesperado con el hash de la contraseña
                            """, "Error", JOptionPane.ERROR_MESSAGE);

                } else JOptionPane.showMessageDialog(panelPrincipal, """
                        Los datos incluidos en la fecha, usuario, contraseña o email
                        no siguen los formatos indicados, apoyese en los botones
                        de consulta si tiene duda
                        """, "Error Formato", JOptionPane.ERROR_MESSAGE);


            } else JOptionPane.showMessageDialog(panelPrincipal, """
                    Todos los campos deben estar diligenciados
                    """, "Error", JOptionPane.ERROR_MESSAGE);


        });


        infoPassword.addActionListener(e -> {
            JOptionPane.showMessageDialog(panelPrincipal, """
                    La contraseña debe contener minimo 12 caracteres
                    puede incluir letras, números y los caracteres especiales
                    '_' , '$' , '@'
                    """, "Formato", JOptionPane.INFORMATION_MESSAGE);
        });

        infoUser.addActionListener(e -> {
            JOptionPane.showMessageDialog(panelPrincipal, """
                    El usuario debe contener minimo 8 caracteres
                    puede incluir letras, números y los caracteres especiales
                    '_' , '$' , '@'
                    """, "Formato", JOptionPane.INFORMATION_MESSAGE);
        });

        infoDate.addActionListener(e -> {
            JOptionPane.showMessageDialog(panelPrincipal, """
                    La fecha de nacimiento debe seguir
                    el formato dd/mm/yyyy
                    """, "Formato", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelarButton.addActionListener(e -> frame.dispose());

    }

    //region Validaciones
    private Date CrearFecha(String fecha) {
        SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return fechaFormat.parse(fecha);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean ValidarPassword(String password) {
        String regex = "^[a-zA-Z\\d_$@ñÑ]{12,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean ValidarUsuario(String username) {
        String regex = "[a-zA-Z\\d_$@ñÑ]{8,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public boolean ValidarEmail(String username) {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private boolean ValidarCampos() {
        return !nombreTxtFld.getText().isEmpty() && !apellidosTxtFld.getText().isEmpty() && !dateTxtFld.getText().isEmpty()
                && !mailTxtFld.getText().isEmpty() && !passwTxtFld.getText().isEmpty() && !userTxtFld.getText().isEmpty();
    }

    private boolean ValidarRegex() {
        boolean validacion1 = ValidarUsuario(userTxtFld.getText());
        boolean validacion2 = ValidarPassword(passwTxtFld.getText());
        boolean validacion3 = ValidarEmail(mailTxtFld.getText());

        return ValidarUsuario(userTxtFld.getText()) && ValidarPassword(passwTxtFld.getText()) && ValidarEmail(mailTxtFld.getText());
    }
    //endregion

    private KeyPair GenerarLLaves() {
        KeyPair keys = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Tamaño de la clave de 2048 bits
            keys = keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No se puede generar el RSA");
        }

        return keys;
    }

    private byte[] HashPassword(String password) {
        byte[] hashed = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            hashed = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error de Algoritmo: " + e.getMessage());
        }
        return hashed;
    }

    private boolean SendPPeticion(Usuario usuario) {
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
                salida.writeObject(usuario);
                confirmacion = (Integer) entrada.readObject();

                switch (confirmacion) {
                    case 100:
                        result = true;
                        break;
                    case -101:
                        JOptionPane.showMessageDialog(panelPrincipal, """
                                El usuario indicado ya existe
                                """, "Error", JOptionPane.ERROR_MESSAGE);
                        break;

                    case -102:
                        JOptionPane.showMessageDialog(panelPrincipal, """
                                Error generando las llaves del usuario
                                no se ha creado
                                """, "Error", JOptionPane.ERROR_MESSAGE);
                        break;

                    case -500:
                        JOptionPane.showMessageDialog(panelPrincipal, """
                                Error Inesperado con la base de datos
                                """, "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }

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
        frame.setContentPane(new V_Register(frame).panelPrincipal);
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

    private static boolean DockerLauncher() {
        boolean result = false;
        String composeFilePath = ".\\Lanzadera\\docker-compose.yml";
        String[] command = {"docker-compose", "-f", composeFilePath, "up", "-d"};

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Docker Compose se ejecutó correctamente.");
                result = true;
            } else {
                System.err.println("Error al ejecutar Docker Compose. Código de salida: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al ejecutar Docker Compose");
        }
        return result;

    }


}
