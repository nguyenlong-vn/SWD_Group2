USE master;
EXEC('IF DB_ID(''WarehouseManagement'') IS NOT NULL DROP DATABASE WarehouseManagement');
EXEC('CREATE DATABASE WarehouseManagement');
USE WarehouseManagement;

CREATE TABLE Roles (
    RoleId INT IDENTITY(1,1) PRIMARY KEY,
    RoleName NVARCHAR(100) UNIQUE NOT NULL,
    Description NVARCHAR(255)
);

CREATE TABLE Users (
    UserId INT IDENTITY(1,1) PRIMARY KEY,
    RoleId INT NOT NULL,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    IsActive BIT NOT NULL DEFAULT 1,
    FOREIGN KEY (RoleId) REFERENCES Roles(RoleId)
);

CREATE TABLE SystemLogs (
    LogId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,
    Action VARCHAR(100) NOT NULL,
    Details NVARCHAR(500),
    CreatedAt DATETIME NOT NULL,
    FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

CREATE TABLE OrderRejectionNotifications (
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
        FOREIGN KEY (RecipientUserId) REFERENCES Users(UserId),
    CONSTRAINT FK_OrderRejectionNotifications_RejectedBy
        FOREIGN KEY (RejectedBy) REFERENCES Users(UserId)
);

CREATE INDEX IX_OrderRejectionNotifications_Recipient_Dismissed_Created
    ON OrderRejectionNotifications (RecipientUserId, IsDismissed, CreatedAt DESC);

CREATE TABLE Categories (
    CategoryId INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100) UNIQUE NOT NULL,
    Description NVARCHAR(255)
);

CREATE TABLE Products (
    ProductId INT IDENTITY(1,1) PRIMARY KEY,
    CategoryId INT NOT NULL,
    ProductCode VARCHAR(50) UNIQUE NOT NULL,
    ProductName NVARCHAR(100) NOT NULL,
    Unit NVARCHAR(30) NOT NULL,
    BasePrice DECIMAL(18,2) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Active',
    FOREIGN KEY (CategoryId) REFERENCES Categories(CategoryId)
);

CREATE TABLE Suppliers (
    SupplierId INT IDENTITY(1,1) PRIMARY KEY,
    SupplierCode VARCHAR(50) UNIQUE NOT NULL,
    SupplierName NVARCHAR(150) NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(100),
    Address NVARCHAR(255),
    Status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

CREATE TABLE Customers (
    CustomerId INT IDENTITY(1,1) PRIMARY KEY,
    CustomerCode VARCHAR(50) UNIQUE NOT NULL,
    CustomerName NVARCHAR(150) NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(100),
    Address NVARCHAR(255),
    Status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

CREATE TABLE StorageLocations (
    LocationId INT IDENTITY(1,1) PRIMARY KEY,
    ZoneName NVARCHAR(100) NOT NULL,
    ShelfCode VARCHAR(20) NOT NULL,
    BinCode VARCHAR(20) NOT NULL,
    LocationType VARCHAR(30) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Available'
);

CREATE TABLE ImportOrders (
    ImportOrderId INT IDENTITY(1,1) PRIMARY KEY,
    SupplierId INT NOT NULL,
    CreatedBy INT NOT NULL,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    OrderDate DATETIME NOT NULL,
    ExpectedDate DATETIME NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    FOREIGN KEY (SupplierId) REFERENCES Suppliers(SupplierId),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserId)
);

CREATE TABLE ImportOrderDetails (
    ImportDetailId INT IDENTITY(1,1) PRIMARY KEY,
    ImportOrderId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Unit NVARCHAR(30) NOT NULL,
    UnitPrice DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (ImportOrderId) REFERENCES ImportOrders(ImportOrderId),
    FOREIGN KEY (ProductId) REFERENCES Products(ProductId)
);

CREATE TABLE ProvisionalReceipts (
    ProvisionalReceiptId INT IDENTITY(1,1) PRIMARY KEY,
    ImportOrderId INT NOT NULL,
    ReceiptNumber VARCHAR(50) UNIQUE NOT NULL,
    ReceivedDate DATETIME NOT NULL,
    ReceivedBy INT NOT NULL,
        Status VARCHAR(20) NOT NULL DEFAULT 'Not Approved'
        CHECK (Status IN ('Awaiting Approval', 'Not Approved', 'Completed')),
    QCStatus VARCHAR(20) NOT NULL DEFAULT 'Pending',
    Notes NVARCHAR(500),
    FOREIGN KEY (ImportOrderId) REFERENCES ImportOrders(ImportOrderId),
    FOREIGN KEY (ReceivedBy) REFERENCES Users(UserId)
);

CREATE TABLE ProvisionalReceiptDetails (
    ProvisionalDetailId INT IDENTITY(1,1) PRIMARY KEY,
    ProvisionalReceiptId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Unit NVARCHAR(30) NOT NULL,
    FOREIGN KEY (ProvisionalReceiptId) REFERENCES ProvisionalReceipts(ProvisionalReceiptId),
    FOREIGN KEY (ProductId) REFERENCES Products(ProductId)
);

CREATE TABLE ProductDetailGrades (
    DetailGradeId INT IDENTITY(1,1) PRIMARY KEY,
    ProvisionalDetailId INT NOT NULL,
    GradeType VARCHAR(20) NOT NULL
    CHECK (GradeType IN ('A', 'B', 'Damaged')),
    Quantity DECIMAL(18,3) NOT NULL CHECK (Quantity >= 0),
    CONSTRAINT UQ_ProvisionalReceiptDetailGrades UNIQUE (ProvisionalDetailId, GradeType),
    FOREIGN KEY (ProvisionalDetailId) REFERENCES ProvisionalReceiptDetails(ProvisionalDetailId)
);

CREATE TABLE InventoryBatches (
    BatchId INT IDENTITY(1,1) PRIMARY KEY,
    ProductId INT NOT NULL,
    LocationId INT NOT NULL,
    BatchCode VARCHAR(50) UNIQUE NOT NULL,
    ReceivedDate DATETIME NOT NULL,
    ExpiryDate DATETIME NOT NULL,
    InitialQty DECIMAL(18,3) NOT NULL,
    CurrentQty DECIMAL(18,3) NOT NULL,
    ReservedQty DECIMAL(18,3) NOT NULL DEFAULT 0,
    AvailableQty DECIMAL(18,3) NOT NULL,
    UnitCost DECIMAL(18,2) NOT NULL,
    QualityGrade VARCHAR(20) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Available',
    FOREIGN KEY (ProductId) REFERENCES Products(ProductId),
    FOREIGN KEY (LocationId) REFERENCES StorageLocations(LocationId)
);

CREATE TABLE GoodsReceipts (
    ReceiptId INT IDENTITY(1,1) PRIMARY KEY,
    ImportOrderId INT NOT NULL,
    ReceiptNumber VARCHAR(50) UNIQUE NOT NULL,
    ReceivedDate DATETIME NOT NULL,
    ReceivedBy INT NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Completed',
    Notes NVARCHAR(500),
    FOREIGN KEY (ImportOrderId) REFERENCES ImportOrders(ImportOrderId),
    FOREIGN KEY (ReceivedBy) REFERENCES Users(UserId)
);

CREATE TABLE GoodsReceiptDetails (
    ReceiptDetailId INT IDENTITY(1,1) PRIMARY KEY,
    ReceiptId INT NOT NULL,
    ProductId INT NOT NULL,
    BatchId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Unit NVARCHAR(30) NOT NULL,
    UnitCost DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (ReceiptId) REFERENCES GoodsReceipts(ReceiptId),
    FOREIGN KEY (ProductId) REFERENCES Products(ProductId),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId)
);

