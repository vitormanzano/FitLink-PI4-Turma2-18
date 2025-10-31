namespace FitLink.Dtos.Personal
{
    public record RegisterPersonalDto(
        string Name,
        string Email,
        string Password,
        string Phone,
        string Cref,
        string Cpf
    );

}
