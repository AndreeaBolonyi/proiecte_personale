namespace PersonalityTypeApplication.Migrations
{
    using Microsoft.AspNet.Identity;
    using Microsoft.AspNet.Identity.EntityFramework;
    using PersonalityTypeApplication.Models;
    using System.Data.Entity.Migrations;

    internal sealed class Configuration : DbMigrationsConfiguration<ApplicationDbContext>
    {
        public Configuration()
        {
            AutomaticMigrationsEnabled = false;
            ContextKey = "PersonalityTypeApplication.Models.ApplicationDbContext";
        }

        protected override void Seed(ApplicationDbContext context)
        {
            var userManager = new UserManager<ApplicationUser>(new UserStore<ApplicationUser>(context));

            ApplicationUser user = new ApplicationUser()
            {
                UserName = "admin@yahoo.com",
                Email = "admin@yahoo.com",
                EmailConfirmed = true
            };
            IdentityResult userResult = userManager.Create(user, "12345678");
        }
    }
}
