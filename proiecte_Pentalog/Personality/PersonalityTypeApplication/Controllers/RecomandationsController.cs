using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using PersonalityTypeApplication.Models;

namespace PersonalityTypeApplication.Controllers
{
    //[Authorize(Users = "admin@yahoo.com")]
    public class RecomandationsController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: Recomandations
        public ActionResult Index()
        {
            var recomandations = db.Recomandations.Include(r => r.Personality);
            return View(recomandations.ToList());
        }

        // GET: Recomandations/Details/5
        [Authorize(Users = "admin@yahoo.com")]
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Recomandation recomandation = db.Recomandations.Find(id);
            if (recomandation == null)
            {
                return HttpNotFound();
            }
            return View(recomandation);
        }

        // GET: Recomandations/Create
        [Authorize(Users = "admin@yahoo.com")]
        public ActionResult Create()
        {
            ViewBag.PersonalityId = new SelectList(db.Personalities, "Id", "Name");
            return View();
        }

        // POST: Recomandations/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Id,Name,Description,PersonalityId,RecomandationType,Link")] Recomandation recomandation)
        {
            if (ModelState.IsValid)
            {
                db.Recomandations.Add(recomandation);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            ViewBag.PersonalityId = new SelectList(db.Personalities, "Id", "Name", recomandation.PersonalityId);
            return View(recomandation);
        }

        // GET: Recomandations/Edit/5
        [Authorize(Users = "admin@yahoo.com")]
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Recomandation recomandation = db.Recomandations.Find(id);
            if (recomandation == null)
            {
                return HttpNotFound();
            }
            ViewBag.PersonalityId = new SelectList(db.Personalities, "Id", "Name", recomandation.PersonalityId);
            return View(recomandation);
        }

        // POST: Recomandations/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see https://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Id,Name,Description,PersonalityId,RecomandationType,Link")] Recomandation recomandation)
        {
            if (ModelState.IsValid)
            {
                db.Entry(recomandation).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            ViewBag.PersonalityId = new SelectList(db.Personalities, "Id", "Name", recomandation.PersonalityId);
            return View(recomandation);
        }

        // GET: Recomandations/Delete/5
        [Authorize(Users = "admin@yahoo.com")]
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Recomandation recomandation = db.Recomandations.Find(id);
            if (recomandation == null)
            {
                return HttpNotFound();
            }
            return View(recomandation);
        }

        // POST: Recomandations/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Recomandation recomandation = db.Recomandations.Find(id);
            db.Recomandations.Remove(recomandation);
            db.SaveChanges();
            return RedirectToAction("Index");
        }

        public JsonResult GetPredominantPersonality(List<String> values)
        {
            List<int> personalitiesCount = new List<int>();
            for (int i = 0; i<4; i++)
            {
                personalitiesCount.Add(0);
            }

            var questions = db.Questions.ToList();
            for (int i = 0; i<questions.Count - 1; i++)
            {
                if (questions[i].PersonalityId == 8)
                {
                    personalitiesCount[0] += Convert.ToInt32(values[i]);
                }

                if (questions[i].PersonalityId == 2)
                {
                    personalitiesCount[1] += Convert.ToInt32(values[i]);
                }

                if (questions[i].PersonalityId == 3)
                {
                    personalitiesCount[2] += Convert.ToInt32(values[i]);
                }

                if (questions[i].PersonalityId == 4)
                {
                    personalitiesCount[3] += Convert.ToInt32(values[i]);
                }
            }

            int indexMax
                = !personalitiesCount.Any() ? -1 :
                personalitiesCount
                .Select((value, index) => new { Value = value, Index = index })
                .Aggregate((a, b) => (a.Value > b.Value) ? a : b)
                .Index;

            int predominantPersonalityId = 0;

            if (indexMax == 0)
            {
                predominantPersonalityId = 8;
            }

            if (indexMax == 1)
            {
                predominantPersonalityId = 2;
            }

            if (indexMax == 2)
            {
                predominantPersonalityId = 3;
            }

            if (indexMax == 3)
            {
                predominantPersonalityId = 4;
            }

            return Json(new
            {
                redirectUrl = Url.Action("RecomandationsBasedOnPersonalityType", "Recomandations", new { personalityId = String.Format(predominantPersonalityId.ToString()) }),
                isRedirect = true
            });
        }

        public ActionResult RecomandationsBasedOnPersonalityType(string personalityId)
        {
            var personalityIdInt = Convert.ToInt32(personalityId);
            var recomandations = db.Recomandations.Where(r => r.PersonalityId == personalityIdInt).ToList();

            var personalityName = db.Personalities.Where(p => p.Id == personalityIdInt).FirstOrDefault().Name;
            ViewBag.PredominantPersonalityType = personalityName;
            return View(recomandations);
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

