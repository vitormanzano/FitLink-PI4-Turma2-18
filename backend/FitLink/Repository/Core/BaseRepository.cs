
using System.Linq.Expressions;
using MongoDB.Bson.Serialization;
using MongoDB.Driver;

namespace FitLink.Repository.Core
{
    public class BaseRepository<T> : IBaseRepository<T> where T : class
    {
        protected readonly IMongoCollection<T> _collection;

        public BaseRepository(IMongoDatabase database, string collectionName)
        {
            MapClasses();
            _collection = database.GetCollection<T>(collectionName);
        }

        public async Task InsertDocumentAsync(T model)
        {
            await _collection.InsertOneAsync(model);
        }

        public async Task<T> GetDocumentByIdAsync(string id)
        {
            var filter = Builders<T>.Filter.Eq("_id", id);
            var result = await _collection.Find(filter).FirstOrDefaultAsync();

            return result;
        }       
 
        public async Task UpdateDocumentAsync(Expression<Func<T, bool>> filterExpression, UpdateDefinition<T> update) // filterExpression : expressão lambda para filtrar o documento, funciona como um WHERE
        {
            await _collection.UpdateOneAsync(filterExpression, update);    
        }

        public async Task DeleteDocumentAsync(Expression<Func<T, bool>> filterExpression)
        {
            await _collection.DeleteOneAsync(filterExpression);
        }

        private static void MapClasses() // Como converter a classe T genérica para o formato BSON
        {
            if (!BsonClassMap.IsClassMapRegistered(typeof(T)))
            {
                BsonClassMap.TryRegisterClassMap<T>(cm =>
                {
                    cm.AutoMap();
                    cm.SetIgnoreExtraElements(true);
                });
            }
        }
    }
}
