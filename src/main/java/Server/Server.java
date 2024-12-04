package Server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyPair;

import static Server.Utils.GenerarLLaves;

public class Server {

    public static int PORT = 8888;
    private static KeyPair keys;

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", ".\\Certificados\\AlmacenSSL.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

        SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket = null;
        keys = GenerarLLaves();

        if (keys != null) {
            try {
                serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(PORT);
                System.out.println("Servidor Montado Correctamente");
                while (true) {
                    SSLSocket peticion = (SSLSocket) serverSocket.accept();
                    ServerThread serverThread = new ServerThread(peticion, keys);
                    serverThread.start();
                    System.out.println("Peticion delegada");
                }


            } catch (IOException e) {
                System.out.println("Error al crear el Socket servidor: " + e.getMessage());
            }
        } else {
            System.out.println("Error al crear el ServerSocket: No se han creado las llaves");
        }
    }

}
