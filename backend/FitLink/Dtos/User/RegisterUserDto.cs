namespace FitLink.Dtos.User
{
    public record RegisterUserDto(
        string Name,
        string Email,
        string Password,
        string Phone
    );
}
