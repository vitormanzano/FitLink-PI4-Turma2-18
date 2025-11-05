using System.Linq.Expressions;
using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.User
{
    public class UserRepository : BaseRepository<UserModel>, IUserRepository
    {
        public UserRepository(IMongoDatabase database) : base(database, "users") // passa pro BaseRepository
        {
            
        }

        public async Task<UserModel> GetUserByEmailAsync(string email)
        {
            var filter = Builders<UserModel>.Filter.Eq(u => u.Email, email);
            return await _collection.Find(filter).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<UserModel>> GetUsersByCity(string city)
        {
            var limitToReturn = 5;

            var filter = Builders<UserModel>.Filter.Eq(p => p.City, city);
            return await _collection.Find(filter)
                .Limit(limitToReturn)
                .ToListAsync();
        }

        public async Task LinkUserToPersonal(Expression<Func<UserModel, bool>> filterExpression, UpdateDefinition<UserModel> update)
        {
            await _collection.UpdateOneAsync(filterExpression, update);
        }
    }
}
