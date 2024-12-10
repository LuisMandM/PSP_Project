package Server;

import Models.Incidencia;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Utils {

    /**
     * Genera un par de llaves con algoritmo RSA.
     *
     * @return KeyPair - RSA
     */
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

    /**
     * Cifrado de un byte Array con clave Publica
     *
     * @param mensaje   byte Array
     * @param publicKey PublicKey - RSA
     * @return byte Array con el cifrado del mensaje.
     */
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

    /**
     * Descifrado con Clave privada
     *
     * @param mensajeCifrado byte Array cifrado con RSA
     * @param privateKey     PrivateKey RSA
     * @return byte Array descifrado
     */
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

    /**
     * Reconstruccion de un byte Array perteneciente a una PublicKey con algoritmo RSA
     *
     * @param publicKeyBytes byte Array de clave Publica
     * @return PublicKey construida con RSA.
     */
    public static PublicKey bytesToPublicKey(byte[] publicKeyBytes) {
        PublicKey publicKey = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println("InvalidKeySpecException: " + e.getMessage());
        }
        return publicKey;
    }

    /**
     * Reconstruccion de un byte Array perteneciente a una PrivateKey con algoritmo RSA
     *
     * @param privateKeyBytes byte Array de clave Privada
     * @return PrivateKey construida con RSA.
     */
    public static PrivateKey bytesToPrivateKey(byte[] privateKeyBytes) {
        PrivateKey privateKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println("InvalidKeySpecException: " + e.getMessage());
        }

        return privateKey;
    }


    /**
     * Serializacion de una instancia de la clase Incidencia
     *
     * @param obj Instancia de Incidencia
     * @return Byte Array correspondiente al objeto dado
     */
    public static byte[] IncidenciaToBytes(Incidencia obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.out.println("Error al pasar a Bytes: " + e.getMessage());
        }

        return bytes;
    }

    /**
     * Construccion de una instancia de Incidencia a partir de un byteArray
     *
     * @param data byte Array
     * @return Instancia de Incidencia
     */
    public static Incidencia BytesToIncidencia(byte[] data) {
        Incidencia incidencia = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            incidencia = (Incidencia) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al castear Array de Bytes: " + e.getMessage());
        }
        return incidencia;
    }

    /**
     * Creacion de SecretKey con algoritmo AES
     *
     * @return SecretKey - AES
     */
    public static SecretKey GenerarLLaveSincro() {
        SecretKey key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);  // 128 bits de longitud
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No se puede generar el AES");
        }
        return key;
    }

    /**
     * Recosntruccion de una SecretKey con algoritmo AES
     *
     * @param keyBytes byte Array con los bytes pertenecientes a la llaves
     * @return SecretKey AES
     */
    public static SecretKey CastAES(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }


    /**
     * Cifrado de byteArray con SecretKey con algoritmo AES
     *
     * @param objeto byteArray origen
     * @param key    SecretKey AES
     * @return byteArray cifrado AES
     */
    public static byte[] CifrarAES(byte[] objeto, SecretKey key) {
        byte[] cifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cifrado = cipher.doFinal(objeto);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.out.println("NoSuchPaddingException: " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("InvalidKeyException: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println("IllegalBlockSizeException: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println("BadPaddingException: " + e.getMessage());
        }
        return cifrado;
    }


    /**
     * Descifrado de byteArray con SecretKey con algoritmo AES
     *
     * @param textoCifrado byte Array cifrado con AES
     * @param key          SecretKey AES
     * @return byteArray descifrado.
     */
    public static byte[] DescifrarAES(byte[] textoCifrado, SecretKey key) {

        byte[] descifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            descifrado = cipher.doFinal(textoCifrado);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.out.println("NoSuchPaddingException: " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("InvalidKeyException: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println("IllegalBlockSizeException: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println("BadPaddingException: " + e.getMessage());
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
