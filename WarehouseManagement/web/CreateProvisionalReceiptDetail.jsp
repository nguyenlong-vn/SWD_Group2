<%--
    Warehouse Staff - Xác nhận thông tin đơn nhập để tạo Provisional Receipt.
    Hiển thị thông tin import order (mã đã nhập) kèm danh sách chi tiết,
    có nút tạo Provisional Receipt.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Provisional Receipt Detail - Warehouse Staff</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <link rel="stylesheet" href="${ctx}/assets/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/feather/css/feather.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/fontawesome/css/fontawesome-all.min.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/datta/datta-icon.css">
    <link rel="stylesheet" href="${ctx}/assets/plugins/jquery-scrollbar/css/perfect-scrollbar.css">
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/pages/pages.css">
    <style>
        body {
            display: block;
            align-items: initial;
            justify-content: initial;
            padding: 0;
        }
        .pcoded-content {
            padding-left: 20px;
            padding-right: 20px;
        }
        .page-header {
            display: flex;
            align-items: flex-start;
            justify-content: flex-start;
            flex-direction: column;
            gap: 16px;
        }
        .page-header-title h4 {
            margin: 0;
        }
        .page-header-breadcrumb .breadcrumb-title {
            display: flex;
            flex-wrap: wrap;
            align-items: flex-start;
            margin: 0;
            padding: 0;
            list-style: none;
        }
    </style>
</head>
<body>
    <div class="pcoded-wrapper">
        <jsp:include page="/include/sidebar.jsp" />
        <div class="pcoded-main-container">
            <jsp:include page="/include/header.jsp" />
            <div class="pcoded-content">
                <div class="pcoded-inner-content">
                    <div class="main-body">
                        <div class="page-wrapper">
                            <div class="page-header">
                                <div class="page-header-title">
                                    <h4>Tạo Provisional Receipt</h4>
                                </div>
                                <div class="page-header-breadcrumb">
                                    <ul class="breadcrumb-title">
                                        <li class="breadcrumb-item"><a href="${ctx}/WarehouseStaffDashboard.jsp">Dashboard</a></li>
                                        <li class="breadcrumb-item"><a href="${ctx}/CreateProvisionalReceipt.jsp">Provisional Receipt</a></li>
                                        <li class="breadcrumb-item">Detail</li>
                                    </ul>
                                </div>
                            </div>
                            <div class="page-body">
                                <c:if test="${not empty provisionalView}">
                                    <form action="${ctx}/ProvisionalController" method="post">
                                        <input type="hidden" name="action" value="createProvisionalReceipt">
                                        <input type="hidden" name="importOrderId" value="${provisionalView.importOrder.importOrderId}">

                                        <div class="card">
                                            <div class="card-header">
                                                <h5>Thông tin đơn nhập</h5>
                                            </div>
                                            <div class="card-block">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <p><strong>Mã đơn nhập:</strong> <c:out value="${provisionalView.importOrder.orderNumber}" /></p>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <p><strong>Nhà cung cấp:</strong>
                                                            <c:out value="${provisionalView.supplierName}" />
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="card">
                                            <div class="card-header">
                                                <h5>Chi tiết đơn nhập</h5>
                                            </div>
                                            <div class="card-block">
                                                <div class="table-responsive">
                                                    <table class="table table-bordered table-hover">
                                                        <thead>
                                                            <tr>
                                                                <th>No</th>
                                                                <th>Mã sản phẩm</th>
                                                                <th>Tên sản phẩm</th>
                                                                <th>Số lượng</th>
                                                                <th>Đơn vị</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="d" items="${provisionalView.orderDetails}" varStatus="s">
                                                                <tr>
                                                                    <td>${s.index + 1}</td>
                                                                    <td>
                                                                        <c:out value="${d.productId}" />
                                                                        <input type="hidden" name="productId" value="${d.productId}">
                                                                    </td>
                                                                    <td><c:out value="${d.productName}" /></td>
                                                                    <td>
                                                                        <c:out value="${d.quantity}" />
                                                                        <input type="hidden" name="quantity" value="${d.quantity}">
                                                                    </td>
                                                                    <td>
                                                                        <c:out value="${d.unit}" />
                                                                        <input type="hidden" name="unit" value="${d.unit}">
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                            <c:if test="${empty provisionalView.orderDetails}">
                                                                <tr>
                                                                    <td colspan="5" class="text-center">Không có chi tiết đơn hàng.</td>
                                                                </tr>
                                                            </c:if>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>

                                        <input type="hidden" name="notes" value="">
                                        <div class="text-right mt-3">
                                            <a href="${ctx}/CreateProvisionalReceipt.jsp" class="btn btn-secondary">
                                                <i class="feather icon-arrow-left"></i> Quay lại
                                            </a>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="feather icon-save"></i> Tạo Provisional Receipt
                                            </button>
                                        </div>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="${ctx}/assets/js/vendor-all.min.js"></script>
    <script src="${ctx}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="${ctx}/assets/js/pcoded.min.js"></script>
    <script src="${ctx}/assets/js/main.js"></script>
</body>
</html>