CREATE TABLE SupplierReturns (
    ReturnId INT IDENTITY(1,1) PRIMARY KEY,
    SupplierId INT NOT NULL,
    ImportOrderId INT NOT NULL,
    ReturnNumber VARCHAR(50) UNIQUE NOT NULL,
    ReturnDate DATETIME NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    CreatedBy INT NOT NULL,
    FOREIGN KEY (SupplierId) REFERENCES Suppliers(SupplierId),
    FOREIGN KEY (ImportOrderId) REFERENCES ImportOrders(ImportOrderId),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserId)
);

CREATE TABLE SupplierReturnDetails (
    ReturnDetailId INT IDENTITY(1,1) PRIMARY KEY,
    ReturnId INT NOT NULL,
    ProvisionalDetailId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    FOREIGN KEY (ReturnId) REFERENCES SupplierReturns(ReturnId),
    FOREIGN KEY (ProvisionalDetailId) REFERENCES ProvisionalReceiptDetails(ProvisionalDetailId)
);

CREATE TABLE ExportOrders (
    ExportOrderId INT IDENTITY(1,1) PRIMARY KEY,
    CustomerId INT NOT NULL,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    OrderDate DATETIME NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    CreatedBy INT NOT NULL,
    FOREIGN KEY (CustomerId) REFERENCES Customers(CustomerId),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserId)
);

CREATE TABLE ExportOrderDetails (
    ExportDetailId INT IDENTITY(1,1) PRIMARY KEY,
    ExportOrderId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Unit NVARCHAR(30) NOT NULL,
    UnitPrice DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (ExportOrderId) REFERENCES ExportOrders(ExportOrderId),
    FOREIGN KEY (ProductId) REFERENCES Products(ProductId)
);

CREATE TABLE StockAllocations (
    AllocationId INT IDENTITY(1,1) PRIMARY KEY,
    ExportDetailId INT NOT NULL,
    BatchId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    AllocatedDate DATETIME NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Allocated',
    FOREIGN KEY (ExportDetailId) REFERENCES ExportOrderDetails(ExportDetailId),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId)
);

CREATE TABLE StockMovements (
    MovementId INT IDENTITY(1,1) PRIMARY KEY,
    BatchId INT NOT NULL,
    FromLocationId INT,
    ToLocationId INT,
    MovedBy INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    MovementDate DATETIME NOT NULL,
    Reason NVARCHAR(255),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId),
    FOREIGN KEY (FromLocationId) REFERENCES StorageLocations(LocationId),
    FOREIGN KEY (ToLocationId) REFERENCES StorageLocations(LocationId),
    FOREIGN KEY (MovedBy) REFERENCES Users(UserId)
);

