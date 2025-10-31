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
    }
}
