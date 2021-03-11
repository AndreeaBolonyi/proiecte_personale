using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Microsoft.AspNet.Identity;
using SocialApp.Models;

namespace SocialApp.Controllers
{
    [Authorize]
    public class MessagesController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Messages
        public ActionResult Index()
        {
            var users = db.Users.ToList();
            var user = users.Where(usr => usr.UserName == HttpContext.User.Identity.Name).FirstOrDefault();
            var messages = new List<Message>();

            var authenticatedUser = db.Users.Where(u => u.UserName == HttpContext.User.Identity.Name).FirstOrDefault();

            foreach(var message in authenticatedUser.Messages)
            {
                message.Title = authenticatedUser.FullName;
                messages.Add(message);
            }

            var followedUsers = authenticatedUser.FollowedUsers;
            foreach (ApplicationUser followedUser in followedUsers)
            {
                var followedUserMessages = followedUser.Messages;
                foreach (var followedUserMessage in followedUserMessages)
                {
                    followedUserMessage.Title = followedUser.FullName;
                    messages.Add(followedUserMessage);
                }
            }

            return View(messages.OrderBy(m => m.CreationDate));
        }

        public ActionResult MyMessages()
        {
            var msg = db.Messages.ToList();
            var myMessages = msg.Where(m => m.ApplicationUser.UserName == HttpContext.User.Identity.Name).ToList();

            if (myMessages == null || myMessages.Count == 0)
                return View("Create");

            foreach(var message in myMessages)
            {
                message.IsEditableByUser = true;
            }

            return View(myMessages.OrderBy(m => m.CreationDate));
        }

        // GET: Messages/Details/5
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Message message = db.Messages.Find(id);
            if (message == null)
            {
                return HttpNotFound();
            }
            return View(message);
        }

        [Authorize]
        public ActionResult Create()
        {
            return View();
        }

        // POST: Messages/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.

        [Authorize]
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Id,Title,Content")] Message message)
        {
            if (ModelState.IsValid)
            {
                message.CreationDate = DateTime.Now;
                message.ApplicationUserId = User.Identity.GetUserId();
                db.Messages.Add(message);
                db.SaveChanges();
                return RedirectToAction("Index", "Messages");
            }

            return View(message);
        }

        // GET: Messages/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Message message = db.Messages.Find(id);
            if (message == null)
            {
                return HttpNotFound();
            }
            return View(message);
        }

        // POST: Messages/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Id,Title,Content")] Message message)
        {
            if (ModelState.IsValid)
            {
                //db.Entry(message).State = EntityState.Modified;
                //db.SaveChanges();
                //return RedirectToAction("Index");

                db.Messages.Attach(message);

                //Specify the fields that should be updated.
                db.Entry(message).Property(x => x.Title).IsModified = true;
                db.Entry(message).Property(x => x.Content).IsModified = true;

                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(message);
        }

        // GET: Messages/Delete/5
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Message message = db.Messages.Find(id);
            if (message == null)
            {
                return HttpNotFound();
            }
            return View(message);
        }

        // POST: Messages/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Message message = db.Messages.Find(id);
            db.Messages.Remove(message);
            db.SaveChanges();
            return RedirectToAction("Index");
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
