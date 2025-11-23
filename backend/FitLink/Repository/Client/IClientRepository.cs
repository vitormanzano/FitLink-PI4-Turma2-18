using System.Linq.Expressions;
using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.Client
{
    public interface IClientRepository : IBaseRepository<ClientModel>
    {
        Task<ClientModel> GetClientByEmailAsync(string email);
        Task<IEnumerable<ClientModel>> GetClientsByCity(string city);
        Task<IEnumerable<ClientModel>> GetClientsByPersonalId(string personalId);
        Task LinkClientToPersonal(Expression<Func<ClientModel, bool>> filterExpression, UpdateDefinition<ClientModel> update);
        Task CloseLinkWithPersonal(Expression<Func<ClientModel, bool>> filterExpression, UpdateDefinition<ClientModel> update);
        Task AddInformations(Expression<Func<ClientModel, bool>> filterExpression, UpdateDefinition<ClientModel> update);
    }
}
