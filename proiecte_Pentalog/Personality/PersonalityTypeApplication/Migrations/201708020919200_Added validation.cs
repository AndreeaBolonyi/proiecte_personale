namespace PersonalityTypeApplication.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class Addedvalidation : DbMigration
    {
        public override void Up()
        {
            AlterColumn("dbo.Personalities", "Name", c => c.String(nullable: false));
        }
        
        public override void Down()
        {
            AlterColumn("dbo.Personalities", "Name", c => c.String());
        }
    }
}
