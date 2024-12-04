package dao;

import Models.Area;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import static Server.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class AreaDaoImplTest {

    AreaDaoImpl dao = new AreaDaoImpl();

    @Test
    void create() {
        KeyPair keys = GenerarLLaves();
        Area area = new Area();
        area.setNombre("Error Urgente");
        byte[] hashed = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update("Error".getBytes());
            hashed = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error de Algoritmo: " + e.getMessage());
        }
        area.setPassword(hashed);
        area.setPrivateKey(keys.getPrivate().getEncoded());
        area.setPublicKey(keys.getPublic().getEncoded());

        String prueba = "Prueba Encoded";
        byte[] pruebaBytes = cifrarConClavePublica(prueba.getBytes(), keys.getPublic());

        try {
            Area created = dao.create(area);
            assertNotNull(created);
            byte[] decoded = descifrarConClavePrivada(pruebaBytes, created.getPrivateKey());
            assertNotNull(decoded);
            //assertArrayEquals(pruebaBytes, decoded);
            assertEquals(prueba, new String(decoded));
            System.out.println("Prueba: " + prueba);
            System.out.println("Descifrado: " + new String(decoded));

        } catch (SQLException e) {
            System.out.println("Error de test add: " + e.getMessage());
        }

    }

    @Test
    void findByName() {
    }


    @Test
    void getAll() {


        try {
            ArrayList<Area> areas = (ArrayList<Area>) dao.getAll();
            assertNotNull(areas);

            for (Area area : areas) {
                System.out.println(area);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}