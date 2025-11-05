using System.Security.Authentication;
using FitLink.Dtos.User;
using FitLink.Exceptions.User;
using FitLink.Models;
using FitLink.PasswordHasher;
using FitLink.Repository.User;
using MongoDB.Driver;

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
                throw new UserAlreadyExistException();

            var hashPassword = _passwordHasher.Hash(registerUserDto.Password);

            var user = new UserModel(registerUserDto.Name, registerUserDto.Email, hashPassword, registerUserDto.Phone, registerUserDto.City);
            await _userRepository.InsertDocumentAsync(user);
        }

        public async Task<UserResponseDto> Login(LoginUserDto loginUserDto)
        {
            var user = await _userRepository.GetUserByEmailAsync(loginUserDto.Email);
            if (user == null)
                throw new UserNotFoundException();

            var isPasswordValid = _passwordHasher.Verify(loginUserDto.Password, user.HashedPassword);

            if (!isPasswordValid)
                throw new InvalidCredentialException("Credenciais inválidas!");

            return new UserResponseDto(
                user.Id, 
                user.Name, 
                user.Email, 
                user.Phone, 
                user.City);
        }

        public async Task<UserResponseDto> GetUserById(string id)
        {
            var user = await _userRepository.GetDocumentByIdAsync(id);

            if (user == null)
                throw new UserNotFoundException();

            var userResponseDto = new UserResponseDto(
                user.Id,
                user.Name,
                user.Email,
                user.Phone,
                user.City
            );

            return userResponseDto;
        }

        public async Task<IEnumerable<UserResponseDto>> GetUsersByCity(string city)
        {
            var users = await _userRepository.GetUsersByCity(city);

            if (users.Count() == 0)
                throw new UserNotFoundException();

            var usersResponseDto = users.Select(user => new UserResponseDto(
                user.Id,
                user.Name,
                user.Email,
                user.Phone,
                user.City
            ));

            return usersResponseDto;
        }

        public async Task<UserResponseDto> Update(string id, UpdateUserDto updateUserDto)
        {
            var user = await _userRepository.GetDocumentByIdAsync(id);

            if (user == null)
                throw new UserNotFoundException();

            await _userRepository.UpdateDocumentAsync(
                u => u.Id.ToString() == (id),
                Builders<UserModel>.Update
                    .Set(u => u.Name, updateUserDto.Name)
                    .Set(u => u.Email, updateUserDto.Email)
                    .Set(u => u.HashedPassword, _passwordHasher.Hash(updateUserDto.Password))
                    .Set(u => u.Phone, updateUserDto.Phone)
                    .Set(u => u.City, updateUserDto.City)
            );

            var userResponse = new UserResponseDto(
                user.Id,
                updateUserDto.Name,
                updateUserDto.Email,
                updateUserDto.Phone,
                updateUserDto.City
            );

            return userResponse;
        }

        public async Task Delete(string id)
        {
            var user = _userRepository.GetDocumentByIdAsync(id);

            if (user == null)
                throw new UserNotFoundException();

            await _userRepository.DeleteDocumentAsync(p => p.Id.ToString() == id);
        }
    }
}
