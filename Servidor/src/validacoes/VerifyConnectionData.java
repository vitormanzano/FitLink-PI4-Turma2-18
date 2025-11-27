package validacoes;

public class VerifyConnectionData {
    public static ValidationResult validate (String clientID, String personalID){
        ValidationResult result = VerifyIfIdIsNull.validate(clientID);

        if (!result.isValid())
            return new ValidationResult(false, "Id do cliente ausente!");

        result = VerifyIfIdIsNull.validate(personalID);

        if (!result.isValid())
            return new ValidationResult(false, "Id do personal ausente!");

        return new ValidationResult(true, "");
    }
}
