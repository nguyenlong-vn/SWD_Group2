<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Goods Receipt</title>
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
                <div class="page-header"><div class="page-header-title"><h4>Create Goods Receipt</h4></div></div>
                <div class="page-body">
                    <c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}" /></div></c:if>
                    <div class="card">
                        <div class="card-header"><h5><c:out value="${receipt.receiptNumber}" /> | Import Order <c:out value="${receipt.importOrderNumber}" /></h5></div>
                        <div class="card-block"><div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead><tr><th>Product</th><th>Grade</th><th>Quantity</th><th>Unit</th><th>Location</th><th>Expiry Date</th></tr></thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty receiptDetails}">
                                            <c:forEach var="d" items="${receiptDetails}">
                                                <c:if test="${d.gradeAQuantity gt 0}">
                                                    <tr>
                                                        <td><c:out value="${d.productName}" /><input type="hidden" name="detailId" value="${d.provisionalDetailId}" form="goodsReceiptForm"><input type="hidden" name="gradeType" value="A" form="goodsReceiptForm"></td>
                                                        <td>A</td><td><c:out value="${d.gradeAQuantity}" /></td><td><c:out value="${d.unit}" /></td>
                                                        <td>
                                                            <select class="form-control" name="locationId" form="goodsReceiptForm" required>
                                                                <option value="">Select location</option>
                                                                <c:forEach var="location" items="${availableLocations}">
                                                                    <option value="${location.locationId}"><c:out value="${location.zoneName}" /> / <c:out value="${location.shelfCode}" /> / <c:out value="${location.binCode}" /></option>
                                                                </c:forEach>
                                                            </select>
                                                        </td>
                                                        <td><input class="form-control" type="date" name="expiryDate" min="${today}" form="goodsReceiptForm" required></td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${d.gradeBQuantity gt 0}">
                                                    <tr>
                                                        <td><c:out value="${d.productName}" /><input type="hidden" name="detailId" value="${d.provisionalDetailId}" form="goodsReceiptForm"><input type="hidden" name="gradeType" value="B" form="goodsReceiptForm"></td>
                                                        <td>B</td><td><c:out value="${d.gradeBQuantity}" /></td><td><c:out value="${d.unit}" /></td>
                                                        <td>
                                                            <select class="form-control" name="locationId" form="goodsReceiptForm" required>
                                                                <option value="">Select location</option>
                                                                <c:forEach var="location" items="${availableLocations}">
                                                                    <option value="${location.locationId}"><c:out value="${location.zoneName}" /> / <c:out value="${location.shelfCode}" /> / <c:out value="${location.binCode}" /></option>
                                                                </c:forEach>
                                                            </select>
                                                        </td>
                                                        <td><input class="form-control" type="date" name="expiryDate" min="${today}" form="goodsReceiptForm" required></td>
                                                    </tr>
                                                </c:if>
                                                <c:if test="${d.damagedQuantity gt 0}">
                                                    <tr>
                                                        <td><c:out value="${d.productName}" /><input type="hidden" name="detailId" value="${d.provisionalDetailId}" form="goodsReceiptForm"><input type="hidden" name="gradeType" value="Damaged" form="goodsReceiptForm"></td>
                                                        <td>Damaged</td><td><c:out value="${d.damagedQuantity}" /></td><td><c:out value="${d.unit}" /></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty quarantineLocation}">
                                                                    <c:out value="${quarantineLocation.zoneName}" /> / <c:out value="${quarantineLocation.shelfCode}" /> / <c:out value="${quarantineLocation.binCode}" />
                                                                </c:when>
                                                                <c:otherwise>Unavailable</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>Received date</td>
                                                    </tr>
                                                </c:if>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise><tr><td colspan="6" class="text-center">No product details found.</td></tr></c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div></div>
                    </div>
                    <form id="goodsReceiptForm" action="${ctx}/GoodsReceiptController" method="post">
                        <input type="hidden" name="action" value="createGoodsReceipt">
                        <input type="hidden" name="importOrderId" value="${receipt.importOrderId}">
                        <input type="hidden" name="provisionalReceiptId" value="${receipt.provisionalReceiptId}">
                        <div class="card">
                            <div class="card-header"><h5>Receipt Notes</h5></div>
                            <div class="card-block">
                                <div class="form-group"><textarea class="form-control" name="notes" rows="3"></textarea></div>
                                <c:choose>
                                    <c:when test="${not classificationValid}">
                                        <div class="alert alert-danger">
                                            Product grade quantities are invalid. Correct the listed items.
                                            <ul class="mb-0">
                                                <c:forEach var="issue" items="${classificationIssues}">
                                                    <li><c:out value="${issue}" /></li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </c:when>
                                    <c:when test="${not empty classificationIssues}">
                                        <div class="alert alert-warning">
                                            Unclassified quantities will be omitted from this Goods Receipt.
                                            <ul class="mb-0">
                                                <c:forEach var="issue" items="${classificationIssues}">
                                                    <li><c:out value="${issue}" /></li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </c:when>
                                    <c:when test="${empty availableLocations}">
                                        <div class="alert alert-warning">No available storage locations.</div>
                                    </c:when>
                                </c:choose>
                                <c:if test="${hasDamagedQuantity and empty quarantineLocation}">
                                    <div class="alert alert-warning">No available quarantine location.</div>
                                </c:if>
                                <div class="text-right">
                                    <a href="${ctx}/PurchaseController?action=goodsReceiptList" class="btn btn-secondary"><i class="feather icon-arrow-left"></i> Back</a>
                                    <c:if test="${classificationValid and not empty availableLocations and (not hasDamagedQuantity or not empty quarantineLocation)}">
                                        <button type="submit" form="goodsReceiptForm" class="btn btn-primary"><i class="feather icon-save"></i> Create Goods Receipt</button>
                                    </c:if>
                                </div>
                            </div>
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