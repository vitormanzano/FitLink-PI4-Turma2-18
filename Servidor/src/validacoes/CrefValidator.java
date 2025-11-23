package validacoes;

public class CrefValidator {

    public static ValidationResult validate(String cref) {

        // 1. Campo vazio
        if (StringValidator.validateIfEmptyOrNull(cref))
            return new ValidationResult(false, "CREF vazio!");

        // 2. Tamanho mínimo
        if (StringValidator.validateIfLessThan(cref, 3))
            return new ValidationResult(false, "CREF muito curto!");

        // 3. Tamanho máximo
        if (StringValidator.validateIfGreaterThan(cref, 20))
            return new ValidationResult(false, "CREF muito longo!");

        // 4. Verificar caracteres permitidos
        // Permitido: A-Z 0-9 - /
        if (!cref.matches("[A-Za-z0-9\\-\\/]+"))
            return new ValidationResult(false, "CREF contém caracteres inválidos!");

        // 5. Deve conter pelo menos um número
        if (!cref.matches(".*\\d.*"))
            return new ValidationResult(false, "CREF deve conter ao menos um número!");

        return new ValidationResult(true, "");
    }
}
