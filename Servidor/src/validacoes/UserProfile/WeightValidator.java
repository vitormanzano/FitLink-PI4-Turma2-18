package validacoes.UserProfile;

import validacoes.StringValidator;
import validacoes.ValidationResult;

public class WeightValidator {
    // faixa realista, mas ampla
    private static final double MIN_PESO = 20.0;
    private static final double MAX_PESO = 500.0;

    public static ValidationResult validate(String pesoStr) {
        if (StringValidator.validateIfEmptyOrNull(pesoStr))
            return new ValidationResult(false, "Peso vazio!");

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Peso deve ser um número (ex: 72.5)!");
        }

        if (peso < MIN_PESO)
            return new ValidationResult(false, "Peso muito baixo!");
        if (peso > MAX_PESO)
            return new ValidationResult(false, "Peso muito alto!");

        return new ValidationResult(true, "");
    }

}
