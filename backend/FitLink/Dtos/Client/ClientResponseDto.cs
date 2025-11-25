using FitLink.Models;

namespace FitLink.Dtos.Client
{
    public record ClientResponseDto(
        Guid Id, 
        string Name, 
        string Email, 
        string Phone, 
        string City,
        string AboutMe,
        string Goals, 
        MetricsModel Metrics) { }

}