CREATE TABLE QualityInspections (
    InspectionId INT IDENTITY(1,1) PRIMARY KEY,
    ProvisionalReceiptId INT NOT NULL,
    InspectedBy INT NOT NULL,
    InspectionDate DATETIME NOT NULL,
    Result VARCHAR(20) NOT NULL,
    Notes NVARCHAR(500),
    FOREIGN KEY (ProvisionalReceiptId) REFERENCES ProvisionalReceipts(ProvisionalReceiptId),
    FOREIGN KEY (InspectedBy) REFERENCES Users(UserId)
);

CREATE TABLE LossReports (
    LossReportId INT IDENTITY(1,1) PRIMARY KEY,
    BatchId INT NULL,
    ProvisionalDetailId INT NULL,
    ReportedBy INT NOT NULL,
    LossType VARCHAR(30) NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    ReportedDate DATETIME NOT NULL,
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId),
    FOREIGN KEY (ProvisionalDetailId) REFERENCES ProvisionalReceiptDetails(ProvisionalDetailId),
    FOREIGN KEY (ReportedBy) REFERENCES Users(UserId),
    CONSTRAINT CK_LossReports_Source CHECK (BatchId IS NOT NULL OR ProvisionalDetailId IS NOT NULL)
);

CREATE TABLE StockCounts (
    CountId INT IDENTITY(1,1) PRIMARY KEY,
    CountedBy INT NOT NULL,
    CountDate DATETIME NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Completed',
    Notes NVARCHAR(500),
    FOREIGN KEY (CountedBy) REFERENCES Users(UserId)
);

CREATE TABLE StockCountDetails (
    CountDetailId INT IDENTITY(1,1) PRIMARY KEY,
    CountId INT NOT NULL,
    BatchId INT NOT NULL,
    SystemQty DECIMAL(18,3) NOT NULL,
    CountedQty DECIMAL(18,3) NOT NULL,
    Difference DECIMAL(18,3) NOT NULL,
    FOREIGN KEY (CountId) REFERENCES StockCounts(CountId),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId)
);

CREATE TABLE StockAdjustments (
    AdjustmentId INT IDENTITY(1,1) PRIMARY KEY,
    CountId INT NOT NULL,
    RequestedBy INT NOT NULL,
    ApprovedBy INT,
    RequestDate DATETIME NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    ApprovedDate DATETIME,
    FOREIGN KEY (CountId) REFERENCES StockCounts(CountId),
    FOREIGN KEY (RequestedBy) REFERENCES Users(UserId),
    FOREIGN KEY (ApprovedBy) REFERENCES Users(UserId)
);

CREATE TABLE StockAdjustmentDetails (
    AdjustmentDetailId INT IDENTITY(1,1) PRIMARY KEY,
    AdjustmentId INT NOT NULL,
    BatchId INT NOT NULL,
    SystemQty DECIMAL(18,3) NOT NULL,
    ActualQty DECIMAL(18,3) NOT NULL,
    QuantityChange DECIMAL(18,3) NOT NULL,
    FOREIGN KEY (AdjustmentId) REFERENCES StockAdjustments(AdjustmentId),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId)
);

INSERT INTO Roles (RoleName, Description)
VALUES
(N'Administrator', N'Manages users, system logs, and reports'),
(N'Warehouse Manager', N'Approves stock adjustments and controls warehouse operations'),
(N'Warehouse Staff', N'Receives, stores, counts, moves, and picks goods'),
(N'Purchase Staff', N'Manages suppliers and import orders'),
(N'Sale Staff', N'Manages customers and export orders');

INSERT INTO Users (RoleId, Username, Password, FullName, Email, IsActive)
VALUES
(1, 'admin01', '123456', N'Nguyen Van An', 'admin@vfw-wms.com', 1),
(2, 'manager01', '123456', N'Tran Thi Mai', 'manager@vfw-wms.com', 1),
(3, 'warehouse01', '123456', N'Le Van Nam', 'warehouse01@vfw-wms.com', 1),
(4, 'purchase01', '123456', N'Nguyen Thi Hoa', 'purchase@vfw-wms.com', 1),
(5, 'sale01', '123456', N'Tran Van Minh', 'sale@vfw-wms.com', 1);

INSERT INTO SystemLogs (UserId, Action, Details, CreatedAt)
VALUES
(1, 'LOGIN', N'System activity record 1', '2026-01-05 00:00:00'),
(2, 'CREATE_EXPORT_ORDER', N'System activity record 2', '2026-01-06 00:00:00'),
(3, 'CREATE_IMPORT_ORDER', N'System activity record 3', '2026-01-07 00:00:00'),
(4, 'RECEIVE_GOODS', N'System activity record 4', '2026-01-08 00:00:00'),
(5, 'QC_INSPECTION', N'System activity record 5', '2026-01-09 00:00:00'),
(1, 'CREATE_STOCK_COUNT', N'System activity record 6', '2026-01-10 00:00:00'),
(2, 'APPROVE_ADJUSTMENT', N'System activity record 7', '2026-01-11 00:00:00'),
(3, 'STOCK_MOVEMENT', N'System activity record 8', '2026-01-12 00:00:00'),
(4, 'CREATE_RETURN', N'System activity record 9', '2026-01-13 00:00:00'),
(5, 'CREATE_PRODUCT', N'System activity record 10', '2026-01-14 00:00:00'),
(1, 'LOGIN', N'System activity record 11', '2026-01-15 00:00:00'),
(2, 'CREATE_EXPORT_ORDER', N'System activity record 12', '2026-01-16 00:00:00'),
(3, 'CREATE_IMPORT_ORDER', N'System activity record 13', '2026-01-17 00:00:00'),
(4, 'RECEIVE_GOODS', N'System activity record 14', '2026-01-18 00:00:00'),
(5, 'QC_INSPECTION', N'System activity record 15', '2026-01-19 00:00:00'),
(1, 'CREATE_STOCK_COUNT', N'System activity record 16', '2026-01-20 00:00:00'),
(2, 'APPROVE_ADJUSTMENT', N'System activity record 17', '2026-01-21 00:00:00'),
(3, 'STOCK_MOVEMENT', N'System activity record 18', '2026-01-22 00:00:00'),
(4, 'CREATE_RETURN', N'System activity record 19', '2026-01-23 00:00:00'),
(5, 'CREATE_PRODUCT', N'System activity record 20', '2026-01-24 00:00:00');

