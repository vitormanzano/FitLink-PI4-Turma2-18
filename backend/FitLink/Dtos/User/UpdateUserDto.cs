namespace FitLink.Dtos.User
{
    public record UpdateUserDto(
        string Name,
        string Email,
        string Password,
        string Phone,
        string City);
    
}
