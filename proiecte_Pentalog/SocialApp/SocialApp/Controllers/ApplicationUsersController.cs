using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using SocialApp.Models;

namespace SocialApp.Controllers
{
    public class ApplicationUsersController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: ApplicationUsers
        public ActionResult Index()
        {
            AllUsersViewModel allUsers = new AllUsersViewModel();
            allUsers.Followed = new List<User>();
            allUsers.Recommendations = new List<User>();

            var loggedUser = db.Users.Where(user => user.UserName == HttpContext.User.Identity.Name).FirstOrDefault();
            foreach (var applicationUser in db.Users.ToList())
            {
                if (loggedUser.UserName != applicationUser.UserName
                    && loggedUser.FollowedUsers.Contains(applicationUser))
                {
                    allUsers.Followed.Add(new Models.User(applicationUser));
                }
            }

            foreach (var applicationUser in db.Users.ToList())
            {
                if (loggedUser.UserName != applicationUser.UserName
                    && !loggedUser.FollowedUsers.Contains(applicationUser))
                {
                    allUsers.Recommendations.Add(new Models.User(applicationUser));
                }
            }

            return View(allUsers);
        }

        // GET: ApplicationUsers/Details/5
        public ActionResult Details(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            ApplicationUser applicationUser = db.Users.Find(id);
            if (applicationUser == null)
            {
                return HttpNotFound();
            }
            return View(applicationUser);
        }

        // GET: ApplicationUsers/Create
        public ActionResult Create()
        {
            return View();
        }


        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Id,Email,EmailConfirmed,PasswordHash,SecurityStamp,PhoneNumber,PhoneNumberConfirmed,TwoFactorEnabled,LockoutEndDateUtc,LockoutEnabled,AccessFailedCount,UserName")] ApplicationUser applicationUser)
        {
            if (ModelState.IsValid)
            {
                db.Users.Add(applicationUser);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(applicationUser);
        }

        // GET: ApplicationUsers/Edit/5
        public ActionResult Edit(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            ApplicationUser applicationUser = db.Users.Find(id);
            if (applicationUser == null)
            {
                return HttpNotFound();
            }
            return View(applicationUser);
        }

        // POST: ApplicationUsers/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Id,Email,EmailConfirmed,PasswordHash,SecurityStamp,PhoneNumber,PhoneNumberConfirmed,TwoFactorEnabled,LockoutEndDateUtc,LockoutEnabled,AccessFailedCount,UserName")] ApplicationUser applicationUser)
        {
            if (ModelState.IsValid)
            {
                db.Entry(applicationUser).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(applicationUser);
        }

        // GET: ApplicationUsers/Delete/5
        public ActionResult Delete(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            ApplicationUser applicationUser = db.Users.Find(id);
            if (applicationUser == null)
            {
                return HttpNotFound();
            }
            return View(applicationUser);
        }

        // POST: ApplicationUsers/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(string id)
        {
            ApplicationUser applicationUser = db.Users.Find(id);
            db.Users.Remove(applicationUser);
            db.SaveChanges();
            return RedirectToAction("Index");
        }

        [HttpGet, ActionName("FollowOrUnfollowUser")]
        public JsonResult FollowOrUnfollowUser(string email, string toFollow)
        {
            var authenticatedUser = db.Users.Where(u => u.UserName == HttpContext.User.Identity.Name).FirstOrDefault();

            var userToFollow = authenticatedUser.FollowedUsers.Where(u => u.FullName == email).FirstOrDefault();

            if (userToFollow == null && toFollow == "true")
            {

                authenticatedUser.FollowedUsers.Add(db.Users.Where(u => u.FullName == email).FirstOrDefault());
            }

            if (userToFollow != null && toFollow == "false")
            {
                authenticatedUser.FollowedUsers.Remove(db.Users.Where(u => u.FullName == email).FirstOrDefault());
            }

            db.SaveChanges();

            return this.Json(JsonRequestBehavior.AllowGet);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
