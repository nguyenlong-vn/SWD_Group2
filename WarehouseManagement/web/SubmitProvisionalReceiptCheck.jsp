<%--
    Warehouse Staff - Kiểm tra phiếu nhận tạm (Provisional Check).
    Hiển thị danh sách các ProvisionalReceipt.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Provisional Check - Warehouse Staff</title>
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
                                    <h4>Provisional Check</h4>
                                </div>
                                <div class="page-header-breadcrumb">
                                    <ul class="breadcrumb-title">
                                        <li class="breadcrumb-item"><a href="${ctx}/WarehouseStaffDashboard.jsp">Dashboard</a></li>
                                        <li class="breadcrumb-item">Provisional Check</li>
                                    </ul>
                                </div>
                            </div>
                            <div class="page-body">
                                <c:if test="${not empty sessionScope.success}">
                                    <div class="alert alert-success" role="alert">
                                        <c:out value="${sessionScope.success}" />
                                    </div>
                                    <c:remove var="success" scope="session" />
                                </c:if>
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger" role="alert"><c:out value="${error}" /></div>
                                </c:if>
                                <div class="card">
                                    <div class="card-header">
                                        <h5>Danh sách Provisional Receipt</h5>
                                    </div>
                                    <div class="card-block">
                                        <div class="table-responsive">
                                            <table class="table table-striped table-hover">
                                                <thead>
                                                    <tr>
                                                        <th>No</th>
                                                        <th>Receipt Number</th>
                                                        <th>Import Order</th>
                                                        <th>Status</th>
                                                        <th>QC Status</th>
                                                        <th>Notes</th>
                                                        <th>Action</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:choose>
                                                        <c:when test="${not empty provisionalReceipts}">
                                                            <c:forEach var="r" items="${provisionalReceipts}" varStatus="s">
                                                                <tr>
                                                                    <td>${s.index + 1}</td>
                                                                    <td><c:out value="${r.receiptNumber}" /></td>
                                                                    <td><c:out value="${r.importOrderId}" /></td>
                                                                    <td><c:out value="${r.status}" /></td>
                                                                    <td><c:out value="${r.qcStatus}" /></td>
                                                                    <td><c:out value="${r.notes}" /></td>
                                                                    <td>
                                                                        <a href="${ctx}/ProvisionalController?action=editProvisionalReceipt&amp;provisionalReceiptId=${r.provisionalReceiptId}"
                                                                           class="btn btn-sm btn-primary" aria-label="Sửa ${r.receiptNumber}">
                                                                            <i class="feather icon-edit"></i> Sửa
                                                                        </a>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <tr>
                                                                <td colspan="7" class="text-center">Chưa có dữ liệu Provisional Receipt.</td>
                                                            </tr>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </tbody>
                                            </table>
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
