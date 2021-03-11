using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;
using PersonalityTypeApplication.Models;

namespace PersonalityTypeApplication.Controllers
{
    [Authorize(Users = "admin@yahoo.com")]
    public class PersonalitiesController : Controller
    {
        private ApplicationDbContext _database = new ApplicationDbContext();
        
        public ActionResult Index()
        {
            return View(_database.Personalities.ToList());
        }

        public ActionResult Details(int id)
        {
            Personality user = _database.Personalities.First(u => u.Id == id);
            return View(user);
        }

        [HttpGet]
        public ActionResult Create()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Create(Personality personality)
        {
            if (ModelState.IsValid)
            {
                _database.Personalities.Add(personality);
                _database.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(personality);
        }

        [HttpGet]
        public ActionResult Edit(int id)
        {
            Personality personality = _database.Personalities.First(u => u.Id == id);
            return View("Create", personality);
        }

        [HttpPost]
        public ActionResult Edit(Personality user)
        {
            Personality exisingUser = _database.Personalities.First(u => u.Id == user.Id);
            exisingUser.Name = user.Name;
            exisingUser.Description = user.Description;
            _database.SaveChanges();
            return RedirectToAction("Index");
        }

        [HttpGet]
        public ActionResult Delete(int id)
        {
            Personality user = _database.Personalities.First(u => u.Id == id);
            _database.Personalities.Remove(user);
            _database.SaveChanges();
            return RedirectToAction("Index");
        }
    }
}
