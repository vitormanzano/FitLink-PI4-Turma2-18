using System.Security.Authentication;
using FitLink.Dtos.Personal;
using FitLink.Exceptions.User;
using FitLink.Services.Personal;
using Microsoft.AspNetCore.Mvc;

namespace FitLink.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class PersonalController : Controller
    {
        private readonly IPersonalService _personalService;

        public PersonalController(IPersonalService personalService)
        {
            _personalService = personalService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterPersonalDto personal)
        {
            try
            {
                await _personalService.Register(personal);
                return Ok("Personal trainer inserido com sucesso!");
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

        [HttpGet("city/{city}")]
        public async Task<IActionResult> GetPersonalTrainersByCity([FromRoute] string city)
        {
            try
            {
                var personals = await _personalService.GetPersonalTrainersByCity(city);
                return Ok(personals);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginPersonalDto personal)
        {
            try
            {
                var personalResponse = await _personalService.Login(personal);
                return Ok(personalResponse);
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

        [HttpGet("getById/{personalId}")]
        public async Task<IActionResult> GetPersonalById([FromRoute] string personalId)
        {
            try
            {
                var personalResponse = await _personalService.GetPersonalById(personalId);
                return Ok(personalResponse);
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
