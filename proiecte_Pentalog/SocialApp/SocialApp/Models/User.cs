using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace SocialApp.Models
{
    public class User
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }

        public User(ApplicationUser applicationUser)
        {
            this.Id = applicationUser.Id;
            this.Name = applicationUser.FullName;
            this.Email = applicationUser.Email;
        }
    }
}