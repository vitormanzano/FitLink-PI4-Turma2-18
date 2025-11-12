namespace FitLink.Dtos.Train
{
    public record ResponseExerciseDto(
        string Name,
        string Instructions,
        List<ResponseSetDto> Sets);
}
