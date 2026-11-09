<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Supplier Return</title>
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
                <div class="page-header"><div class="page-header-title"><h4>Create Supplier Return</h4></div></div>
                <div class="page-body">
                    <c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}" /></div></c:if>
                    <c:if test="${not hasReturnableQuantity}">
                        <div class="alert alert-warning" role="status">No returnable quantities remain. A previous return request may be pending or completed.</div>
                    </c:if>
                    <form action="${ctx}/VendorReturnController" method="post">
                        <input type="hidden" name="action" value="createReturnRequest">
                        <input type="hidden" name="importOrderId" value="${receipt.importOrderId}">
                        <input type="hidden" name="supplierId" value="${receipt.supplierId}">
                        <input type="hidden" name="quantity" value="${returnableQuantity}">
                        <div class="card">
                            <div class="card-header">
                                <h5><c:out value="${receipt.receiptNumber}" /> | Import Order <c:out value="${receipt.importOrderNumber}" /> | <c:out value="${receipt.supplierName}" /></h5>
                            </div>
                            <div class="card-block"><div class="table-responsive">
                                <table class="table table-bordered table-hover">
                                    <thead><tr><th>Product</th><th>Received</th><th>Unit</th></tr></thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${not empty receiptDetails}">
                                                <c:forEach var="d" items="${receiptDetails}">
                                                    <tr>
                                                        <td><c:out value="${d.productName}" /></td>
                                                        <td><c:out value="${d.quantity}" /></td>
                                                        <td><c:out value="${d.unit}" /></td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise><tr><td colspan="3" class="text-center">No product details found.</td></tr></c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div></div>
                        </div>
                        <div class="card">
                            <div class="card-header"><h5>Return Reason</h5></div>
                            <div class="card-block">
                                <div class="form-group">
                                    <textarea class="form-control" name="reason" rows="3" maxlength="255" required></textarea>
                                </div>
                            </div>
                        </div>
                        <div class="d-flex justify-content-end align-items-center mt-3" style="gap: 8px;">
                            <a href="${ctx}/PurchaseController?action=receivedOrderList" class="btn btn-secondary"><i class="feather icon-arrow-left"></i> Back</a>
                            <c:choose>
                                <c:when test="${hasReturnableQuantity}">
                                    <button type="submit" class="btn btn-danger"><i class="feather icon-corner-up-left"></i> Create Return</button>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-danger" disabled><i class="feather icon-corner-up-left"></i> Create Return</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </form>
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