package test;

import org.junit.jupiter.api.Test;
import validacoes.CpfValidator;
import validacoes.EmailValidator;
import validacoes.ValidationResult;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CpfTest {
    //01
    @Test
    void shouldNotAllowCpfEmptyOrNull() {
        String cpf =  "";

        ValidationResult result = CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF vazio!", result.getMessage());

        cpf = null;
        result = CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF vazio!", result.getMessage());
    }

    //02
    @Test
    void shouldNotAllowCpfWithEqualsNumbers() {
        HashSet<String> cpfs = new HashSet<>();

        cpfs.add("00000000000");
        cpfs.add("11111111111");
        cpfs.add("22222222222");
        cpfs.add("33333333333");
        cpfs.add("44444444444");
        cpfs.add("55555555555");
        cpfs.add("66666666666");
        cpfs.add("77777777777");
        cpfs.add("88888888888");
        cpfs.add("99999999999");

        cpfs.forEach(cpf -> {
            ValidationResult result =  CpfValidator.validate(cpf);

            assertEquals(false, result.isValid());
            assertEquals("CPF apenas com números iguais!", result.getMessage());
        });
    }

    //03
    @Test
    void shouldNotAllowCpfWith10Characters() {
        String cpf = "1235647291";

        ValidationResult result =  CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF deve ter 11 caracteres! Digite apenas números!", CpfValidator.validate(cpf).getMessage());
    }

    //04
    @Test
    void shouldAllowCpfWith11Characters() {
        String cpf = "12736592051";

        ValidationResult result =  CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF inválido!", result.getMessage()); // Não passa, pois é um cpf inválido, mas passa pelo tamanho
    }

    //05
    @Test
    void shouldAllowCpfWith12Characters() {
        String cpf = "127365920512";

        ValidationResult result =  CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF deve ter 11 caracteres! Digite apenas números!", CpfValidator.validate(cpf).getMessage());
    }

    //06
    @Test
    void shouldNotAllowCpfWithFirstDigitWrong() {
        String cpf = "52998224795";

        ValidationResult result = CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF inválido!", result.getMessage());
    }

    //07
    @Test
    void shouldNotAllowCpfWithSecondDigitWrong() {
        String cpf = "52998224729";

        ValidationResult result = CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF inválido!", result.getMessage());
    }

    //08
    @Test
    void shouldNotAllowCpfWithBothDigitsWrong() {
        String cpf = "52998224799";

        ValidationResult result = CpfValidator.validate(cpf);
        assertEquals(false, result.isValid());
        assertEquals("CPF inválido!", result.getMessage());
    }

    //09
    @Test
    void shouldAllowValidCpf() {
        String cpf = "52998224725";

        ValidationResult result = CpfValidator.validate(cpf);
        assertEquals(true, result.isValid());
        assertEquals("", result.getMessage());
    }

}
