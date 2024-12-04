package Server;

import Models.Incidencia;
import Models.Nivel;
import Models.Usuario;
import dao.UsuarioDaoImpl;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

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
                    case 200:
                        byte[] AESCrypted = (byte[]) entrada.readObject();
                        byte[] AES = descifrarConClavePrivada(AESCrypted, keys.getPrivate());
                        SecretKey AESKey = CastAES(AES);

                        byte[] report = (byte[]) entrada.readObject();
                        report = DescifrarAES(report, AESKey);
                        byte[] sign = (byte[]) entrada.readObject();

                        if (VerifySign(report, sign, publicKeyClient)) {
                            salida.writeObject(true);
                            Incidencia incidencia = BytesToIncidencia(report);
                            if (incidencia != null) {
                                salida.writeObject(true);
                                Incidencia gestionada = GestionarIncidencia(incidencia);
                                byte[] reportGest = IncidenciaToBytes(gestionada);
                                byte[] reportCipher = CifrarAES(reportGest, AESKey);
                                salida.writeObject(reportCipher);
                            } else
                                salida.writeObject(false);

                        } else salida.writeObject(false);


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

    private Incidencia GestionarIncidencia(Incidencia incidencia) {
        int nivel = new Random().nextInt(0, 3);
        int horas = 0;
        switch (nivel) {
            case 0:
                incidencia.setNivel(Nivel.LEVE);
                horas = new Random().nextInt(24, 72);
                incidencia.setTiempo(hoursToMillis(horas));
                break;
            case 1:
                incidencia.setNivel(Nivel.MODERADO);
                horas = new Random().nextInt(24, 36);
                incidencia.setTiempo(hoursToMillis(horas));
                break;
            case 2:
                incidencia.setNivel(Nivel.ALTO);
                horas = new Random().nextInt(0, 24);
                incidencia.setTiempo(hoursToMillis(horas));
                break;
        }
        SecretKey key = GenerarLLaveSincro();
        incidencia.setLlave(key.getEncoded());
        return incidencia;
    }

    private static boolean VerifySign(byte[] report, byte[] sign, PublicKey publicKey) {
        boolean verificado = false;
        try {
            Signature verify = Signature.getInstance("SHA1withRSA");
            verify.initVerify(publicKey);
            verify.update(report);
            verificado = verify.verify(sign);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            System.out.println("Error al verificar la firma: " + e.getMessage());
        }
        return verificado;
    }

    private long hoursToMillis(long hours) {
        return hours * 60 * 60 * 1000;
    }
}
