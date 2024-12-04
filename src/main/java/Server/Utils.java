package Server;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

public class Utils {
    public synchronized static KeyPair GenerarLLaves() {
        KeyPair keys = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Tamaño de la clave de 2048 bits
            keys = keyPairGenerator.genKeyPair();
            System.out.println("LLaves generadas");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No se puede generar el RSA");
        }
        return keys;
    }

    public synchronized static byte[] cifrarConClavePublica(byte[] mensaje, PublicKey publicKey) {
        byte[] cifrado = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cifrado = cipher.doFinal(mensaje);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error de algoritmo: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.out.println("Excepcion de padding: " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("Error de llave: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println("Otros errores: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println("Otros errores: " + e.getMessage());
        }

        return cifrado;
    }

    public synchronized static byte[] descifrarConClavePrivada(byte[] mensajeCifrado, PrivateKey privateKey) {
        byte[] descifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            descifrado = cipher.doFinal(mensajeCifrado);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error de algoritmo: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.out.println("Excepcion de padding: " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("Error de llave: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println("Otros errores: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println("Otros errores: " + e.getMessage());
        }
        return descifrado;
    }

    private static <T> byte[] GetBytes(T object) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.out.println("Error en serializacion: " + e.getMessage());
        }

        return bytes;
    }
}
