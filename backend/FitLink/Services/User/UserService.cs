using FitLink.Dtos.User;
using FitLink.Exceptions.User;
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
            var userAlreadyExists = await _userRepository.GetUserByEmailAsync(registerUserDto.Email);

            if (userAlreadyExists != null)
                throw new UserAlreadyExist();

            var hashPassword = _passwordHasher.Hash(registerUserDto.Password);

            var user = new UserModel(registerUserDto.Name, registerUserDto.Email, hashPassword, registerUserDto.Phone);
            await _userRepository.InsertDocumentAsync(user);
        }

        public async Task<UserResponseDto> Login(LoginUserDto loginUserDto)
        {
            var user = await _userRepository.GetUserByEmailAsync(loginUserDto.Email);
            if (user == null)
                throw new UserNotFound();

            var isPasswordValid = _passwordHasher.Verify(loginUserDto.Password, user.HashedPassword);

            if (!isPasswordValid)
                throw new InvalidCredentials();

            return new UserResponseDto(user.Id, user.Name, user.Email, user.Phone);
        }
    }
}
