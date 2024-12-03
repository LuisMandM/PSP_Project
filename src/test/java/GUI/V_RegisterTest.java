package GUI;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class V_RegisterTest {

    @Test
    void validarEmail() {
        String email = "test@gmail.com";
        String email2 = "luismedina@example.com";

        V_Register register = new V_Register();

        boolean result1 = register.ValidarEmail(email);
        boolean result2 = register.ValidarEmail(email2);

        assertTrue(result1);
        assertTrue(result2);

    }
}