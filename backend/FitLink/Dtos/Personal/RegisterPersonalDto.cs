namespace FitLink.Dtos.Personal
{
    public record RegisterPersonalDto(
        string Name,
        string Email,
        string Password,
        string Phone,
        string City,
        string Cref,
        string Cpf
    );

}
