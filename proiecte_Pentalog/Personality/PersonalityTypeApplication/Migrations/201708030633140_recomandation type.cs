namespace PersonalityTypeApplication.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class recomandationtype : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Recomandations", "RecomandationType", c => c.Int(nullable: false));
            DropColumn("dbo.Recomandations", "Recomandation_Type");
        }
        
        public override void Down()
        {
            AddColumn("dbo.Recomandations", "Recomandation_Type", c => c.String(nullable: false));
            DropColumn("dbo.Recomandations", "RecomandationType");
        }
    }
}
