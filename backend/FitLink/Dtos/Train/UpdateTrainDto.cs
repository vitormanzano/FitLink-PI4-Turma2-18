namespace FitLink.Dtos.Train
{
    public record UpdateTrainDto(
            string Name,
            List<RegisterExerciseDto> Exercises);
}
