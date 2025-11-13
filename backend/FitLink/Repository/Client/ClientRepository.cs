using System.Linq.Expressions;
using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.Client
{
    public class ClientRepository : BaseRepository<ClientModel>, IClientRepository
    {
        public ClientRepository(IMongoDatabase database) : base(database, "clients") // passa pro BaseRepository
        {
            
        }

        public async Task<ClientModel> GetClientByEmailAsync(string email)
        {
            var filter = Builders<ClientModel>.Filter.Eq(u => u.Email, email);
            return await _collection.Find(filter).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<ClientModel>> GetClientsByCity(string city)
        {
            var filter = Builders<ClientModel>.Filter.Eq(p => p.City, city);
            return await _collection.Find(filter)
                .ToListAsync();
        }

        public async Task LinkClientToPersonal(Expression<Func<ClientModel, bool>> filterExpression, UpdateDefinition<ClientModel> update)
        {
            await _collection.UpdateOneAsync(filterExpression, update);
        }

        public async Task CloseLinkWithPersonal(Expression<Func<ClientModel, bool>> filterExpression, UpdateDefinition<ClientModel> update)
        {
            await _collection.UpdateOneAsync(filterExpression, update);
        }

    }
}
