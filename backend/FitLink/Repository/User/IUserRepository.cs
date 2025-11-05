using FitLink.Models;
using FitLink.Repository.Core;

namespace FitLink.Repository.User
{
    public interface IUserRepository : IBaseRepository<UserModel>
    {
        Task<UserModel> GetUserByEmailAsync(string email);
        Task<IEnumerable<UserModel>> GetUsersByCity(string city);
    }
}
