namespace FitLink.Dtos.Personal
{
    public record UpdatePersonalDto(
        string Name,
        string Email,
        string Password,
        string Phone,
        string City
    );

}
