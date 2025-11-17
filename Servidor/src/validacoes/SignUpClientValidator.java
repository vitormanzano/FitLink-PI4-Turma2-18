package validacoes;

public class SignUpClientValidator {
    public static ValidationResult validate(String name,
                           String email,
                           String password,
                           String Phone,
                           String city) {

        ValidationResult result = validateName(name);
        if (!result.isValid())
            return result;

        result = EmailValidator.validate(email);
        if (!result.isValid())
            return result;

        result = validatePassword(password);
        if (!result.isValid())
            return result;

        result = PhoneValidator.validate(Phone);
        if (!result.isValid())
            return result;

        result = validateCity(city);
        if (!result.isValid())
            return result;

        return new ValidationResult(true, "");
    }

    private static ValidationResult validateName(String name) {
        if (name == null || name.isEmpty())
            return new ValidationResult(false, "Nome ausente!");

        if (name.length() < 3)
            return new ValidationResult(false, "Nome Precisa ter no mínimo 3 caracteres!");
        if (name.length() > 255)
            return new ValidationResult(false, "Nome Precisa ter menos de 255 caracteres!");

        return new ValidationResult(true, "" );
    }

    private static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty())
            return new ValidationResult(false, "Senha ausente!");

        if (password.length() < 3)
            return new ValidationResult(false, "Senha Precisa ter no mínimo 3 caracteres!");
        if (password.length() > 255)
            return new ValidationResult(false, "Senha Precisa ter menos de 255 caracteres!");

        return new ValidationResult(true, "" );
    }

    private static ValidationResult validateCity(String city) {
        if (city == null || city.isEmpty())
            return new ValidationResult(false, "Cidade ausente!");
        if (StringValidator.validateIfLessThan(city, 2))
            return new ValidationResult(false, "Cidade deve ter mis de 2 caracteres");
        if (StringValidator.validateIfGreaterThan(city, 255))
            return new ValidationResult(false, "Cidade deve ter menos de 255 caracteres!");

        return new ValidationResult(true, "");
    }
}
