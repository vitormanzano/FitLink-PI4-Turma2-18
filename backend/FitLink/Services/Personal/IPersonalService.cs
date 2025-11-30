using FitLink.Dtos.Message;
using FitLink.Dtos.Personal;

namespace FitLink.Services.Personal
{
    public interface IPersonalService
    {
        Task Register(RegisterPersonalDto registerPersonalDto);
        Task<ResponsePersonalDto> Login(LoginPersonalDto loginPersonalDto);
        Task<IEnumerable<ResponsePersonalDto>> GetPersonalTrainersByCity(string city);
        Task<ResponsePersonalDto> GetPersonalById(string personalId);
        Task<ResponsePersonalDto> Update(string personalId, UpdatePersonalDto updatePersonalDto);
        Task Delete(string personalId);
        Task AddMoreInformations(string personalId, MoreInformationsPersonalDto moreInformationsPersonalDto);
    }
}
