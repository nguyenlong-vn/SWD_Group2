<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Discrepancy Log - ${discrepancyView.importOrderNumber}</title>
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
                <div class="page-header"><div class="page-header-title"><h4>Discrepancy Log</h4></div></div>
                <div class="page-body">
                    <c:if test="${not empty sessionScope.success}">
                        <div class="alert alert-success" role="alert"><c:out value="${sessionScope.success}" /></div>
                        <c:remove var="success" scope="session" />
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert"><c:out value="${error}" /></div>
                    </c:if>
                    <div class="card">
                        <div class="card-header"><h5>Receipt: <c:out value="${discrepancyView.receiptNumber}" /> | Import Order: <c:out value="${discrepancyView.importOrderNumber}" /></h5></div>
                        <div class="card-block"><div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead><tr><th>Product</th><th>Unit</th><th>Ordered Quantity</th><th>Received Quantity</th><th>Grade A</th><th>Grade B</th><th>Damaged</th><th>Difference</th></tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty discrepancyView.discrepancies}">
                                            <c:forEach var="d" items="${discrepancyView.discrepancies}">
                                                <tr>
                                                    <td><c:out value="${d.productName}" /></td>
                                                    <td><c:out value="${d.unit}" /></td>
                                                    <td><c:out value="${d.orderedQuantity}" /></td>
                                                    <td><c:out value="${d.receivedQuantity}" /></td>
                                                    <td><c:out value="${d.gradeAQuantity}" /></td>
                                                    <td><c:out value="${d.gradeBQuantity}" /></td>
                                                    <td><c:out value="${d.damagedQuantity}" /></td>
                                                    <td><c:out value="${d.difference}" /></td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise><tr><td colspan="8" class="text-center">No order details found.</td></tr></c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div></div>
                    </div>
                    <div class="d-flex justify-content-end align-items-center mt-3" style="gap: 8px;">
                        <a href="${ctx}/PurchaseController?action=discrepancyList" class="btn btn-secondary">
                            <i class="feather icon-arrow-left"></i> Back
                        </a>
                        <c:if test="${discrepancyView.hasDamagedQuantity}">
                            <form action="${ctx}/DiscrepancyController" method="post" class="mb-0">
                                <input type="hidden" name="action" value="createDiscrepancyLog">
                                <input type="hidden" name="importOrderId" value="${discrepancyView.importOrderId}">
                                <input type="hidden" name="quantity" value="${discrepancyView.totalDamagedQuantity}">
                                <button type="submit" class="btn btn-primary">
                                    <i class="feather icon-check"></i> Confirm
                                </button>
                            </form>
                        </c:if>
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
