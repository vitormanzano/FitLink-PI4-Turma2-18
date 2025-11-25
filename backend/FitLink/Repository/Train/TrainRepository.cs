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

        public async Task<List<TrainModel>> GetTrainsByClientId(string clientId)
        {
            var filter = Builders<TrainModel>.Filter
                .Eq(t => t.ClientId, clientId);

            return await _collection.Find(filter)
                .ToListAsync();
        }

        public async Task<List<TrainModel>> GetTrainsByPersonalId(string personalId)
        {
            var filter = Builders<TrainModel>.Filter
                .Eq(t => t.PersonalId, personalId);
            return await _collection.Find(filter)
                .ToListAsync();
        }
    }
}
