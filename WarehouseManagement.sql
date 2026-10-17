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
    PasswordHash VARCHAR(255) NOT NULL,
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
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
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
    BatchId INT NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    FOREIGN KEY (ReturnId) REFERENCES SupplierReturns(ReturnId),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId)
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
    BatchId INT NOT NULL,
    InspectedBy INT NOT NULL,
    InspectionDate DATETIME NOT NULL,
    Grade VARCHAR(20) NOT NULL,
    Result VARCHAR(20) NOT NULL,
    Notes NVARCHAR(500),
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId),
    FOREIGN KEY (InspectedBy) REFERENCES Users(UserId)
);

CREATE TABLE LossReports (
    LossReportId INT IDENTITY(1,1) PRIMARY KEY,
    BatchId INT NOT NULL,
    ReportedBy INT NOT NULL,
    LossType VARCHAR(30) NOT NULL,
    Quantity DECIMAL(18,3) NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    ReportedDate DATETIME NOT NULL,
    FOREIGN KEY (BatchId) REFERENCES InventoryBatches(BatchId),
    FOREIGN KEY (ReportedBy) REFERENCES Users(UserId)
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

INSERT INTO Users (RoleId, Username, PasswordHash, FullName, Email, IsActive)
VALUES
(1, 'admin01', 'hash_admin_01', N'Nguyen Van An', 'admin@vfw-wms.com', 1),
(2, 'manager01', 'hash_manager_01', N'Tran Thi Mai', 'manager@vfw-wms.com', 1),
(3, 'warehouse01', 'hash_warehouse_01', N'Le Van Nam', 'warehouse01@vfw-wms.com', 1),
(4, 'purchase01', 'hash_purchase_01', N'Nguyen Thi Hoa', 'purchase@vfw-wms.com', 1),
(5, 'sale01', 'hash_sale_01', N'Tran Van Minh', 'sale@vfw-wms.com', 1);

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
(1, 1, 'PO001', '2026-01-03 08:00:00', '2026-01-05 08:00:00', 'Completed'),
(2, 2, 'PO002', '2026-01-04 08:00:00', '2026-01-06 08:00:00', 'Completed'),
(3, 3, 'PO003', '2026-01-05 08:00:00', '2026-01-07 08:00:00', 'Completed'),
(4, 4, 'PO004', '2026-01-06 08:00:00', '2026-01-08 08:00:00', 'Completed'),
(5, 5, 'PO005', '2026-01-07 08:00:00', '2026-01-09 08:00:00', 'Completed'),
(6, 1, 'PO006', '2026-01-08 08:00:00', '2026-01-10 08:00:00', 'Completed'),
(7, 2, 'PO007', '2026-01-09 08:00:00', '2026-01-11 08:00:00', 'Completed'),
(8, 3, 'PO008', '2026-01-10 08:00:00', '2026-01-12 08:00:00', 'Completed'),
(9, 4, 'PO009', '2026-01-11 08:00:00', '2026-01-13 08:00:00', 'Completed'),
(10, 5, 'PO010', '2026-01-12 08:00:00', '2026-01-14 08:00:00', 'Completed'),
(11, 1, 'PO011', '2026-01-13 08:00:00', '2026-01-15 08:00:00', 'Completed'),
(12, 2, 'PO012', '2026-01-14 08:00:00', '2026-01-16 08:00:00', 'Completed'),
(13, 3, 'PO013', '2026-01-15 08:00:00', '2026-01-17 08:00:00', 'Completed'),
(14, 4, 'PO014', '2026-01-16 08:00:00', '2026-01-18 08:00:00', 'Completed'),
(15, 5, 'PO015', '2026-01-17 08:00:00', '2026-01-19 08:00:00', 'Completed'),
(16, 1, 'PO016', '2026-01-18 08:00:00', '2026-01-20 08:00:00', 'Pending'),
(17, 2, 'PO017', '2026-01-19 08:00:00', '2026-01-21 08:00:00', 'Pending'),
(18, 3, 'PO018', '2026-01-20 08:00:00', '2026-01-22 08:00:00', 'Pending'),
(19, 4, 'PO019', '2026-01-21 08:00:00', '2026-01-23 08:00:00', 'Pending'),
(20, 5, 'PO020', '2026-01-22 08:00:00', '2026-01-24 08:00:00', 'Pending');

INSERT INTO ImportOrderDetails (ImportOrderId, ProductId, Quantity, Unit, UnitPrice)
VALUES
(1, 1, 110, N'kg', 10500),
(2, 2, 120, N'kg', 11000),
(3, 3, 130, N'kg', 11500),
(4, 4, 140, N'kg', 12000),
(5, 5, 150, N'kg', 12500),
(6, 6, 160, N'kg', 13000),
(7, 7, 170, N'kg', 13500),
(8, 8, 180, N'kg', 14000),
(9, 9, 190, N'kg', 14500),
(10, 10, 200, N'kg', 15000),
(11, 11, 210, N'kg', 15500),
(12, 12, 220, N'kg', 16000),
(13, 13, 230, N'kg', 16500),
(14, 14, 240, N'kg', 17000),
(15, 15, 250, N'kg', 17500),
(16, 16, 260, N'kg', 18000),
(17, 17, 270, N'kg', 18500),
(18, 18, 280, N'kg', 19000),
(19, 19, 290, N'kg', 19500),
(20, 20, 300, N'kg', 20000),
(1, 6, 185, N'kg', 18300),
(2, 7, 190, N'kg', 18600),
(3, 8, 195, N'kg', 18900),
(4, 9, 200, N'kg', 19200),
(5, 10, 205, N'kg', 19500),
(6, 11, 210, N'kg', 19800),
(7, 12, 215, N'kg', 20100),
(8, 13, 220, N'kg', 20400),
(9, 14, 225, N'kg', 20700),
(10, 15, 230, N'kg', 21000),
(11, 16, 235, N'kg', 21300),
(12, 17, 240, N'kg', 21600),
(13, 18, 245, N'kg', 21900),
(14, 19, 250, N'kg', 22200),
(15, 20, 255, N'kg', 22500),
(16, 1, 260, N'kg', 22800),
(17, 2, 265, N'kg', 23100),
(18, 3, 270, N'kg', 23400),
(19, 4, 275, N'kg', 23700),
(20, 5, 280, N'kg', 24000);

INSERT INTO ProvisionalReceipts (ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, Notes)
VALUES
(1, 'PR001', '2026-01-04 08:00:00', 2, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(2, 'PR002', '2026-01-05 08:00:00', 3, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(3, 'PR003', '2026-01-06 08:00:00', 4, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(4, 'PR004', '2026-01-07 08:00:00', 1, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(5, 'PR005', '2026-01-08 08:00:00', 2, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(6, 'PR006', '2026-01-09 08:00:00', 3, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(7, 'PR007', '2026-01-10 08:00:00', 4, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(8, 'PR008', '2026-01-11 08:00:00', 1, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(9, 'PR009', '2026-01-12 08:00:00', 2, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(10, 'PR010', '2026-01-13 08:00:00', 3, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(11, 'PR011', '2026-01-14 08:00:00', 4, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(12, 'PR012', '2026-01-15 08:00:00', 1, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(13, 'PR013', '2026-01-16 08:00:00', 2, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(14, 'PR014', '2026-01-17 08:00:00', 3, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(15, 'PR015', '2026-01-18 08:00:00', 4, 'Completed', N'Hàng được tiếp nhận và kiểm tra'),
(16, 'PR016', '2026-01-19 08:00:00', 1, 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(17, 'PR017', '2026-01-20 08:00:00', 2, 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(18, 'PR018', '2026-01-21 08:00:00', 3, 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(19, 'PR019', '2026-01-22 08:00:00', 4, 'Pending', N'Hàng được tiếp nhận và kiểm tra'),
(20, 'PR020', '2026-01-23 08:00:00', 1, 'Pending', N'Hàng được tiếp nhận và kiểm tra');

INSERT INTO ProvisionalReceiptDetails (ProvisionalReceiptId, ProductId, Quantity, Unit)
VALUES
(1, 1, 103, N'kg'),
(2, 2, 106, N'kg'),
(3, 3, 109, N'kg'),
(4, 4, 112, N'kg'),
(5, 5, 115, N'kg'),
(6, 6, 118, N'kg'),
(7, 7, 121, N'kg'),
(8, 8, 124, N'kg'),
(9, 9, 127, N'kg'),
(10, 10, 130, N'kg'),
(11, 11, 133, N'kg'),
(12, 12, 136, N'kg'),
(13, 13, 139, N'kg'),
(14, 14, 142, N'kg'),
(15, 15, 145, N'kg'),
(16, 16, 148, N'kg'),
(17, 17, 151, N'kg'),
(18, 18, 154, N'kg'),
(19, 19, 157, N'kg'),
(20, 20, 160, N'kg'),
(1, 1, 163, N'kg'),
(2, 2, 166, N'kg'),
(3, 3, 169, N'kg'),
(4, 4, 172, N'kg'),
(5, 5, 175, N'kg'),
(6, 6, 178, N'kg'),
(7, 7, 181, N'kg'),
(8, 8, 184, N'kg'),
(9, 9, 187, N'kg'),
(10, 10, 190, N'kg'),
(11, 11, 193, N'kg'),
(12, 12, 196, N'kg'),
(13, 13, 199, N'kg'),
(14, 14, 202, N'kg'),
(15, 15, 205, N'kg'),
(16, 16, 208, N'kg'),
(17, 17, 211, N'kg'),
(18, 18, 214, N'kg'),
(19, 19, 217, N'kg'),
(20, 20, 220, N'kg');

INSERT INTO InventoryBatches (ProductId, LocationId, BatchCode, ReceivedDate, ExpiryDate, InitialQty, CurrentQty, ReservedQty, AvailableQty, UnitCost, QualityGrade, Status)
VALUES
(1, 1, 'BATCH001', '2026-01-04 08:00:00', '2026-01-10 08:00:00', 310, 300, 10, 290, 10400, 'A', 'Available'),
(2, 2, 'BATCH002', '2026-01-05 08:00:00', '2026-01-12 08:00:00', 320, 300, 20, 280, 10800, 'B', 'Available'),
(3, 3, 'BATCH003', '2026-01-06 08:00:00', '2026-01-14 08:00:00', 330, 300, 30, 270, 11200, 'A', 'Available'),
(4, 4, 'BATCH004', '2026-01-07 08:00:00', '2026-01-16 08:00:00', 340, 300, 0, 300, 11600, 'B', 'Available'),
(5, 5, 'BATCH005', '2026-01-08 08:00:00', '2026-01-18 08:00:00', 350, 300, 10, 290, 12000, 'A', 'Available'),
(6, 6, 'BATCH006', '2026-01-09 08:00:00', '2026-01-20 08:00:00', 360, 360, 20, 340, 12400, 'A', 'Available'),
(7, 7, 'BATCH007', '2026-01-10 08:00:00', '2026-01-22 08:00:00', 370, 360, 30, 330, 12800, 'B', 'Available'),
(8, 8, 'BATCH008', '2026-01-11 08:00:00', '2026-01-16 08:00:00', 380, 360, 0, 360, 13200, 'A', 'Available'),
(9, 9, 'BATCH009', '2026-01-12 08:00:00', '2026-01-18 08:00:00', 390, 360, 10, 350, 13600, 'B', 'Available'),
(10, 10, 'BATCH010', '2026-01-13 08:00:00', '2026-01-20 08:00:00', 400, 360, 20, 340, 14000, 'A', 'Available'),
(11, 11, 'BATCH011', '2026-01-14 08:00:00', '2026-01-22 08:00:00', 410, 360, 30, 330, 14400, 'A', 'Available'),
(12, 12, 'BATCH012', '2026-01-15 08:00:00', '2026-01-24 08:00:00', 420, 420, 0, 420, 14800, 'B', 'Available'),
(13, 13, 'BATCH013', '2026-01-16 08:00:00', '2026-01-26 08:00:00', 430, 420, 10, 410, 15200, 'A', 'Available'),
(14, 14, 'BATCH014', '2026-01-17 08:00:00', '2026-01-28 08:00:00', 440, 420, 20, 400, 15600, 'B', 'Available'),
(15, 15, 'BATCH015', '2026-01-18 08:00:00', '2026-01-30 08:00:00', 450, 420, 30, 390, 16000, 'A', 'Available'),
(16, 16, 'BATCH016', '2026-01-19 08:00:00', '2026-01-24 08:00:00', 460, 420, 0, 420, 16400, 'A', 'Available'),
(17, 17, 'BATCH017', '2026-01-20 08:00:00', '2026-01-26 08:00:00', 470, 420, 10, 410, 16800, 'B', 'Available'),
(18, 18, 'BATCH018', '2026-01-21 08:00:00', '2026-01-28 08:00:00', 480, 480, 20, 460, 17200, 'A', 'Available'),
(19, 19, 'BATCH019', '2026-01-22 08:00:00', '2026-01-30 08:00:00', 490, 480, 30, 450, 17600, 'B', 'Quarantine'),
(20, 20, 'BATCH020', '2026-01-23 08:00:00', '2026-02-01 08:00:00', 500, 480, 0, 480, 18000, 'A', 'Expired');

INSERT INTO GoodsReceipts (ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, Notes)
VALUES
(1, 'GRN001', '2026-01-04 09:00:00', 3, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(2, 'GRN002', '2026-01-05 09:00:00', 4, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(3, 'GRN003', '2026-01-06 09:00:00', 1, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(4, 'GRN004', '2026-01-07 09:00:00', 2, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(5, 'GRN005', '2026-01-08 09:00:00', 3, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(6, 'GRN006', '2026-01-09 09:00:00', 4, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(7, 'GRN007', '2026-01-10 09:00:00', 1, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(8, 'GRN008', '2026-01-11 09:00:00', 2, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(9, 'GRN009', '2026-01-12 09:00:00', 3, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(10, 'GRN010', '2026-01-13 09:00:00', 4, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(11, 'GRN011', '2026-01-14 09:00:00', 1, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(12, 'GRN012', '2026-01-15 09:00:00', 2, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(13, 'GRN013', '2026-01-16 09:00:00', 3, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(14, 'GRN014', '2026-01-17 09:00:00', 4, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(15, 'GRN015', '2026-01-18 09:00:00', 1, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(16, 'GRN016', '2026-01-19 09:00:00', 2, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(17, 'GRN017', '2026-01-20 09:00:00', 3, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(18, 'GRN018', '2026-01-21 09:00:00', 4, 'Completed', N'Đã đối chiếu với phiếu nhập'),
(19, 'GRN019', '2026-01-22 09:00:00', 1, 'Pending', N'Đã đối chiếu với phiếu nhập'),
(20, 'GRN020', '2026-01-23 09:00:00', 2, 'Pending', N'Đã đối chiếu với phiếu nhập');

INSERT INTO GoodsReceiptDetails (ReceiptId, ProductId, BatchId, Quantity, Unit, UnitCost)
VALUES
(1, 1, 1, 84, N'kg', 10850),
(2, 2, 2, 88, N'kg', 11200),
(3, 3, 3, 92, N'kg', 11550),
(4, 4, 4, 96, N'kg', 11900),
(5, 5, 5, 100, N'kg', 12250),
(6, 6, 6, 104, N'kg', 12600),
(7, 7, 7, 108, N'kg', 12950),
(8, 8, 8, 112, N'kg', 13300),
(9, 9, 9, 116, N'kg', 13650),
(10, 10, 10, 120, N'kg', 14000),
(11, 11, 11, 124, N'kg', 14350),
(12, 12, 12, 128, N'kg', 14700),
(13, 13, 13, 132, N'kg', 15050),
(14, 14, 14, 136, N'kg', 15400),
(15, 15, 15, 140, N'kg', 15750),
(16, 16, 16, 144, N'kg', 16100),
(17, 17, 17, 148, N'kg', 16450),
(18, 18, 18, 152, N'kg', 16800),
(19, 19, 19, 156, N'kg', 17150),
(20, 20, 20, 160, N'kg', 17500),
(1, 1, 1, 164, N'kg', 17850),
(2, 2, 2, 168, N'kg', 18200),
(3, 3, 3, 172, N'kg', 18550),
(4, 4, 4, 176, N'kg', 18900),
(5, 5, 5, 180, N'kg', 19250),
(6, 6, 6, 184, N'kg', 19600),
(7, 7, 7, 188, N'kg', 19950),
(8, 8, 8, 192, N'kg', 20300),
(9, 9, 9, 196, N'kg', 20650),
(10, 10, 10, 200, N'kg', 21000),
(11, 11, 11, 204, N'kg', 21350),
(12, 12, 12, 208, N'kg', 21700),
(13, 13, 13, 212, N'kg', 22050),
(14, 14, 14, 216, N'kg', 22400),
(15, 15, 15, 220, N'kg', 22750),
(16, 16, 16, 224, N'kg', 23100),
(17, 17, 17, 228, N'kg', 23450),
(18, 18, 18, 232, N'kg', 23800),
(19, 19, 19, 236, N'kg', 24150),
(20, 20, 20, 240, N'kg', 24500);

INSERT INTO SupplierReturns (SupplierId, ImportOrderId, ReturnNumber, ReturnDate, Reason, Status, CreatedBy)
VALUES
(1, 1, 'RET001', '2026-01-07 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 1),
(2, 2, 'RET002', '2026-01-08 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 2),
(3, 3, 'RET003', '2026-01-09 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 3),
(4, 4, 'RET004', '2026-01-10 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 4),
(5, 5, 'RET005', '2026-01-11 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 1),
(6, 6, 'RET006', '2026-01-12 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 2),
(7, 7, 'RET007', '2026-01-13 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Completed', 3),
(8, 8, 'RET008', '2026-01-14 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Pending', 4),
(9, 9, 'RET009', '2026-01-15 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Pending', 1),
(10, 10, 'RET010', '2026-01-16 08:00:00', N'Hàng không đạt yêu cầu chất lượng', 'Pending', 2);

INSERT INTO SupplierReturnDetails (ReturnId, BatchId, Quantity, Reason)
VALUES
(1, 1, 6, N'Hàng bị dập hoặc không đạt chuẩn'),
(2, 2, 7, N'Hàng bị dập hoặc không đạt chuẩn'),
(3, 3, 8, N'Hàng bị dập hoặc không đạt chuẩn'),
(4, 4, 9, N'Hàng bị dập hoặc không đạt chuẩn'),
(5, 5, 10, N'Hàng bị dập hoặc không đạt chuẩn'),
(6, 6, 11, N'Hàng bị dập hoặc không đạt chuẩn'),
(7, 7, 12, N'Hàng bị dập hoặc không đạt chuẩn'),
(8, 8, 13, N'Hàng bị dập hoặc không đạt chuẩn'),
(9, 9, 14, N'Hàng bị dập hoặc không đạt chuẩn'),
(10, 10, 15, N'Hàng bị dập hoặc không đạt chuẩn');

INSERT INTO ExportOrders (CustomerId, OrderNumber, OrderDate, Status, CreatedBy)
VALUES
(1, 'SO001', '2026-01-09 08:00:00', 'Completed', 2),
(2, 'SO002', '2026-01-10 08:00:00', 'Completed', 3),
(3, 'SO003', '2026-01-11 08:00:00', 'Completed', 4),
(4, 'SO004', '2026-01-12 08:00:00', 'Completed', 5),
(5, 'SO005', '2026-01-13 08:00:00', 'Completed', 1),
(6, 'SO006', '2026-01-14 08:00:00', 'Completed', 2),
(7, 'SO007', '2026-01-15 08:00:00', 'Completed', 3),
(8, 'SO008', '2026-01-16 08:00:00', 'Completed', 4),
(9, 'SO009', '2026-01-17 08:00:00', 'Completed', 5),
(10, 'SO010', '2026-01-18 08:00:00', 'Completed', 1),
(11, 'SO011', '2026-01-19 08:00:00', 'Completed', 2),
(12, 'SO012', '2026-01-20 08:00:00', 'Completed', 3),
(13, 'SO013', '2026-01-21 08:00:00', 'Completed', 4),
(14, 'SO014', '2026-01-22 08:00:00', 'Completed', 5),
(15, 'SO015', '2026-01-23 08:00:00', 'Pending', 1),
(16, 'SO016', '2026-01-24 08:00:00', 'Pending', 2),
(17, 'SO017', '2026-01-25 08:00:00', 'Pending', 3),
(18, 'SO018', '2026-01-26 08:00:00', 'Pending', 4),
(19, 'SO019', '2026-01-27 08:00:00', 'Pending', 5),
(20, 'SO020', '2026-01-28 08:00:00', 'Pending', 1);

INSERT INTO ExportOrderDetails (ExportOrderId, ProductId, Quantity, Unit, UnitPrice)
VALUES
(1, 9, 32, N'kg', 15500),
(2, 10, 34, N'kg', 16000),
(3, 11, 36, N'kg', 16500),
(4, 12, 38, N'kg', 17000),
(5, 13, 40, N'kg', 17500),
(6, 14, 42, N'kg', 18000),
(7, 15, 44, N'kg', 18500),
(8, 16, 46, N'kg', 19000),
(9, 17, 48, N'kg', 19500),
(10, 18, 50, N'kg', 20000),
(11, 19, 52, N'kg', 20500),
(12, 20, 54, N'kg', 21000),
(13, 1, 56, N'kg', 21500),
(14, 2, 58, N'kg', 22000),
(15, 3, 60, N'kg', 22500),
(16, 4, 62, N'kg', 23000),
(17, 5, 64, N'kg', 23500),
(18, 6, 66, N'kg', 24000),
(19, 7, 68, N'kg', 24500),
(20, 8, 70, N'kg', 25000),
(1, 9, 72, N'kg', 25500),
(2, 10, 74, N'kg', 26000),
(3, 11, 76, N'kg', 26500),
(4, 12, 78, N'kg', 27000),
(5, 13, 80, N'kg', 27500),
(6, 14, 82, N'kg', 28000),
(7, 15, 84, N'kg', 28500),
(8, 16, 86, N'kg', 29000),
(9, 17, 88, N'kg', 29500),
(10, 18, 90, N'kg', 30000),
(11, 19, 92, N'kg', 30500),
(12, 20, 94, N'kg', 31000),
(13, 1, 96, N'kg', 31500),
(14, 2, 98, N'kg', 32000),
(15, 3, 100, N'kg', 32500),
(16, 4, 102, N'kg', 33000),
(17, 5, 104, N'kg', 33500),
(18, 6, 106, N'kg', 34000),
(19, 7, 108, N'kg', 34500),
(20, 8, 110, N'kg', 35000);

INSERT INTO StockAllocations (ExportDetailId, BatchId, Quantity, AllocatedDate, Status)
VALUES
(1, 1, 21, '2026-01-11 08:00:00', 'Allocated'),
(2, 2, 22, '2026-01-12 08:00:00', 'Allocated'),
(3, 3, 23, '2026-01-13 08:00:00', 'Allocated'),
(4, 4, 24, '2026-01-14 08:00:00', 'Allocated'),
(5, 5, 25, '2026-01-15 08:00:00', 'Allocated'),
(6, 6, 26, '2026-01-16 08:00:00', 'Allocated'),
(7, 7, 27, '2026-01-17 08:00:00', 'Allocated'),
(8, 8, 28, '2026-01-18 08:00:00', 'Allocated'),
(9, 9, 29, '2026-01-19 08:00:00', 'Allocated'),
(10, 10, 30, '2026-01-20 08:00:00', 'Allocated'),
(11, 11, 31, '2026-01-21 08:00:00', 'Allocated'),
(12, 12, 32, '2026-01-22 08:00:00', 'Allocated'),
(13, 13, 33, '2026-01-23 08:00:00', 'Allocated'),
(14, 14, 34, '2026-01-24 08:00:00', 'Allocated'),
(15, 15, 35, '2026-01-25 08:00:00', 'Allocated'),
(16, 16, 36, '2026-01-26 08:00:00', 'Pending'),
(17, 17, 37, '2026-01-27 08:00:00', 'Pending'),
(18, 18, 38, '2026-01-28 08:00:00', 'Pending'),
(19, 19, 39, '2026-01-29 08:00:00', 'Pending'),
(20, 20, 40, '2026-01-30 08:00:00', 'Pending');

INSERT INTO StockMovements (BatchId, FromLocationId, ToLocationId, MovedBy, Quantity, MovementDate, Reason)
VALUES
(1, 1, 2, 3, 11, '2026-01-12 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(2, 2, 3, 4, 12, '2026-01-13 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(3, 3, 4, 1, 13, '2026-01-14 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(4, 4, 5, 2, 14, '2026-01-15 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(5, 5, 6, 3, 15, '2026-01-16 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(6, 6, 7, 4, 16, '2026-01-17 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(7, 7, 8, 1, 17, '2026-01-18 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(8, 8, 9, 2, 18, '2026-01-19 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(9, 9, 10, 3, 19, '2026-01-20 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(10, 10, 11, 4, 20, '2026-01-21 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(11, 11, 12, 1, 21, '2026-01-22 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(12, 12, 13, 2, 22, '2026-01-23 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(13, 13, 14, 3, 23, '2026-01-24 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(14, 14, 15, 4, 24, '2026-01-25 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(15, 15, 16, 1, 25, '2026-01-26 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(16, 16, 17, 2, 26, '2026-01-27 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(17, 17, 18, 3, 27, '2026-01-28 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(18, 18, 19, 4, 28, '2026-01-29 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(19, 19, 20, 1, 29, '2026-01-30 08:00:00', N'Điều chuyển hàng giữa các vị trí'),
(20, 20, 1, 2, 30, '2026-01-31 08:00:00', N'Điều chuyển hàng giữa các vị trí');

INSERT INTO QualityInspections (BatchId, InspectedBy, InspectionDate, Grade, Result, Notes)
VALUES
(1, 3, '2026-01-05 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(2, 4, '2026-01-06 08:00:00', 'B', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(3, 1, '2026-01-07 08:00:00', 'C', 'Failed', N'Kết quả kiểm tra chất lượng lô hàng'),
(4, 2, '2026-01-08 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(5, 3, '2026-01-09 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(6, 4, '2026-01-10 08:00:00', 'B', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(7, 1, '2026-01-11 08:00:00', 'C', 'Failed', N'Kết quả kiểm tra chất lượng lô hàng'),
(8, 2, '2026-01-12 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(9, 3, '2026-01-13 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(10, 4, '2026-01-14 08:00:00', 'B', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(11, 1, '2026-01-15 08:00:00', 'C', 'Failed', N'Kết quả kiểm tra chất lượng lô hàng'),
(12, 2, '2026-01-16 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(13, 3, '2026-01-17 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(14, 4, '2026-01-18 08:00:00', 'B', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(15, 1, '2026-01-19 08:00:00', 'C', 'Failed', N'Kết quả kiểm tra chất lượng lô hàng'),
(16, 2, '2026-01-20 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(17, 3, '2026-01-21 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(18, 4, '2026-01-22 08:00:00', 'B', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng'),
(19, 1, '2026-01-23 08:00:00', 'C', 'Failed', N'Kết quả kiểm tra chất lượng lô hàng'),
(20, 2, '2026-01-24 08:00:00', 'A', 'Passed', N'Kết quả kiểm tra chất lượng lô hàng');

INSERT INTO LossReports (BatchId, ReportedBy, LossType, Quantity, Reason, Status, ReportedDate)
VALUES
(1, 4, 'Spoilage', 3, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-14 08:00:00'),
(2, 1, 'Loss', 4, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-15 08:00:00'),
(3, 2, 'Damage', 5, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-16 08:00:00'),
(4, 3, 'Spoilage', 6, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-17 08:00:00'),
(5, 4, 'Loss', 2, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-18 08:00:00'),
(6, 1, 'Damage', 3, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-19 08:00:00'),
(7, 2, 'Spoilage', 4, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-20 08:00:00'),
(8, 3, 'Loss', 5, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-21 08:00:00'),
(9, 4, 'Damage', 6, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-22 08:00:00'),
(10, 1, 'Spoilage', 2, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-23 08:00:00'),
(11, 2, 'Loss', 3, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-24 08:00:00'),
(12, 3, 'Damage', 4, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-25 08:00:00'),
(13, 4, 'Spoilage', 5, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-26 08:00:00'),
(14, 1, 'Loss', 6, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Approved', '2026-01-27 08:00:00'),
(15, 2, 'Damage', 2, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Pending', '2026-01-28 08:00:00'),
(16, 3, 'Spoilage', 3, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Pending', '2026-01-29 08:00:00'),
(17, 4, 'Loss', 4, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Pending', '2026-01-30 08:00:00'),
(18, 1, 'Damage', 5, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Pending', '2026-01-31 08:00:00'),
(19, 2, 'Spoilage', 6, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Pending', '2026-02-01 08:00:00'),
(20, 3, 'Loss', 2, N'Ghi nhận hao hụt trong quá trình bảo quản', 'Pending', '2026-02-02 08:00:00');

INSERT INTO StockCounts (CountedBy, CountDate, Status, Notes)
VALUES
(3, '2026-01-16 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(4, '2026-01-17 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(1, '2026-01-18 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(2, '2026-01-19 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(3, '2026-01-20 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(4, '2026-01-21 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(1, '2026-01-22 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(2, '2026-01-23 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(3, '2026-01-24 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(4, '2026-01-25 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(1, '2026-01-26 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(2, '2026-01-27 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(3, '2026-01-28 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(4, '2026-01-29 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(1, '2026-01-30 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(2, '2026-01-31 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(3, '2026-02-01 08:00:00', 'Completed', N'Kiểm kê định kỳ kho'),
(4, '2026-02-02 08:00:00', 'Pending', N'Kiểm kê định kỳ kho'),
(1, '2026-02-03 08:00:00', 'Pending', N'Kiểm kê định kỳ kho'),
(2, '2026-02-04 08:00:00', 'Pending', N'Kiểm kê định kỳ kho');

INSERT INTO StockCountDetails (CountId, BatchId, SystemQty, CountedQty, Difference)
VALUES
(1, 1, 251, 250, -1),
(2, 2, 252, 250, -2),
(3, 3, 253, 250, -3),
(4, 4, 254, 254, 0),
(5, 5, 255, 254, -1),
(6, 6, 256, 254, -2),
(7, 7, 257, 254, -3),
(8, 8, 258, 258, 0),
(9, 9, 259, 258, -1),
(10, 10, 260, 258, -2),
(11, 11, 261, 258, -3),
(12, 12, 262, 262, 0),
(13, 13, 263, 262, -1),
(14, 14, 264, 262, -2),
(15, 15, 265, 262, -3),
(16, 16, 266, 266, 0),
(17, 17, 267, 266, -1),
(18, 18, 268, 266, -2),
(19, 19, 269, 266, -3),
(20, 20, 270, 270, 0);

INSERT INTO StockAdjustments (CountId, RequestedBy, ApprovedBy, RequestDate, Reason, Status, ApprovedDate)
VALUES
(1, 3, 2, '2026-01-17 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-17 09:00:00'),
(2, 4, 2, '2026-01-18 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-18 09:00:00'),
(3, 3, 2, '2026-01-19 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-19 09:00:00'),
(4, 4, 2, '2026-01-20 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-20 09:00:00'),
(5, 3, 2, '2026-01-21 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-21 09:00:00'),
(6, 4, 2, '2026-01-22 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-22 09:00:00'),
(7, 3, 2, '2026-01-23 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-23 09:00:00'),
(8, 4, 2, '2026-01-24 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-24 09:00:00'),
(9, 3, 2, '2026-01-25 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-25 09:00:00'),
(10, 4, 2, '2026-01-26 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-26 09:00:00'),
(11, 3, 2, '2026-01-27 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-27 09:00:00'),
(12, 4, 2, '2026-01-28 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-28 09:00:00'),
(13, 3, 2, '2026-01-29 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-29 09:00:00'),
(14, 4, 2, '2026-01-30 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Approved', '2026-01-30 09:00:00'),
(15, 3, NULL, '2026-01-31 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Pending', NULL),
(16, 4, NULL, '2026-02-01 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Pending', NULL),
(17, 3, NULL, '2026-02-02 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Pending', NULL),
(18, 4, NULL, '2026-02-03 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Pending', NULL),
(19, 3, NULL, '2026-02-04 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Pending', NULL),
(20, 4, NULL, '2026-02-05 08:00:00', N'Điều chỉnh theo kết quả kiểm kê thực tế', 'Pending', NULL);

INSERT INTO StockAdjustmentDetails (AdjustmentId, BatchId, SystemQty, ActualQty, QuantityChange)
VALUES
(1, 1, 251, 250, -1),
(2, 2, 252, 250, -2),
(3, 3, 253, 250, -3),
(4, 4, 254, 254, 0),
(5, 5, 255, 254, -1),
(6, 6, 256, 254, -2),
(7, 7, 257, 254, -3),
(8, 8, 258, 258, 0),
(9, 9, 259, 258, -1),
(10, 10, 260, 258, -2),
(11, 11, 261, 258, -3),
(12, 12, 262, 262, 0),
(13, 13, 263, 262, -1),
(14, 14, 264, 262, -2),
(15, 15, 265, 262, -3),
(16, 16, 266, 266, 0),
(17, 17, 267, 266, -1),
(18, 18, 268, 266, -2),
(19, 19, 269, 266, -3),
(20, 20, 270, 270, 0);
