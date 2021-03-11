using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace PersonalityTypeApplication.Models
{
    public class Personality
    {
        public int Id { get; set; }

        [Required]
        [MinLength(3)]
        public string Name { get; set; }
        
        public string Description { get; set; }

        public virtual ICollection<Question> Questions { get; set; }
        public virtual ICollection<Recomandation> Recomandations { get; set; }
    }
}