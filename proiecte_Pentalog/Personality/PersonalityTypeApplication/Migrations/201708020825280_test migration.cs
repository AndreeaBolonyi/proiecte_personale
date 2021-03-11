namespace PersonalityTypeApplication.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class testmigration : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Personalities",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Description = c.String(),
                    })
                .PrimaryKey(t => t.Id);
            
            CreateTable(
                "dbo.Questions",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        Text = c.String(),
                        PersonalityId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.Id)
                .ForeignKey("dbo.Personalities", t => t.PersonalityId, cascadeDelete: true)
                .Index(t => t.PersonalityId);
            
            CreateTable(
                "dbo.Recomandations",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Description = c.String(),
                        PersonalityId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.Id)
                .ForeignKey("dbo.Personalities", t => t.PersonalityId, cascadeDelete: true)
                .Index(t => t.PersonalityId);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Recomandations", "PersonalityId", "dbo.Personalities");
            DropForeignKey("dbo.Questions", "PersonalityId", "dbo.Personalities");
            DropIndex("dbo.Recomandations", new[] { "PersonalityId" });
            DropIndex("dbo.Questions", new[] { "PersonalityId" });
            DropTable("dbo.Recomandations");
            DropTable("dbo.Questions");
            DropTable("dbo.Personalities");
        }
    }
}
