package Train;

import validacoes.VerifyifIdIsNull;

public class TrainingRegistrationvalidator {
        public static ValidationResult validate(String nome, int clienteId, int, personalId, List<ExerciseDto> exercicios {

                if (validateIfEmptyOrNull(nome)) return new ValidationResult(false, "Nome inválido");
                ValidationResult Idresult;

                idResult = VerifyIfIdIsNull.validate(clienteId);
                if (!idResult.isValid())
                        return new ValidationResult(false, "Id de usuário inválido");

                idResult = VerifyIfIdIsNull.validate(personalId);
                if (!idResult.isValid())
                        return new ValidationResult(false, "Id de personal inválido");

                if (exercicios == null || exercicios.isEmpty())
                        return new ValidationResult(false, "Nenhum exercício informado");

                for (ExerciseDto ex: exercicios){
                        ValidationResult result = ExerciseValidator.validate(ex.name, ex.instructions, ex.sets);
                };

                if (!result.isValid()) return new ValidationResult(false, "Erro no exercício \"" + ex.nome + "\": " + result.getMessage());

                return new ValidationResult(true,"");
        }
}