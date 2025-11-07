using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.Train
{
    public class TrainRepository : BaseRepository<TrainModel>, ITrainRepository
    {
        public TrainRepository(IMongoDatabase database) : base(database, "trains")
        {
        }
    }
}
