namespace FitLink.Dtos.Personal
{
    public record ResponsePersonalDto(
        Guid Id,
        string Name,
        string Phone,
        string City);
}
