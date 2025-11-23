package validacoes;

public class SignUpPersonalValidator {

    public static ValidationResult validate(
        String name,
        String email,
        String password,
        String phone,
        String city,
        String cpf,
        String cref
    ) {
        // 1) Reaproveita TODA validação de cliente
        ValidationResult result = SignUpClientValidator.validate(
            name,
            email,
            password,
            phone,
            city
        );
        if (!result.isValid())
            return result;

        // 2) Valida CPF
        result = CpfValidator.validate(cpf);
        if (!result.isValid())
            return result;

        // 3) Valida CREF
        result = CrefValidator.validate(cref);
        if (!result.isValid())
            return result;

        return new ValidationResult(true, "");
    }
}
