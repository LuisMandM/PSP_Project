package dao;

import Models.Usuario;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDaoImplTest {

    @Test
    void create() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario 1");
        usuario.setApellido("Usuario 2");
        usuario.setEmail("Usuario 3");
        usuario.setUsername("Username");

        UsuarioDaoImpl dao = new UsuarioDaoImpl();
        try {
            Usuario created = dao.create(usuario);
            assertNotNull(created);
        } catch (SQLException e) {
            System.out.println("Error al crear el usuario: " + e.getMessage());
        }

    }

    @Test
    void findByUsername() {

        UsuarioDaoImpl dao = new UsuarioDaoImpl();
        try {
            Usuario usuario = dao.findByUsername("pepe");
            assertNull (usuario);
            System.out.println("Usuario no encontrado");
        } catch (SQLException e) {
            System.out.println("Error al consultar el usuario: " + e.getMessage());
        }
    }
}