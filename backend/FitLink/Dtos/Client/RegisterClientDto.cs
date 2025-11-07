namespace FitLink.Dtos.Client
{
    public record RegisterClientDto(
        string Name,
        string Email,
        string Password,
        string Phone,
        string City
    );
}
