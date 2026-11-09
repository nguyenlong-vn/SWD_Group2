SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF COL_LENGTH('dbo.LossReports', 'ProvisionalDetailId') IS NULL
    BEGIN
        ALTER TABLE dbo.LossReports ADD ProvisionalDetailId INT NULL;
    END;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.LossReports')
          AND name = 'BatchId'
          AND is_nullable = 0
    )
    BEGIN
        ALTER TABLE dbo.LossReports ALTER COLUMN BatchId INT NULL;
    END;

    IF NOT EXISTS (
        SELECT 1 FROM sys.foreign_keys
        WHERE name = 'FK_LossReports_ProvisionalReceiptDetails'
    )
    BEGIN
        ALTER TABLE dbo.LossReports
            ADD CONSTRAINT FK_LossReports_ProvisionalReceiptDetails
            FOREIGN KEY (ProvisionalDetailId)
            REFERENCES dbo.ProvisionalReceiptDetails(ProvisionalDetailId);
    END;

    IF NOT EXISTS (
        SELECT 1 FROM sys.check_constraints
        WHERE name = 'CK_LossReports_Source'
    )
    BEGIN
        EXEC(N'ALTER TABLE dbo.LossReports
            ADD CONSTRAINT CK_LossReports_Source
            CHECK (BatchId IS NOT NULL OR ProvisionalDetailId IS NOT NULL)');
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;