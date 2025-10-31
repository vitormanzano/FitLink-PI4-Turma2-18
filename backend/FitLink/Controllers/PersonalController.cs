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
                    UserAlreadyExist => Conflict(ex.Message),
                    _ => BadRequest(ex.Message)
                };
            }
        }
    }
}
