using FitLink.Dtos.User;
using FitLink.Models;
using FitLink.PasswordHasher;
using FitLink.Repository.User;

namespace FitLink.Services.User
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;
        private readonly IPasswordHasher _passwordHasher;

        public UserService(IUserRepository userRepository, IPasswordHasher passwordHasher)
        {
            _userRepository = userRepository;
            _passwordHasher = passwordHasher;
        }

        public async Task Register(RegisterUserDto registerUserDto)
        {
            var hashPassword = _passwordHasher.Hash(registerUserDto.Password);

            var user = new UserModel(registerUserDto.Name, registerUserDto.Email, hashPassword,registerUserDto.IsPersonalTrainer);
            await _userRepository.InsertDocumentAsync(user);
        }
    }
}
