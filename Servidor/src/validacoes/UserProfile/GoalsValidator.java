package validacoes.UserProfile;

import validacoes.StringValidator;
import validacoes.ValidationResult;

import java.util.Arrays;
import java.util.List;

public class GoalsValidator {

    private static final List<String> VALID_GOALS = Arrays.asList(
            "Hipertrofia",
            "Emagrecimento",
            "Condicionamento",
            "Resistência",
            "Mobilidade",
            "Outro"
    );

    public static ValidationResult validate(String objetivo) {

        if (StringValidator.validateIfEmptyOrNull(objetivo))
            return new ValidationResult(false, "Objetivo não pode estar vazio!");

        // remove espaços extras
        String trimmed = objetivo.trim();

        if (!VALID_GOALS.contains(trimmed)) {
            return new ValidationResult(false, "Objetivo inválido!");
        }

        return new ValidationResult(true, "");
    }
}
