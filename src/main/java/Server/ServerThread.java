package Server;

import Models.Usuario;
import dao.UsuarioDaoImpl;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.*;
import java.sql.SQLException;
import java.util.Arrays;

import static Server.Utils.*;

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
        System.out.println("Iniciando Intercambio de llaves");
        try {
            PublicKey publicKeyClient = (PublicKey) entrada.readObject();
            salida.writeObject(keys.getPublic());
            System.out.println("Intercambio de llaves correcto");
            try {
                byte[] peticionRaw = (byte[]) entrada.readObject();
                peticionRaw = descifrarConClavePrivada(peticionRaw, keys.getPrivate());
                int peticion = ByteBuffer.wrap(peticionRaw).getInt();

                byte[] peticionBytes = ByteBuffer.allocate(4).putInt(peticion).array();
                salida.writeObject(cifrarConClavePublica(peticionBytes, publicKeyClient));
                switch (peticion) {
                    case 100:
                        byte[] contrasenia = (byte[]) entrada.readObject();
                        contrasenia = descifrarConClavePrivada(contrasenia, keys.getPrivate());

                        byte[] passRaw = (byte[]) entrada.readObject();
                        passRaw = descifrarConClavePrivada(passRaw, keys.getPrivate());
                        String passField = new String(passRaw);

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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en intercambio de llaves: " + e.getMessage());
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
