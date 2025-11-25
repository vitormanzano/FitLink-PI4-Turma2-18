package validacoes.UserProfile;

import validacoes.ValidationResult;

public class GoalDetailsValidator {

    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 500;

    public static ValidationResult validate(String goalDetails) {

        if (goalDetails == null || goalDetails.trim().isEmpty()) {
            return new ValidationResult(true, "");
        }

        String trimmed = goalDetails.trim();

        if (trimmed.length() < MIN_LENGTH)
            return new ValidationResult(false, "Detalhes muito curtos!");

        if (trimmed.length() > MAX_LENGTH)
            return new ValidationResult(false, "Detalhes muito longos! Máximo de 500 caracteres.");

        return new ValidationResult(true, "");
    }
}
