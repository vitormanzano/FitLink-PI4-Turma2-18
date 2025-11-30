package Train;

import validacoes.ValidationResult;
import java.util.List;
import Train.SetValidator

public class ExerciseValidator {
    public static ValidationResult validate (String nome, String instrucoes, List<ResponseSetDto> sets) {

        if (validateIfEmptyOrNull(nome)) return new ValidationResult(false, "Nome inválido");

        if (validateIfEmptyOrNull(instrucoes)) return new ValidationResult(false, "Instruções inválidas");

        if (sets.lenght == 0) return new ValidationResult(false, "Quantidade de sets inválida");

        //chama o validador de set pra cada set recebido no exercício
        for (ResponseSetDto s : sets) {
            ValidationResult result = SetValidator.validate(s.number, s.numberOfRepetitions, s.weight);
        }

        if (!result.isValid()) {
            return new ValidationResult(false, "Erro no set (número" + s.number + "): " + result.getMessage());
        }
    }
    return new ValidationResult(true,"");
}