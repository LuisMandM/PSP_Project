package dao;

import jakarta.persistence.EntityTransaction;

import java.sql.SQLException;

abstract class Dao<T, K> {

    public abstract T find(K id) throws SQLException;

    public T create(T t) throws SQLException {
        EntityTransaction entityTransaction = Manejador.getInstance().getEm().getTransaction();
        entityTransaction.begin();
        Manejador.getInstance().getEm().persist(t);
        entityTransaction.commit();
        return t;
    }

    public T update(T t) throws SQLException {
        EntityTransaction entityTransaction = Manejador.getInstance().getEm().getTransaction();
        entityTransaction.begin();
        Manejador.getInstance().getEm().merge(t);
        entityTransaction.commit();
        return t;
    }

    public void delete(T t) throws SQLException {
        EntityTransaction entityTransaction = Manejador.getInstance().getEm().getTransaction();
        entityTransaction.begin();
        Manejador.getInstance().getEm().remove(t);
        entityTransaction.commit();

    }
}
