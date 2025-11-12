using FitLink.Dtos.Train;

namespace FitLink.Services.Train
{
    public interface ITrainService
    {
        Task<ResponseTrainDto> Register(RegisterTrainDto registerTrainDto);
        Task<ResponseTrainDto> GetTrainById(string trainId);
        Task<ResponseTrainDto> GetTrainByClientId(string clientId);
        Task DeleteTrainById(string trainId);
    }
}
