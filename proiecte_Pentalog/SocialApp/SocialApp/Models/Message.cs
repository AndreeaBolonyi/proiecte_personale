using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SocialApp.Models
{
    public class Message
    { 
        public int Id { get; set; }

        [Display (Name ="Message title")]
        public string Title { get; set; }

        [Required]
        [Display(Name = "Message content")]
        public string Content { get; set; }

        [Display(Name = "Creation date")]
        public DateTime CreationDate { get; set; }

        public string ApplicationUserId { get; set; }
        
        public virtual ApplicationUser ApplicationUser { get; set; }

        [NotMapped]
        public bool IsEditableByUser { get; set; }
    }
}