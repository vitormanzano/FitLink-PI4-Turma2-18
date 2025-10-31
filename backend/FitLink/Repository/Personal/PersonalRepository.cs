using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.Personal
{
    public class PersonalRepository : BaseRepository<PersonalTrainer>, IPersonalRepository
    {
        public PersonalRepository(IMongoDatabase database) : base(database, "personalTrainers")
        {
        }

        public async Task<PersonalTrainer> GetPersonalByEmailAsync(string email)
        {
            var filter = Builders<PersonalTrainer>.Filter.Eq(u => u.Email, email);
            return await _collection.Find(filter).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<PersonalTrainer>> GetPersonalTrainersByCity(string city)
        {
            var limitToReturn = 5;

            var filter = Builders<PersonalTrainer>.Filter.Eq(p => p.City, city);
            return await _collection.Find(filter)
                .Limit(limitToReturn)
                .ToListAsync();
        }
    }
}
