package validacoes.UserProfile;

import validacoes.ValidationResult;

public class AboutMeValidator {

    private static final int MIN_LENGTH = 5;       // mínimo opcional
    private static final int MAX_LENGTH = 500;     // máximo comum para bio

    public static ValidationResult validate(String aboutMe) {

        // Campo não obrigatório: se estiver vazio, pode retornar como válido
        if (aboutMe == null || aboutMe.trim().isEmpty()) {
            return new ValidationResult(true, "");
        }

        // remove espaços laterais para validação
        String trimmed = aboutMe.trim();

        if (trimmed.length() < MIN_LENGTH)
            return new ValidationResult(false, "Descrição muito curta!");

        if (trimmed.length() > MAX_LENGTH)
            return new ValidationResult(false, "Descrição muito longa! Máximo de 500 caracteres.");

        return new ValidationResult(true, "");
    }
}
