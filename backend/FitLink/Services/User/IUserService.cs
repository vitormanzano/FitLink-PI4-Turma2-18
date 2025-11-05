using FitLink.Dtos.User;

namespace FitLink.Services.User
{
    public interface IUserService
    {
        Task Register(RegisterUserDto registerUserDto);
        Task<UserResponseDto> Login(LoginUserDto loginUserDto);
        Task<UserResponseDto> GetUserById(string id);
        Task<IEnumerable<UserResponseDto>> GetUsersByCity(string city);
        Task<UserResponseDto> Update(string id, UpdateUserDto updateUserDto);
        Task Delete(string id);
    }
}
