using System.Security.Authentication;
using System.Threading.Tasks;
using FitLink.Dtos.User;
using FitLink.Exceptions.User;
using FitLink.Services.User;
using Microsoft.AspNetCore.Mvc;

namespace FitLink.Controllers
{
    [ApiController]
    [Route("[controller]")] // O nome da rota vai começar com o nome do controller | User
    public class UserController : Controller
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterUserDto user)
        {
            try
            {
                await _userService.Register(user);
                return Ok("Usuário inserido com sucesso!");
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserAlreadyExistException => Conflict(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginUserDto user)
        {
            try
            {
                var userInformations = await _userService.Login(user);
                return Ok(userInformations);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    InvalidCredentialException => Unauthorized(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpGet("getById/{id}")]
        public async Task<IActionResult> GetUserById([FromRoute] string id)
        {
            try
            {
                var userResponse = await _userService.GetUserById(id);
                return Ok(userResponse);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpGet("GetByCity/{city}")]
        public async Task<IActionResult> GetUsersByCity([FromRoute] string city)
        {
            try
            {
                var usersInCity = await _userService.GetUsersByCity(city);
                return Ok(usersInCity);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPatch("update/{id}")]
        public async Task<IActionResult> Update([FromRoute] string id, [FromBody] UpdateUserDto updateUserDto)
        {
            try
            {
                var updatedUser = await _userService.Update(id, updateUserDto);
                return Ok(updatedUser);
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }

        [HttpDelete("delete/{id}")]
        public async Task<IActionResult> Delete([FromRoute] string id)
        {
            try
            {
                await _userService.Delete(id);
                return Ok();
            }
            catch (Exception ex)
            {
                return ex switch
                {
                    UserNotFoundException => NotFound(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }
    }
}
