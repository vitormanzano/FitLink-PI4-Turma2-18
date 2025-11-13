using System.Security.Authentication;
using FitLink.Dtos.Client;
using FitLink.Exceptions.User;
using FitLink.Services.Client;
using Microsoft.AspNetCore.Mvc;

namespace FitLink.Controllers
{
    [ApiController]
    [Route("[controller]")] // O nome da rota vai começar com o nome do controller | User
    public class ClientController : Controller
    {
        private readonly IClientService _clientService;

        public ClientController(IClientService clientService)
        {
            _clientService = clientService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterClientDto client)
        {
            try
            {
                await _clientService.Register(client);
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
        public async Task<IActionResult> Login([FromBody] LoginClientDto client)
        {
            try
            {
                var clientInformations = await _clientService.Login(client);
                return Ok(clientInformations);
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
                var clientResponse = await _clientService.GetClientById(id);
                return Ok(clientResponse);
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
                var usersInCity = await _clientService.GetClientsByCity(city);
                return Ok(usersInCity);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPatch("update/{id}")]
        public async Task<IActionResult> Update([FromRoute] string id, [FromBody] UpdateClientDto updateClientDto)
        {
            try
            {
                var updatedClient = await _clientService.Update(id, updateClientDto);
                return Ok(updatedClient);
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
                await _clientService.Delete(id);
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

        [HttpPatch("linkToPersonal/{clientId}/{personalTrainerId}")]
        public async Task<IActionResult> LinkUserToPersonal([FromRoute] string clientId, [FromRoute] string personalTrainerId)
        {
            try
            {
                await _clientService.LinkClientToPersonal(clientId, personalTrainerId);
                return Ok("Usuário vinculado ao personal trainer com sucesso!");
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

        [HttpPatch("closeLinkWithPersonal/{clientId}")]
        public async Task<IActionResult> CloseLinkWithPersonal([FromRoute] string clientId)
        {
            try
            {
                await _clientService.CloseLinkWithPersonal(clientId);
                return Ok("Vínculo com o personal trainer encerrado com sucesso!");
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
