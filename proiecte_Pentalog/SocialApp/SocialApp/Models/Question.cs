using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;

namespace SocialApp.Models
{
    public class Question
    {
        public int Id { get; set; }

        [Display(Name = "Question text")]
        public string Text { get; set; }

        [Display(Name = "Answer 1")]
        public string Answer1 { get; set; }

        [Display(Name = "Answer 2")]
        public string Answer2 { get; set; }

        [Display(Name = "Answer 3")]
        public string Answer3 { get; set; }

        public string ApplicationUserId { get; set; }

        public virtual ApplicationUser ApplicationUser { get; set; }
    }
}