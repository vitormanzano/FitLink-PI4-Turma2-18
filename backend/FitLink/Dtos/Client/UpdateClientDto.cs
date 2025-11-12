namespace FitLink.Dtos.Client
{
    public record UpdateClientDto(
        string Name,
        string Email,
        string Password,
        string Phone,
        string City);
    
}
