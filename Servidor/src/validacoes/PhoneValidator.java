package validacoes;

public class PhoneValidator {
    public static ValidationResult validate(String phone) {
        if (!StringValidator.validateIfLengthEquals(phone, 11))
            return new ValidationResult(false, "Telefone precisa ter 11 caracteres!");
        return new ValidationResult(true, "");
    }

}
