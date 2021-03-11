namespace PersonalityTypeApplication.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class RecomandationMigration : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Recomandations", "Recomandation_Type", c => c.String(nullable: false));
            AddColumn("dbo.Recomandations", "Link", c => c.String());
            AlterColumn("dbo.Recomandations", "Name", c => c.String(nullable: false));
            AlterColumn("dbo.Recomandations", "Description", c => c.String(maxLength: 250));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Recomandations", "Description", c => c.String());
            AlterColumn("dbo.Recomandations", "Name", c => c.String());
            DropColumn("dbo.Recomandations", "Link");
            DropColumn("dbo.Recomandations", "Recomandation_Type");
        }
    }
}
