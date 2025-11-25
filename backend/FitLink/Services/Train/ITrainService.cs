using FitLink.Dtos.Train;

namespace FitLink.Services.Train
{
    public interface ITrainService
    {
        Task<ResponseTrainDto> Register(RegisterTrainDto registerTrainDto);
        Task<ResponseTrainDto> GetTrainById(string trainId);
        Task<List<ResponseTrainDto>> GetTrainsByClientId(string clientId);
        Task<List<ResponseTrainDto>> GetTrainsByPersonalId(string personalId);
        Task<ResponseTrainDto> UpdateTrainById(string trainId, UpdateTrainDto updateTrainDto);
        Task DeleteTrainById(string trainId);
    }
}
