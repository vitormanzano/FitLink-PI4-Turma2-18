namespace FitLink.Dtos.Train
{
    public record RegisterExerciseDto(
        string Name,
        string Instructions,
        List<RegisterSetDto> Sets);
}
