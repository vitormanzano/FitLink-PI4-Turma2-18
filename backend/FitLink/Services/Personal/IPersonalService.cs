using FitLink.Dtos.Personal;

namespace FitLink.Services.Personal
{
    public interface IPersonalService
    {
        Task Register(RegisterPersonalDto registerPersonalDto);
    }
}
