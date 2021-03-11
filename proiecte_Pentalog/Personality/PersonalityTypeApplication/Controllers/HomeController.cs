using PersonalityTypeApplication.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace PersonalityTypeApplication.Controllers
{
    public class HomeController : Controller
    {
        private ApplicationDbContext _database = new ApplicationDbContext();

        public ActionResult Index()
        {

            return View(_database.Personalities.ToList());
        }
       


        public ActionResult About() 
        {   
                return View();
        }
    }

    //public ActionResult Contact
    //{
    //    get;
    //    {
    //        ViewBag.Message = "Your contact page.";

    //        return View();
    //    }
    //}
}
