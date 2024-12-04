package Server;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class Utils {
    public static KeyPair GenerarLLaves() {
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

    public static byte[] cifrarConClavePublica(String mensaje, PublicKey publicKey) {
        byte[] cifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cifrado = cipher.doFinal(mensaje.getBytes());  // Devuelve los bytes cifrados
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

    public static byte[] descifrarConClavePrivada(byte[] mensajeCifrado, PrivateKey privateKey) {
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
}
