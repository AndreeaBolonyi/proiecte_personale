namespace PersonalityTypeApplication.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class Recomandationerror : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Recomandations", "Description", c => c.String(maxLength: 1000));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Recomandations", "Description", c => c.String(maxLength: 250));
        }
    }
}
