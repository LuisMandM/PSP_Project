package Server;

import Models.Usuario;
import dao.UsuarioDaoImpl;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ServerThread extends Thread {
    private SSLSocket peticion;
    private KeyPair keys;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    public ServerThread(SSLSocket peticion, KeyPair keys) {
        this.peticion = peticion;
        this.keys = keys;
        try {
            salida = new ObjectOutputStream(peticion.getOutputStream());
            entrada = new ObjectInputStream(peticion.getInputStream());
        } catch (IOException e) {
            System.out.println("Error al crear entradas y salidas del hilo servidor: " + e.getMessage());
        }
    }

    @Override
    public void run() {

        URL persistenceXml = Thread.currentThread().getContextClassLoader().getResource("META-INF/persistence.xml");
        System.out.println("persistence.xml encontrado en: " + persistenceXml);

        try {
            int peticion = (Integer) entrada.readObject();
            salida.writeObject(peticion);
            switch (peticion) {
                case 100:
                    byte[] contrasenia = (byte[]) entrada.readObject();
                    String passField = (String) entrada.readObject();
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        messageDigest.update(passField.getBytes());
                        byte[] hashed = messageDigest.digest();
                        if (Arrays.equals(hashed, contrasenia)) {
                            salida.writeObject(true);
                        } else salida.writeObject(false);
                    } catch (NoSuchAlgorithmException e) {
                        System.out.println("Error en la codificacion de la contraseña: " + e.getMessage());
                        salida.writeObject(false);
                    }


                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error al recibir el objeto: " + e.getMessage());
            try {
                salida.writeObject(-500);
            } catch (IOException ex) {
                System.out.println("Si esto falla ya salvame diosito");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al parcear el objeto: " + e.getMessage());
        }
    }


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
}
