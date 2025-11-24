package test;

import org.junit.jupiter.api.Test;
import validacoes.CrefValidator;
import validacoes.ValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrefTest {
    // 01
    @Test
    void shouldNotAllowIfCrefIsEmpty() {
        String cref = "";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(false, result.isValid());
        assertEquals("CREF vazio!", result.getMessage());

        cref = null;
        result = CrefValidator.validate(cref);

        assertEquals(false, result.isValid());
        assertEquals("CREF vazio!", result.getMessage());
    }

    //02
    @Test
    void shouldNotAllowCrefWithLessThan3Characters() {
        String cref = "12";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(false, result.isValid());
        assertEquals("CREF muito curto!", result.getMessage());
    }

    //03
    @Test
    void shouldAllowCrefWith3Characters() {
        String cref = "123";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

    //04
    @Test
    void shouldAllowCrefWith19Characters() {
        String cref = "123456789qwertyuiop";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

    //05
    @Test
    void shouldAllowCrefWith20Characters() {
        String cref = "123456789qwertyuiop1";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

    //06
    @Test
    void shouldNotAllowCrefWithMoreThan20Characters() {
        String cref = "123456789qwertyuiop12";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(false, result.isValid());
        assertEquals("CREF muito longo!", result.getMessage());
    }

    //07
    @Test
    void shouldNotAllowCrefWithInvalidCharacters() {
        String cref = "123456789q@#pa";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(false, result.isValid());
        assertEquals("CREF contém caracteres inválidos!", result.getMessage());
    }

    //08
    @Test
    void shouldAllowCrefWithValidCharacters() {
        String cref = "123456789qwertyuiop";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

    @Test
    void shouldNotAllowCrefWithoutAnyNumber() {
        String cref = "qwertyuiop";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(false, result.isValid());
        assertEquals("CREF deve conter ao menos um número!", result.getMessage());
    }

    @Test
    void shoudAllowCrefWithNumber() {
        String cref = "qwertyuiop1";

        ValidationResult result = CrefValidator.validate(cref);
        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }
}

