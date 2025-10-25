using MongoDB.Driver;

namespace FitLink.ServicesExtensions
{
    public static class MongoExtensions
    {
        public static IServiceCollection AddMongo(this IServiceCollection services, IConfiguration configuration)
        {
            var mongoConnectionString = configuration.GetConnectionString("MongoConnection");

            var clientSettings = MongoClientSettings.FromConnectionString(mongoConnectionString);
            var mongoClient = new MongoClient(clientSettings);
            services.AddSingleton<IMongoClient>(_ => mongoClient);

            services.AddSingleton(sp =>
            {
                var mongoClient = sp.GetService<IMongoClient>();
                var db = mongoClient!.GetDatabase("Cluster-FitLink");
                return db;
            });
            return services;
        }
    }
}