INSERT INTO Categories (CategoryName, Description)
VALUES
(N'Rau xanh', N'Nhóm sản phẩm rau xanh'),
(N'Củ', N'Nhóm sản phẩm củ'),
(N'Quả', N'Nhóm sản phẩm quả'),
(N'Rau gia vị', N'Nhóm sản phẩm rau gia vị'),
(N'Nấm', N'Nhóm sản phẩm nấm'),
(N'Trái cây nhiệt đới', N'Nhóm sản phẩm trái cây nhiệt đới'),
(N'Trái cây ôn đới', N'Nhóm sản phẩm trái cây ôn đới'),
(N'Rau ăn lá', N'Nhóm sản phẩm rau ăn lá'),
(N'Rau ăn củ', N'Nhóm sản phẩm rau ăn củ'),
(N'Rau ăn quả', N'Nhóm sản phẩm rau ăn quả');

INSERT INTO Products (CategoryId, ProductCode, ProductName, Unit, BasePrice, Status)
VALUES
(1, 'P001', N'Cải xanh', N'kg', 11500, 'Active'),
(2, 'P002', N'Cà rốt', N'kg', 13000, 'Active'),
(3, 'P003', N'Cà chua', N'kg', 14500, 'Active'),
(4, 'P004', N'Khoai tây', N'kg', 16000, 'Active'),
(5, 'P005', N'Bắp cải', N'kg', 17500, 'Active'),
(6, 'P006', N'Dưa leo', N'kg', 19000, 'Active'),
(7, 'P007', N'Rau muống', N'kg', 20500, 'Active'),
(8, 'P008', N'Xà lách', N'kg', 22000, 'Active'),
(9, 'P009', N'Bí đỏ', N'kg', 23500, 'Active'),
(10, 'P010', N'Hành tây', N'kg', 25000, 'Active'),
(1, 'P011', N'Chuối', N'kg', 26500, 'Active'),
(2, 'P012', N'Cam', N'kg', 28000, 'Active'),
(3, 'P013', N'Táo', N'kg', 29500, 'Active'),
(4, 'P014', N'Xoài', N'kg', 31000, 'Active'),
(5, 'P015', N'Dưa hấu', N'kg', 32500, 'Active'),
(6, 'P016', N'Thanh long', N'kg', 34000, 'Active'),
(7, 'P017', N'Dứa', N'kg', 35500, 'Active'),
(8, 'P018', N'Bơ', N'kg', 37000, 'Active'),
(9, 'P019', N'Nho', N'kg', 38500, 'Active'),
(10, 'P020', N'Lê', N'kg', 40000, 'Active');

INSERT INTO Suppliers (SupplierCode, SupplierName, Phone, Email, Address, Status)
VALUES
('SUP001', N'Nhà cung cấp nông sản 1', '0901000001', 'supplier1@example.com', N'Tỉnh/Thành phố 1', 'Active'),
('SUP002', N'Nhà cung cấp nông sản 2', '0901000002', 'supplier2@example.com', N'Tỉnh/Thành phố 2', 'Active'),
('SUP003', N'Nhà cung cấp nông sản 3', '0901000003', 'supplier3@example.com', N'Tỉnh/Thành phố 3', 'Active'),
('SUP004', N'Nhà cung cấp nông sản 4', '0901000004', 'supplier4@example.com', N'Tỉnh/Thành phố 4', 'Active'),
('SUP005', N'Nhà cung cấp nông sản 5', '0901000005', 'supplier5@example.com', N'Tỉnh/Thành phố 5', 'Active'),
('SUP006', N'Nhà cung cấp nông sản 6', '0901000006', 'supplier6@example.com', N'Tỉnh/Thành phố 6', 'Active'),
('SUP007', N'Nhà cung cấp nông sản 7', '0901000007', 'supplier7@example.com', N'Tỉnh/Thành phố 7', 'Active'),
('SUP008', N'Nhà cung cấp nông sản 8', '0901000008', 'supplier8@example.com', N'Tỉnh/Thành phố 8', 'Active'),
('SUP009', N'Nhà cung cấp nông sản 9', '0901000009', 'supplier9@example.com', N'Tỉnh/Thành phố 9', 'Active'),
('SUP010', N'Nhà cung cấp nông sản 10', '0901000010', 'supplier10@example.com', N'Tỉnh/Thành phố 10', 'Active'),
('SUP011', N'Nhà cung cấp nông sản 11', '0901000011', 'supplier11@example.com', N'Tỉnh/Thành phố 11', 'Active'),
('SUP012', N'Nhà cung cấp nông sản 12', '0901000012', 'supplier12@example.com', N'Tỉnh/Thành phố 12', 'Active'),
('SUP013', N'Nhà cung cấp nông sản 13', '0901000013', 'supplier13@example.com', N'Tỉnh/Thành phố 13', 'Active'),
('SUP014', N'Nhà cung cấp nông sản 14', '0901000014', 'supplier14@example.com', N'Tỉnh/Thành phố 14', 'Active'),
('SUP015', N'Nhà cung cấp nông sản 15', '0901000015', 'supplier15@example.com', N'Tỉnh/Thành phố 15', 'Active'),
('SUP016', N'Nhà cung cấp nông sản 16', '0901000016', 'supplier16@example.com', N'Tỉnh/Thành phố 16', 'Active'),
('SUP017', N'Nhà cung cấp nông sản 17', '0901000017', 'supplier17@example.com', N'Tỉnh/Thành phố 17', 'Active'),
('SUP018', N'Nhà cung cấp nông sản 18', '0901000018', 'supplier18@example.com', N'Tỉnh/Thành phố 18', 'Active'),
('SUP019', N'Nhà cung cấp nông sản 19', '0901000019', 'supplier19@example.com', N'Tỉnh/Thành phố 19', 'Active'),
('SUP020', N'Nhà cung cấp nông sản 20', '0901000020', 'supplier20@example.com', N'Tỉnh/Thành phố 20', 'Active');

