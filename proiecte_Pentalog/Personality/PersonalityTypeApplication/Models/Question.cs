using System.ComponentModel;
using System.ComponentModel.DataAnnotations;

namespace PersonalityTypeApplication.Models
{
    public class Question
    {
        public int Id { get; set; }

        [Display(Name = "Question text")]
        public string Text { get; set; }

        [Display(Name = "Personality type")]
        public int PersonalityId { get; set; }

        [Display(Name = "Personality type")]
        public virtual Personality Personality { get; set; }
    }
}