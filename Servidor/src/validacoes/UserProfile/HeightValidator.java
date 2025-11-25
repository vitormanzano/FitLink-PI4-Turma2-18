package validacoes.UserProfile;

import validacoes.StringValidator;
import validacoes.ValidationResult;

public class HeightValidator {

    // valores típicos: 50 cm (bebe) até 300 cm (extremos)
    private static final int MIN_ALTURA = 50;
    private static final int MAX_ALTURA = 300;

    public static ValidationResult validate(String alturaStr) {
        if (StringValidator.validateIfEmptyOrNull(alturaStr))
            return new ValidationResult(false, "Altura vazia!");

        // tenta converter para número
        int altura;
        try {
            altura = Integer.parseInt(alturaStr);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Altura deve ser um número inteiro!");
        }

        if (altura < MIN_ALTURA)
            return new ValidationResult(false, "Altura muito baixa!");
        if (altura > MAX_ALTURA)
            return new ValidationResult(false, "Altura muito alta!");

        return new ValidationResult(true, "");
    }
}
