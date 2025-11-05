using System.Linq.Expressions;
using FitLink.Models;
using FitLink.Repository.Core;
using MongoDB.Driver;

namespace FitLink.Repository.User
{
    public interface IUserRepository : IBaseRepository<UserModel>
    {
        Task<UserModel> GetUserByEmailAsync(string email);
        Task<IEnumerable<UserModel>> GetUsersByCity(string city);
        Task LinkUserToPersonal(Expression<Func<UserModel, bool>> filterExpression, UpdateDefinition<UserModel> update);
    }
}
