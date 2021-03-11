using System.ComponentModel.DataAnnotations;

namespace PersonalityTypeApplication.Models
{
    public class Recomandation
    {
        public int Id { get; set; }

        [Required]
        [MinLength(1, ErrorMessage = "The name must be add")]
        public string Name { get; set; }

        [MinLength(10, ErrorMessage = "The description must be add"), MaxLength(1000, ErrorMessage = "The description must be 250 or less")]
        public string Description { get; set; }

        public int PersonalityId { get; set; }

        [Required]
        public RECOMANDATION_TYPE RecomandationType { get; set; }

        public string Link { get; set; }

        public virtual Personality Personality { get; set; }
    }

    public enum RECOMANDATION_TYPE {
        Movie = 0,
        Series = 1,
        TravelPlace = 2
    }
}