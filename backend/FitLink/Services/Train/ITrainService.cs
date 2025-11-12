using FitLink.Dtos.Train;
using FitLink.Models;

namespace FitLink.Services.Train
{
    public interface ITrainService
    {
        Task Register(RegisterTrainDto registerTrainDto);
        Task<ResponseTrainDto> GetTrainById(string trainId);
    }
}
