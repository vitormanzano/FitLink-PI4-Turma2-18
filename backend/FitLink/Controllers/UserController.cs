using System.Threading.Tasks;
using FitLink.Dtos.User;
using FitLink.Exceptions;
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
                    UserAlreadyExist => Conflict(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }
    }
}
