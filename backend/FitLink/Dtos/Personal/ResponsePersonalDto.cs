namespace FitLink.Dtos.Personal
{
    public record ResponsePersonalDto(
        Guid Id,
        string Name,
        string Email,
        string Phone,
        string City,
        string AboutMe,
        string Specialization,
        string Experience);
}
