package validacoes.UserProfile;

import validacoes.StringValidator;
import validacoes.ValidationResult;

public class NameValidator {

    private static ValidationResult validate(String name) {
        if (name == null || name.isEmpty())
            return new ValidationResult(false, "Nome ausente!");

        if (StringValidator.validateIfLessThan(name, 3))
            return new ValidationResult(false, "Nome Precisa ter no mínimo 3 caracteres!");
        if (StringValidator.validateIfGreaterThan(name, 255))
            return new ValidationResult(false, "Nome Precisa ter menos de 255 caracteres!");

        return new ValidationResult(true, "");
    }
}