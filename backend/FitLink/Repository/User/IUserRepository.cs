using FitLink.Models;

namespace FitLink.Repository.User
{
    public interface IUserRepository
    {
        Task InsertDocumentAsync(UserModel user);
        Task<UserModel> GetUserByEmailAsync(string email);
    }
}