INSERT INTO Customers (CustomerCode, CustomerName, Phone, Email, Address, Status)
VALUES
('CUS001', N'Khách hàng bán sỉ 1', '0912000001', 'customer1@example.com', N'Hà Nội - Khu vực 1', 'Active'),
('CUS002', N'Khách hàng bán sỉ 2', '0912000002', 'customer2@example.com', N'Hà Nội - Khu vực 2', 'Active'),
('CUS003', N'Khách hàng bán sỉ 3', '0912000003', 'customer3@example.com', N'Hà Nội - Khu vực 3', 'Active'),
('CUS004', N'Khách hàng bán sỉ 4', '0912000004', 'customer4@example.com', N'Hà Nội - Khu vực 4', 'Active'),
('CUS005', N'Khách hàng bán sỉ 5', '0912000005', 'customer5@example.com', N'Hà Nội - Khu vực 5', 'Active'),
('CUS006', N'Khách hàng bán sỉ 6', '0912000006', 'customer6@example.com', N'Hà Nội - Khu vực 6', 'Active'),
('CUS007', N'Khách hàng bán sỉ 7', '0912000007', 'customer7@example.com', N'Hà Nội - Khu vực 7', 'Active'),
('CUS008', N'Khách hàng bán sỉ 8', '0912000008', 'customer8@example.com', N'Hà Nội - Khu vực 8', 'Active'),
('CUS009', N'Khách hàng bán sỉ 9', '0912000009', 'customer9@example.com', N'Hà Nội - Khu vực 9', 'Active'),
('CUS010', N'Khách hàng bán sỉ 10', '0912000010', 'customer10@example.com', N'Hà Nội - Khu vực 10', 'Active'),
('CUS011', N'Khách hàng bán sỉ 11', '0912000011', 'customer11@example.com', N'Hà Nội - Khu vực 11', 'Active'),
('CUS012', N'Khách hàng bán sỉ 12', '0912000012', 'customer12@example.com', N'Hà Nội - Khu vực 12', 'Active'),
('CUS013', N'Khách hàng bán sỉ 13', '0912000013', 'customer13@example.com', N'Hà Nội - Khu vực 13', 'Active'),
('CUS014', N'Khách hàng bán sỉ 14', '0912000014', 'customer14@example.com', N'Hà Nội - Khu vực 14', 'Active'),
('CUS015', N'Khách hàng bán sỉ 15', '0912000015', 'customer15@example.com', N'Hà Nội - Khu vực 15', 'Active'),
('CUS016', N'Khách hàng bán sỉ 16', '0912000016', 'customer16@example.com', N'Hà Nội - Khu vực 16', 'Active'),
('CUS017', N'Khách hàng bán sỉ 17', '0912000017', 'customer17@example.com', N'Hà Nội - Khu vực 17', 'Active'),
('CUS018', N'Khách hàng bán sỉ 18', '0912000018', 'customer18@example.com', N'Hà Nội - Khu vực 18', 'Active'),
('CUS019', N'Khách hàng bán sỉ 19', '0912000019', 'customer19@example.com', N'Hà Nội - Khu vực 19', 'Active'),
('CUS020', N'Khách hàng bán sỉ 20', '0912000020', 'customer20@example.com', N'Hà Nội - Khu vực 20', 'Active');

