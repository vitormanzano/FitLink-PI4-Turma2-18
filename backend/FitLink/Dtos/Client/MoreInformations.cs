using FitLink.Models;

namespace FitLink.Dtos.Client
{
    public record MoreInformations(
        string? AboutMe,
        string? Goals,
        MetricsModel? Metrics
    );
}
