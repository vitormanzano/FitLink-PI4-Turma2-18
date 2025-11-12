using FitLink.Models;
using FitLink.Repository.Core;

namespace FitLink.Repository.Train
{
    public interface ITrainRepository : IBaseRepository<TrainModel>
    {
        Task<TrainModel> GetTrainByClientId(string clientId);
        Task<List<TrainModel>> GetTrainsByPersonalId(string personalId);
    }
}
