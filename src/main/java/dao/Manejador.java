package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.sql.SQLException;

public class Manejador {
    private static Manejador instance;
    private EntityManager em;

    private Manejador() {
        this.em = Persistence.createEntityManagerFactory("default").createEntityManager();

    }

    public EntityManager getEm() {
        return em;
    }

    public static Manejador getInstance() throws SQLException {
        if (instance == null) {
            instance = new Manejador();
        }
        return instance;
    }
}
