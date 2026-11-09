SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    UPDATE pr
    SET Status = CASE
        WHEN EXISTS (
            SELECT 1 FROM dbo.GoodsReceipts gr WHERE gr.ImportOrderId = pr.ImportOrderId
        ) THEN 'Completed'
        WHEN EXISTS (
            SELECT 1
            FROM dbo.SupplierReturnDetails srd
            JOIN dbo.ProvisionalReceiptDetails d ON d.ProvisionalDetailId = srd.ProvisionalDetailId
            JOIN dbo.SupplierReturns sr ON sr.ReturnId = srd.ReturnId
            WHERE d.ProvisionalReceiptId = pr.ProvisionalReceiptId AND sr.Status = 'Pending'
        ) THEN 'Awaiting Approval'
        WHEN EXISTS (
            SELECT 1
            FROM dbo.SupplierReturnDetails srd
            JOIN dbo.ProvisionalReceiptDetails d ON d.ProvisionalDetailId = srd.ProvisionalDetailId
            JOIN dbo.SupplierReturns sr ON sr.ReturnId = srd.ReturnId
            WHERE d.ProvisionalReceiptId = pr.ProvisionalReceiptId AND sr.Status = 'Completed'
        ) THEN 'Completed'
        ELSE 'Not Approved'
    END
    FROM dbo.ProvisionalReceipts pr
    WHERE pr.Status IN ('Pending', 'Not Approved', 'Completed');

    DECLARE @defaultConstraint sysname;
    SELECT @defaultConstraint = dc.name
    FROM sys.default_constraints dc
    JOIN sys.columns c ON c.default_object_id = dc.object_id
    WHERE dc.parent_object_id = OBJECT_ID('dbo.ProvisionalReceipts')
      AND c.name = 'Status';

    IF @defaultConstraint IS NOT NULL
    BEGIN
        DECLARE @dropDefaultSql NVARCHAR(300);
        SET @dropDefaultSql = N'ALTER TABLE dbo.ProvisionalReceipts DROP CONSTRAINT '
            + QUOTENAME(@defaultConstraint);
        EXEC sys.sp_executesql @dropDefaultSql;
    END;

    ALTER TABLE dbo.ProvisionalReceipts
        ADD CONSTRAINT DF_ProvisionalReceipts_Status
        DEFAULT ('Not Approved') FOR Status;

    IF NOT EXISTS (
        SELECT 1 FROM sys.check_constraints
        WHERE name = 'CK_ProvisionalReceipts_Status'
          AND parent_object_id = OBJECT_ID('dbo.ProvisionalReceipts')
    )
    BEGIN
        EXEC sys.sp_executesql N'ALTER TABLE dbo.ProvisionalReceipts
            ADD CONSTRAINT CK_ProvisionalReceipts_Status
            CHECK (Status IN (''Awaiting Approval'', ''Not Approved'', ''Completed''))';
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;