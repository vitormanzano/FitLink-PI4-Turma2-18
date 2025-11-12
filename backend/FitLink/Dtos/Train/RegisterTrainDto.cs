using FitLink.Models;

namespace FitLink.Dtos.Train
{
    public record RegisterTrainDto(
        string Name,
        string ClientId,
        string PersonalId,
        List<RegisterExerciseDto> Exercises);
}
