package test;
import org.junit.jupiter.api.Test;
import validacoes.EmailValidator;
import validacoes.ValidationResult;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTest {
    //01
    @Test
    void shouldNotAllowEmailWithFewerThan4Characters() {
        String email = "t";

        ValidationResult result = EmailValidator.validate(email);
        assertEquals(false, result.isValid());
        assertEquals("Email precisa ter no mínimo 4 caracteres!", result.getMessage());

    }

    //02
    @Test
    void shouldNotAllowEmailWithGreaterThan255Characters() {
        String localPart = "a".repeat(260);
        String email = localPart + "@gmail.com";

        ValidationResult result = EmailValidator.validate(email);
        assertEquals(false, result.isValid());
        assertEquals("Email deve ter menos de 255 caracteres!", result.getMessage());
    }

    // 03
    // Deve permitir, mas não passar, pois está mal formatado
    @Test
    void shouldAllowEmailWith4Characters() { //
        String email = "test";
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email mal formatado!", result.getMessage());
    }

    // 04
    @Test
    void shouldAllowEmailWith255Characters() { //
        String email = "a".repeat(255);
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email mal formatado!", result.getMessage());
    }

    // 05
    @Test
    void shouldNotAllowEmailWith3Characters() { //
        String email = "tes";
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email precisa ter no mínimo 4 caracteres!", result.getMessage());
    }

    // 06
    // Email passa, mas para no mal formatado
    @Test
    void shouldAllowEmailWith254Characters() {
        String email = "a".repeat(254);
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email mal formatado!", result.getMessage());
    }

    //07
    @Test
    void shouldNotAllowEmailInWrongFormat() {
        String email =  "vitormanzano.com";
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email mal formatado!", result.getMessage());
    }

    //08
    @Test
    void shouldNotAllowEmailInBarelyWrongFormat() {
        String email =  "vitormanzanogmail.com";
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email mal formatado!", result.getMessage());
    }

    //09
    @Test
    void shouldAllowEmailWellFormated() {
        String email =  "vitormanzano@gmail.com";
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

    //10
    @Test
    void shouldNotAllowEmailEmptyOrNull() {
        String email =  "";
        ValidationResult result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email vazio!", result.getMessage());

        email = null;
        result = EmailValidator.validate(email);

        assertEquals(false, result.isValid());
        assertEquals("Email vazio!", result.getMessage());
    }
}
