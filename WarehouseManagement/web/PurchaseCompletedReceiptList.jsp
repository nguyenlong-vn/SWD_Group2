<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Provisional Receipt Status" />
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${pageTitle}</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${ctx}/assets/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/feather/css/feather.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/datta/datta-icon.css">
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/pages/pages.css">
    <style>
        body { display: block; align-items: initial; justify-content: initial; padding: 0; }
        .pcoded-content { padding: 25px 20px; }
    </style>
</head>
<body>
    <div class="pcoded-wrapper">
        <jsp:include page="/include/sidebar.jsp" />
        <div class="pcoded-main-container">
            <jsp:include page="/include/header.jsp" />
            <div class="pcoded-content"><div class="pcoded-inner-content"><div class="main-body"><div class="page-wrapper">
                <div class="page-header"><div class="page-header-title"><h4>${pageTitle}</h4></div></div>
                <div class="page-body">
                    <c:if test="${not empty sessionScope.success}">
                        <div class="alert alert-success" role="alert"><c:out value="${sessionScope.success}" /></div>
                        <c:remove var="success" scope="session" />
                    </c:if>
                    <div class="card">
                        <div class="card-header"><h5>All Provisional Receipts</h5></div>
                        <div class="card-block"><div class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead><tr>
                                    <th>No</th><th>Provisional Receipt</th><th>Import Order</th>
                                    <th>Supplier</th><th>Received Date</th><th>Status</th><th>QC Status</th><th>Action</th>
                                </tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty receipts}">
                                            <c:forEach var="r" items="${receipts}" varStatus="s">
                                                <tr>
                                                    <td>${s.index + 1}</td>
                                                    <td><c:out value="${r.receiptNumber}" /></td>
                                                    <td><c:out value="${r.importOrderNumber}" /></td>
                                                    <td><c:out value="${r.supplierName}" /></td>
                                                    <td><c:out value="${r.receivedDate}" /></td>
                                                    <td><c:out value="${r.status}" /></td>
                                                    <td><c:out value="${r.qcStatus}" /></td>
                                                    <td>
                                                        <c:if test="${r.qcStatus == 'Passed' or r.qcStatus == 'Failed'}">
                                                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/DiscrepancyController?action=getDiscrepancyForm&amp;importOrderId=${r.importOrderId}">
                                                                <i class="feather icon-git-compare"></i> Discrepancy Log
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${r.qcStatus == 'Passed' and not r.goodsReceiptExists}">
                                                            <a class="btn btn-sm btn-primary" href="${ctx}/GoodsReceiptController?action=getGoodsReceiptForm&amp;importOrderId=${r.importOrderId}">
                                                                <i class="feather icon-download"></i> Create Receipt
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${r.qcStatus == 'Failed' and r.returnableQuantityAvailable and not r.supplierReturnPending}">
                                                            <a class="btn btn-sm btn-outline-danger" href="${ctx}/VendorReturnController?action=getVendorReturnForm&amp;importOrderId=${r.importOrderId}">
                                                                <i class="feather icon-corner-up-left"></i> Return
                                                            </a>
                                                        </c:if>
                                                        <c:choose>
                                                            <c:when test="${r.status == 'Awaiting Approval'}"><span class="label label-warning">Awaiting Approval</span></c:when>
                                                            <c:when test="${r.qcStatus == 'Passed' and r.goodsReceiptExists}"><span class="label label-success">Receipt Created</span></c:when>
                                                            <c:when test="${r.supplierReturnPending}"><span class="label label-warning">Return Pending</span></c:when>
                                                            <c:when test="${r.qcStatus == 'Failed' and not r.returnableQuantityAvailable}"><span class="label label-default">Return Completed</span></c:when>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise><tr><td colspan="8" class="text-center">No provisional receipts found.</td></tr></c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div></div>
                    </div>
                </div>
            </div></div></div>
        </div>
    </div>
    <script src="${ctx}/assets/js/vendor-all.min.js"></script>
    <script src="${ctx}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="${ctx}/assets/js/pcoded.min.js"></script>
    <script src="${ctx}/assets/js/main.js"></script>
</body>
</html>