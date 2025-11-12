namespace FitLink.Dtos.Train
{
    public record ResponseTrainDto(
        Guid Id,
        string Name,
        string ClientId,
        string PersonalId,
        List<ResponseExerciseDto> Exercises);
}
