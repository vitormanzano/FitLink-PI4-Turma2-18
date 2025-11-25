package test.interclasse;

import org.junit.jupiter.api.Test;
import validacoes.SignUpClientValidator;
import validacoes.ValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignUpClientTest {
    @Test
    void shouldAllowIfEverythingIsValid() {
        ValidationResult result = SignUpClientValidator.
                validate("Vitor",
                         "vitor@gmail.com",
                        "vitor123",
                        "19983242758",
                        "São Paulo");

        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

    @Test
    void shouldNotAllowIfNameIsInvalid() {
        ValidationResult result = SignUpClientValidator.
                validate("vi",
                        "vitor@gmail.com",
                        "vitor123",
                        "19983242758",
                        "São Paulo");

        assertEquals(false, result.isValid());
        assertEquals("Nome Precisa ter no mínimo 3 caracteres!", result.getMessage());
    }

    @Test
    void shouldNotAllowIfEmailIsInvalid() {
        ValidationResult result = SignUpClientValidator.
                validate("vitor",
                        "vitor.com",
                        "vitor123",
                        "19983242758",
                        "São Paulo");

        assertEquals(false, result.isValid());
        assertEquals("Email mal formatado!", result.getMessage());
    }

    @Test
    void shouldNotAllowIfPasswordIsInvalid() {
        ValidationResult result = SignUpClientValidator.
                validate("vitor",
                        "vitor@gmail.com",
                        "vi",
                        "19983242758",
                        "São Paulo");

        assertEquals(false, result.isValid());
        assertEquals("Senha Precisa ter no mínimo 3 caracteres!", result.getMessage());
    }

    @Test
    void shouldNotAllowIfPhoneIsInvalid() {
        ValidationResult result = SignUpClientValidator.
                validate("vitor",
                        "vitor@gmail.com",
                        "vitor123",
                        "1998324275",
                        "São Paulo");

        assertEquals(false, result.isValid());
        assertEquals("Telefone precisa ter 11 caracteres! Digite apenas números!", result.getMessage());
    }

    @Test
    void shouldNotAllowIfCityIsInvalid() {
        ValidationResult result = SignUpClientValidator.
                validate("vitor",
                        "vitor@gmail.com",
                        "vitor123",
                        "19983242758",
                        "Sa");

        assertEquals(false, result.isValid());
        assertEquals("Cidade Precisa ter no mínimo 3 caracteres!", result.getMessage());
    }
}
