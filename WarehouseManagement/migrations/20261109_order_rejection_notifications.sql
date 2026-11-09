SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID('dbo.OrderRejectionNotifications', 'U') IS NULL
    BEGIN
        CREATE TABLE dbo.OrderRejectionNotifications (
            NotificationId INT IDENTITY(1,1) PRIMARY KEY,
            RecipientUserId INT NOT NULL,
            RejectedBy INT NOT NULL,
            Decision VARCHAR(20) NOT NULL DEFAULT 'Rejected'
                CHECK (Decision IN ('Accepted', 'Rejected')),
            OrderType VARCHAR(30) NOT NULL
                CHECK (OrderType IN ('Import Order', 'Export Order', 'Supplier Return')),
            OrderId INT NOT NULL,
            OrderNumber VARCHAR(50) NOT NULL,
            CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
            IsDismissed BIT NOT NULL DEFAULT 0,
            DismissedAt DATETIME2 NULL,
            CONSTRAINT FK_OrderRejectionNotifications_Recipient
                FOREIGN KEY (RecipientUserId) REFERENCES dbo.Users(UserId),
            CONSTRAINT FK_OrderRejectionNotifications_RejectedBy
                FOREIGN KEY (RejectedBy) REFERENCES dbo.Users(UserId)
        );
    END;

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE name = 'IX_OrderRejectionNotifications_Recipient_Dismissed_Created'
          AND object_id = OBJECT_ID('dbo.OrderRejectionNotifications')
    )
    BEGIN
        CREATE INDEX IX_OrderRejectionNotifications_Recipient_Dismissed_Created
            ON dbo.OrderRejectionNotifications (RecipientUserId, IsDismissed, CreatedAt DESC);
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;