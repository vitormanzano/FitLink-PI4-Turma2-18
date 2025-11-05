using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.Personal
{
    public class PersonalRepository : BaseRepository<PersonalTrainerModel>, IPersonalRepository
    {
        public PersonalRepository(IMongoDatabase database) : base(database, "personalTrainers")
        {
        }

        public async Task<PersonalTrainerModel> GetPersonalByEmailAsync(string email)
        {
            var filter = Builders<PersonalTrainerModel>.Filter.Eq(u => u.Email, email);
            return await _collection.Find(filter).FirstOrDefaultAsync();
        }


        public async Task<IEnumerable<PersonalTrainerModel>> GetPersonalTrainersByCity(string city)
        {
            var limitToReturn = 5;

            var filter = Builders<PersonalTrainerModel>.Filter.Eq(p => p.City, city);
            return await _collection.Find(filter)
                .Limit(limitToReturn)
                .ToListAsync();
        }

        public Task<PersonalTrainerModel> GetPersonalTrainerByCpf(string cpf)
        {
            var filter = Builders<PersonalTrainerModel>.Filter.Eq(p => p.Cpf, cpf);
            return _collection.Find(filter).FirstOrDefaultAsync();
        }

        public Task<PersonalTrainerModel> GetPersonalTrainerByCref(string cref)
        {
            var filter = Builders<PersonalTrainerModel>.Filter.Eq(p => p.Cref, cref);
            return _collection.Find(filter).FirstOrDefaultAsync();
        }
    }
}