INSERT INTO StorageLocations (ZoneName, ShelfCode, BinCode, LocationType, Status)
VALUES
(N'Khu A - Rau', 'S01', 'B01', 'Main', 'Available'),
(N'Khu A - Rau', 'S02', 'B02', 'Main', 'Available'),
(N'Khu A - Rau', 'S03', 'B03', 'Main', 'Available'),
(N'Khu A - Rau', 'S04', 'B04', 'Main', 'Available'),
(N'Khu A - Rau', 'S05', 'B05', 'Main', 'Available'),
(N'Khu B - Củ', 'S06', 'B06', 'Main', 'Available'),
(N'Khu B - Củ', 'S07', 'B07', 'Main', 'Available'),
(N'Khu B - Củ', 'S08', 'B08', 'Main', 'Available'),
(N'Khu B - Củ', 'S09', 'B09', 'Main', 'Available'),
(N'Khu B - Củ', 'S10', 'B10', 'Main', 'Available'),
(N'Khu C - Quả', 'S11', 'B11', 'Main', 'Available'),
(N'Khu C - Quả', 'S12', 'B12', 'Main', 'Available'),
(N'Khu C - Quả', 'S13', 'B13', 'Main', 'Available'),
(N'Khu C - Quả', 'S14', 'B14', 'Main', 'Available'),
(N'Khu C - Quả', 'S15', 'B15', 'Main', 'Available'),
(N'Khu D - Cách ly', 'S16', 'B16', 'Main', 'Available'),
(N'Khu D - Cách ly', 'S17', 'B17', 'Main', 'Available'),
(N'Khu D - Cách ly', 'S18', 'B18', 'Main', 'Available'),
(N'Khu D - Cách ly', 'S19', 'B19', 'Main', 'Available'),
(N'Khu D - Cách ly', 'S20', 'B20', 'Main', 'Available');

INSERT INTO ImportOrders (SupplierId, CreatedBy, OrderNumber, OrderDate, ExpectedDate, Status)
VALUES
(1, 4, 'PO001', '2026-01-03 08:00:00', '2026-01-05 08:00:00', 'Completed'),
(2, 4, 'PO002', '2026-01-04 08:00:00', '2026-01-06 08:00:00', 'Completed'),
(3, 4, 'PO003', '2026-01-05 08:00:00', '2026-01-07 08:00:00', 'Pending'),
(4, 4, 'PO004', '2026-01-06 08:00:00', '2026-01-08 08:00:00', 'Pending'),
(5, 4, 'PO005', '2026-01-07 08:00:00', '2026-01-09 08:00:00', 'Pending'),
(6, 4, 'PO006', '2026-01-08 08:00:00', '2026-01-10 08:00:00', 'Pending'),
(7, 4, 'PO007', '2026-01-09 08:00:00', '2026-01-11 08:00:00', 'Pending'),
(8, 4, 'PO008', '2026-01-10 08:00:00', '2026-01-12 08:00:00', 'Pending'),
(9, 4, 'PO009', '2026-01-11 08:00:00', '2026-01-13 08:00:00', 'Pending'),
(10, 4, 'PO010', '2026-01-12 08:00:00', '2026-01-14 08:00:00', 'Pending'),
(11, 4, 'PO011', '2026-01-13 08:00:00', '2026-01-15 08:00:00', 'Pending'),
(12, 4, 'PO012', '2026-01-14 08:00:00', '2026-01-16 08:00:00', 'Pending'),
(13, 4, 'PO013', '2026-01-15 08:00:00', '2026-01-17 08:00:00', 'Pending'),
(14, 4, 'PO014', '2026-01-16 08:00:00', '2026-01-18 08:00:00', 'Pending'),
(15, 4, 'PO015', '2026-01-17 08:00:00', '2026-01-19 08:00:00', 'Pending'),
(16, 4, 'PO016', '2026-01-18 08:00:00', '2026-01-20 08:00:00', 'Pending'),
(17, 4, 'PO017', '2026-01-19 08:00:00', '2026-01-21 08:00:00', 'Pending'),
(18, 4, 'PO018', '2026-01-20 08:00:00', '2026-01-22 08:00:00', 'Pending'),
(19, 4, 'PO019', '2026-01-21 08:00:00', '2026-01-23 08:00:00', 'Pending'),
(20, 4, 'PO020', '2026-01-22 08:00:00', '2026-01-24 08:00:00', 'Pending');

INSERT INTO ImportOrderDetails (ImportOrderId, ProductId, Quantity, Unit, UnitPrice)
VALUES
(1, 1, 200, N'kg', 10500),
(1, 2, 250, N'kg', 11000),
(2, 2, 210, N'kg', 11200),
(2, 3, 260, N'kg', 11500),
(3, 3, 220, N'kg', 11400),
(3, 4, 270, N'kg', 11800),
(4, 4, 230, N'kg', 11600),
(4, 5, 280, N'kg', 12000),
(5, 5, 240, N'kg', 11800),
(5, 6, 290, N'kg', 12200),
(6, 6, 250, N'kg', 12000),
(6, 7, 300, N'kg', 12500),
(7, 7, 200, N'kg', 12100),
(7, 8, 250, N'kg', 12600),
(8, 8, 210, N'kg', 12200),
(8, 9, 260, N'kg', 12800),
(9, 9, 220, N'kg', 12400),
(9, 10, 270, N'kg', 13000),
(10, 10, 230, N'kg', 12600),
(10, 11, 280, N'kg', 13200),
(11, 11, 240, N'kg', 12800),
(11, 12, 290, N'kg', 13500),
(12, 12, 250, N'kg', 13000),
(12, 13, 300, N'kg', 13800),
(13, 13, 200, N'kg', 13200),
(13, 14, 250, N'kg', 14000),
(14, 14, 210, N'kg', 13400),
(14, 15, 260, N'kg', 14200),
(15, 15, 220, N'kg', 13600),
(15, 16, 270, N'kg', 14500),
(16, 16, 230, N'kg', 13800),
(16, 17, 280, N'kg', 14800),
(17, 17, 240, N'kg', 14000),
(17, 18, 290, N'kg', 15000),
(18, 18, 250, N'kg', 14200),
(18, 19, 300, N'kg', 15200),
(19, 19, 260, N'kg', 14500),
(19, 20, 310, N'kg', 15500),
(20, 20, 270, N'kg', 14800),
(20, 1, 320, N'kg', 16000);

