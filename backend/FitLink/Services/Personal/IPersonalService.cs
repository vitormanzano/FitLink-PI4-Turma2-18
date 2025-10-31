using FitLink.Dtos.Personal;

namespace FitLink.Services.Personal
{
    public interface IPersonalService
    {
        Task Register(RegisterPersonalDto registerPersonalDto);
        Task<IEnumerable<ResponsePersonalDto>> GetPersonalTrainersByCity(string city);
    }
}
