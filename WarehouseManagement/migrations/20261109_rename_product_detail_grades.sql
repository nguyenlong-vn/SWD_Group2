SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID('dbo.ProvisionalReceiptDetailGrades', 'U') IS NOT NULL
       AND OBJECT_ID('dbo.ProductDetailGrades', 'U') IS NULL
    BEGIN
        EXEC sys.sp_rename 'dbo.ProvisionalReceiptDetailGrades', 'ProductDetailGrades';
    END;

    IF OBJECT_ID('dbo.ProductDetailGrades', 'U') IS NULL
    BEGIN
        CREATE TABLE dbo.ProductDetailGrades (
            DetailGradeId INT IDENTITY(1,1) PRIMARY KEY,
            ProvisionalDetailId INT NOT NULL,
            GradeType VARCHAR(20) NOT NULL CHECK (GradeType IN ('A', 'B', 'Damaged')),
            Quantity DECIMAL(18,3) NOT NULL CHECK (Quantity >= 0),
            CONSTRAINT UQ_ProductDetailGrades UNIQUE (ProvisionalDetailId, GradeType),
            FOREIGN KEY (ProvisionalDetailId)
                REFERENCES dbo.ProvisionalReceiptDetails(ProvisionalDetailId)
        );
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;