SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    ;WITH PendingRejections AS (
        SELECT OrderType, OrderId, MAX(OrderNumber) AS OrderNumber,
               MAX(RejectedBy) AS RejectedBy, MIN(CreatedAt) AS CreatedAt
        FROM dbo.OrderRejectionNotifications
        WHERE IsDismissed = 0
        GROUP BY OrderType, OrderId
    )
    INSERT INTO dbo.OrderRejectionNotifications
        (RecipientUserId, RejectedBy, OrderType, OrderId, OrderNumber, CreatedAt)
    SELECT u.UserId, p.RejectedBy, p.OrderType, p.OrderId, p.OrderNumber, p.CreatedAt
    FROM PendingRejections p
    JOIN dbo.Roles r ON r.RoleName = CASE
        WHEN p.OrderType IN ('Import Order', 'Supplier Return') THEN 'Purchase Staff'
        WHEN p.OrderType = 'Export Order' THEN 'Sale Staff'
    END
    JOIN dbo.Users u ON u.RoleId = r.RoleId AND u.IsActive = 1
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.OrderRejectionNotifications existing
        WHERE existing.RecipientUserId = u.UserId
          AND existing.OrderType = p.OrderType
          AND existing.OrderId = p.OrderId
    );

    UPDATE n
    SET IsDismissed = 1, DismissedAt = SYSUTCDATETIME()
    FROM dbo.OrderRejectionNotifications n
    WHERE n.IsDismissed = 0
      AND n.OrderType IN ('Import Order', 'Supplier Return', 'Export Order')
    AND NOT EXISTS (
          SELECT 1 FROM dbo.Users u
          JOIN dbo.Roles r ON r.RoleId = u.RoleId
          WHERE u.IsActive = 1
            AND u.UserId = n.RecipientUserId
            AND r.RoleName = CASE
                WHEN n.OrderType IN ('Import Order', 'Supplier Return') THEN 'Purchase Staff'
                WHEN n.OrderType = 'Export Order' THEN 'Sale Staff'
            END
      );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
