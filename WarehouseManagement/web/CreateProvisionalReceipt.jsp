<%--
    Warehouse Staff - Tạo phiếu nhận tạm (Provisional Receipt).
    Gồm ô nhập mã đơn và nút xác nhận mã.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Provisional Receipt - Warehouse Staff</title>
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
        .page-body > .row > .col-md-6 {
            flex: 0 0 100%;
            max-width: 100%;
        }
        @media (max-width: 767.98px) {
            .page-header {
                align-items: flex-start;
                flex-direction: column;
            }
            .page-body > .row > .col-md-6 {
                flex: 0 0 100%;
                max-width: 100%;
            }
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
                                    <h4>Provisional Receipt</h4>
                                </div>
                                <div class="page-header-breadcrumb">
                                    <ul class="breadcrumb-title">
                                        <li class="breadcrumb-item"><a href="${ctx}/WarehouseStaffDashboard.jsp">Dashboard</a></li>
                                        <li class="breadcrumb-item">Provisional Receipt</li>
                                    </ul>
                                </div>
                            </div>
                            <div class="page-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="card">
                                            <div class="card-header">
                                                <h5>Nhập mã đơn nhập</h5>
                                            </div>
                                            <div class="card-block">
                                                <c:if test="${not empty error}">
                                                    <div class="alert alert-danger">
                                                        <c:out value="${error}" />
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty sessionScope.success}">
                                                    <div class="alert alert-success">
                                                        <c:out value="${sessionScope.success}" />
                                                    </div>
                                                    <c:remove var="success" scope="session" />
                                                </c:if>
                                                <form action="${ctx}/ProvisionalController" method="post">
                                                    <input type="hidden" name="action" value="getImportOrder">
                                                    <div class="form-group">
                                                        <label for="orderCode">Mã đơn nhập</label>
                                                        <input type="text" class="form-control" id="orderCode"
                                                            name="orderCode" placeholder="Nhập mã đơn nhập" required>
                                                    </div>
                                                    <button type="submit" class="btn btn-primary">
                                                        <i class="feather icon-check"></i> Xác nhận mã
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
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



