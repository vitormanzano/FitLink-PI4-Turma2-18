package validacoes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);

    public static ValidationResult validate(String email) {
        if (StringValidator.validateIfEmptyOrNull(email))
            return new ValidationResult(false, "Email vazio!");

        if (StringValidator.validateIfLessThan(email, 4))
            return new ValidationResult(false, "Email precisa ter no mínimo 4 caracteres!");
        if (StringValidator.validateIfGreaterThan(email, 255))
            return new ValidationResult(false, "Email deve ter menos de 255 caracteres!");

        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches())
            return new ValidationResult(false, "Email mal formatado!");

        return new ValidationResult(true, "");
    }
}