INSERT INTO ProvisionalReceipts
    (ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, QCStatus, Notes)
VALUES
(1, 'PR001', '2026-01-04 08:00:00', 2, 'Not Approved', 'Passed', N'Hàng được tiếp nhận và kiểm tra'),
(2, 'PR002', '2026-01-05 08:00:00', 3, 'Not Approved', 'Passed', N'Hàng được tiếp nhận và kiểm tra'),
(3, 'PR003', '2026-01-06 08:00:00', 4, 'Not Approved', 'Passed', N'Hàng không đạt yêu cầu chất lượng'),
(4, 'PR004', '2026-01-07 08:00:00', 1, 'Not Approved', 'Passed', N'Hàng được tiếp nhận và kiểm tra'),
(5, 'PR005', '2026-01-08 08:00:00', 2, 'Not Approved', 'Passed', N'Hàng được tiếp nhận và kiểm tra'),
(6, 'PR006', '2026-01-09 08:00:00', 3, 'Not Approved', 'Passed', N'Hàng được tiếp nhận và kiểm tra'),
(7, 'PR007', '2026-01-10 08:00:00', 4, 'Not Approved', 'Failed', N'Hàng không đạt yêu cầu chất lượng'),
(8, 'PR008', '2026-01-11 08:00:00', 1, 'Not Approved', 'Failed', N'Hàng được tiếp nhận và kiểm tra'),
(9, 'PR009', '2026-01-12 08:00:00', 2, 'Not Approved', 'Failed', N'Hàng được tiếp nhận và kiểm tra'),
(10, 'PR010', '2026-01-13 08:00:00', 3, 'Not Approved', 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(11, 'PR011', '2026-01-14 08:00:00', 4, 'Not Approved', 'Pending', N'Hàng không đạt yêu cầu chất lượng'),
(12, 'PR012', '2026-01-15 08:00:00', 1, 'Not Approved', 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(13, 'PR013', '2026-01-16 08:00:00', 2, 'Not Approved', 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(14, 'PR014', '2026-01-17 08:00:00', 3, 'Not Approved', 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(15, 'PR015', '2026-01-18 08:00:00', 4, 'Not Approved', 'Pending', N'Hàng không đạt yêu cầu chất lượng'),
(16, 'PR016', '2026-01-19 08:00:00', 1, 'Not Approved', 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(17, 'PR017', '2026-01-20 08:00:00', 2, 'Not Approved', 'Pending', N'Hàng đang chờ kiểm tra chất lượng'),
(18, 'PR018', '2026-01-21 08:00:00', 3, 'Not Approved', 'Pending', N'Hàng đang chờ kiểm tra chất lượng'),
(19, 'PR019', '2026-01-22 08:00:00', 4, 'Not Approved', 'Pending', N'Hàng đang chờ kiểm tra chất lượng'),
(20, 'PR020', '2026-01-23 08:00:00', 1, 'Not Approved', 'Pending', N'Hàng đang chờ kiểm tra chất lượng');

INSERT INTO ProvisionalReceiptDetails (ProvisionalReceiptId, ProductId, Quantity, Unit)
VALUES
(1, 1, 150, N'kg'),
(1, 2, 200, N'kg'),
(2, 2, 160, N'kg'),
(2, 3, 210, N'kg'),
(3, 3, 170, N'kg'),
(3, 4, 220, N'kg'),
(4, 4, 180, N'kg'),
(4, 5, 230, N'kg'),
(5, 5, 190, N'kg'),
(5, 6, 240, N'kg'),
(6, 6, 200, N'kg'),
(6, 7, 250, N'kg'),
(7, 7, 150, N'kg'),
(7, 8, 200, N'kg'),
(8, 8, 160, N'kg'),
(8, 9, 210, N'kg'),
(9, 9, 170, N'kg'),
(9, 10, 220, N'kg'),
(10, 10, 180, N'kg'),
(10, 11, 230, N'kg'),
(11, 11, 190, N'kg'),
(11, 12, 240, N'kg'),
(12, 12, 200, N'kg'),
(12, 13, 250, N'kg'),
(13, 13, 150, N'kg'),
(13, 14, 200, N'kg'),
(14, 14, 160, N'kg'),
(14, 15, 210, N'kg'),
(15, 15, 170, N'kg'),
(15, 16, 220, N'kg'),
(16, 16, 180, N'kg'),
(16, 17, 230, N'kg'),
(17, 17, 190, N'kg'),
(17, 18, 240, N'kg'),
(18, 18, 200, N'kg'),
(18, 19, 250, N'kg'),
(19, 19, 210, N'kg'),
(19, 20, 260, N'kg'),
(20, 20, 220, N'kg'),
(20, 1, 270, N'kg');

INSERT INTO ProductDetailGrades (ProvisionalDetailId, GradeType, Quantity)
VALUES
(1, 'A', 100.000), (1, 'B', 40.000), (1, 'Damaged', 10.000),
(2, 'A', 135.000), (2, 'B', 53.000), (2, 'Damaged', 12.000),
(3, 'A', 105.000), (3, 'B', 48.000), (3, 'Damaged', 7.000),
(4, 'A', 140.000), (4, 'B', 61.000), (4, 'Damaged', 9.000),
(5, 'A', 112.000), (5, 'B', 47.000), (5, 'Damaged', 11.000),
(6, 'A', 150.000), (6, 'B', 62.000), (6, 'Damaged', 8.000),
(7, 'A', 121.000), (7, 'B', 44.000), (7, 'Damaged', 15.000),
(8, 'A', 158.000), (8, 'B', 61.000), (8, 'Damaged', 11.000),
(9, 'A', 131.000), (9, 'B', 45.000), (9, 'Damaged', 14.000),
(10, 'A', 168.000), (10, 'B', 63.000), (10, 'Damaged', 9.000),
(11, 'A', 142.000), (11, 'B', 55.000), (11, 'Damaged', 3.000),
(12, 'A', 181.000), (12, 'B', 63.000), (12, 'Damaged', 6.000),
(13, 'A', 92.000), (13, 'B', 48.000), (13, 'Damaged', 10.000),
(14, 'A', 133.000), (14, 'B', 55.000), (14, 'Damaged', 12.000),
(15, 'A', 103.000), (15, 'B', 49.000), (15, 'Damaged', 8.000),
(16, 'A', 141.000), (16, 'B', 60.000), (16, 'Damaged', 9.000),
(17, 'A', 111.000), (17, 'B', 47.000), (17, 'Damaged', 12.000),
(18, 'A', 151.000), (18, 'B', 61.000), (18, 'Damaged', 8.000),
(19, 'A', 122.000), (19, 'B', 43.000), (19, 'Damaged', 15.000),
(20, 'A', 159.000), (20, 'B', 60.000), (20, 'Damaged', 11.000),
(21, 'A', 132.000), (21, 'B', 45.000), (21, 'Damaged', 13.000),
(22, 'A', 169.000), (22, 'B', 62.000), (22, 'Damaged', 9.000),
(23, 'A', 133.000), (23, 'B', 54.000), (23, 'Damaged', 13.000),
(24, 'A', 182.000), (24, 'B', 62.000), (24, 'Damaged', 6.000),
(25, 'A', 93.000), (25, 'B', 47.000), (25, 'Damaged', 10.000),
(26, 'A', 134.000), (26, 'B', 54.000), (26, 'Damaged', 12.000),
(27, 'A', 104.000), (27, 'B', 48.000), (27, 'Damaged', 8.000),
(28, 'A', 142.000), (28, 'B', 59.000), (28, 'Damaged', 9.000),
(29, 'A', 112.000), (29, 'B', 46.000), (29, 'Damaged', 12.000),
(30, 'A', 152.000), (30, 'B', 60.000), (30, 'Damaged', 8.000),
(31, 'A', 123.000), (31, 'B', 42.000), (31, 'Damaged', 15.000),
(32, 'A', 160.000), (32, 'B', 59.000), (32, 'Damaged', 11.000),
(33, 'A', 133.000), (33, 'B', 44.000), (33, 'Damaged', 13.000),
(34, 'A', 170.000), (34, 'B', 61.000), (34, 'Damaged', 9.000),
(35, 'A', 143.000), (35, 'B', 54.000), (35, 'Damaged', 13.000),
(36, 'A', 192.000), (36, 'B', 62.000), (36, 'Damaged', 6.000),
(37, 'A', 153.000), (37, 'B', 54.000), (37, 'Damaged', 13.000),
(38, 'A', 202.000), (38, 'B', 62.000), (38, 'Damaged', 6.000),
(39, 'A', 143.000), (39, 'B', 54.000), (39, 'Damaged', 13.000),
(40, 'A', 192.000), (40, 'B', 62.000), (40, 'Damaged', 6.000);

INSERT INTO InventoryBatches (ProductId, LocationId, BatchCode, ReceivedDate, ExpiryDate, InitialQty, CurrentQty, ReservedQty, AvailableQty, UnitCost, QualityGrade, Status)
VALUES
(1, 1, 'BATCH001', '2026-01-04 08:00:00', '2026-01-10 08:00:00', 310, 300, 10, 290, 10400, 'A', 'Available'),
(2, 2, 'BATCH002', '2026-01-05 08:00:00', '2026-01-12 08:00:00', 320, 300, 20, 280, 10800, 'B', 'Available'),
(3, 3, 'BATCH003', '2026-01-06 08:00:00', '2026-01-14 08:00:00', 330, 300, 30, 270, 11200, 'A', 'Available'),
(4, 4, 'BATCH004', '2026-01-07 08:00:00', '2026-01-16 08:00:00', 340, 300, 0, 300, 11600, 'B', 'Available'),
(5, 5, 'BATCH005', '2026-01-08 08:00:00', '2026-01-18 08:00:00', 350, 300, 10, 290, 12000, 'A', 'Available');