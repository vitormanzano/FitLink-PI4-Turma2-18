package validacoes;

public class VerifyIfIdIsNull {
    public static ValidationResult validate(String id) {
        if (!StringValidator.validateIfEmptyOrNull) 
            return new ValidationResult(false, "Id vazio!");
        
        return new ValidationResult(true, "");
    }
}
