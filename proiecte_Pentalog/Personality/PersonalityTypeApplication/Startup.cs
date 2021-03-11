using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(PersonalityTypeApplication.Startup))]
namespace PersonalityTypeApplication
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
