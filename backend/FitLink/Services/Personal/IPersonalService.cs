using FitLink.Dtos.Personal;

namespace FitLink.Services.Personal
{
    public interface IPersonalService
    {
        Task Register(RegisterPersonalDto registerPersonalDto);
        Task<IEnumerable<ResponsePersonalDto>> GetPersonalTrainersByCity(string city);
        Task<ResponsePersonalDto> Login(LoginPersonalDto loginPersonalDto);
        Task<ResponsePersonalDto> GetPersonalById(string personalId);
        Task<ResponsePersonalDto> Update(string personalId, UpdatePersonalDto updatePersonalDto);
        Task Delete(string personalId);
    }
}
