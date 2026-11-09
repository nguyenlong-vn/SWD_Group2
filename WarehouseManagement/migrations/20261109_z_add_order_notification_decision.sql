SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF COL_LENGTH('dbo.OrderRejectionNotifications', 'Decision') IS NULL
    BEGIN
        ALTER TABLE dbo.OrderRejectionNotifications
            ADD Decision VARCHAR(20) NOT NULL
                CONSTRAINT DF_OrderRejectionNotifications_Decision DEFAULT ('Rejected') WITH VALUES;
    END;

    IF NOT EXISTS (
        SELECT 1 FROM sys.check_constraints
        WHERE name = 'CK_OrderRejectionNotifications_Decision'
          AND parent_object_id = OBJECT_ID('dbo.OrderRejectionNotifications')
    )
    BEGIN
        EXEC sys.sp_executesql N'ALTER TABLE dbo.OrderRejectionNotifications
            ADD CONSTRAINT CK_OrderRejectionNotifications_Decision
                CHECK (Decision IN (''Accepted'', ''Rejected''))';
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;