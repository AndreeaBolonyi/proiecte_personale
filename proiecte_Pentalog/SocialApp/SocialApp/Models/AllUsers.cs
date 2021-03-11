using Microsoft.AspNet.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Microsoft.AspNet.Identity;
using Microsoft.Owin.Security;

namespace SocialApp.Models
{
    public class AllUsersViewModel
    {
        public IList<User> Followed  {get; set;}
        public IList<User> Recommendations { get; set; }
    }
}