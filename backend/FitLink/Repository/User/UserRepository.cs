using FitLink.Models;
using MongoDB.Bson.Serialization;
using MongoDB.Driver;

namespace FitLink.Repository.User
{
    public class UserRepository : IUserRepository
    {
        private readonly IMongoCollection<UserModel> _usersCollection;

        public UserRepository(IMongoDatabase database)
        {
            MapClasses();
            _usersCollection = database.GetCollection<UserModel>("users"); // Nome da coleção no MongoDB
        }

        public async Task InsertDocumentAsync(UserModel user)
        {
            await _usersCollection.InsertOneAsync(user);
        }
        private static void MapClasses()
        {
            if (!BsonClassMap.IsClassMapRegistered(typeof(UserModel)))
            {
                BsonClassMap.TryRegisterClassMap<UserModel>(cm =>
                {
                    cm.AutoMap();
                    cm.SetIgnoreExtraElements(true);
                });
            }
        }

    }
}
