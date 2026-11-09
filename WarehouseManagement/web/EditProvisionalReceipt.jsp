<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="inspectionResult" value="${empty inspection ? receipt.qcStatus : inspection.result}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Provisional Receipt</title>
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
                                    <h4>Edit Provisional Receipt</h4>
                                </div>
                                <div class="page-header-breadcrumb">
                                    <ul class="breadcrumb-title">
                                        <li class="breadcrumb-item"><a href="${ctx}/WarehouseStaffDashboard.jsp">Dashboard</a></li>
                                        <li class="breadcrumb-item"><a href="${ctx}/ProvisionalController?action=listProvisionalReceipts">Provisional Check</a></li>
                                        <li class="breadcrumb-item"><c:out value="${receipt.receiptNumber}" /></li>
                                    </ul>
                                </div>
                            </div>
                            <div class="page-body">
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger" role="alert"><c:out value="${error}" /></div>
                                </c:if>
                                <form action="${ctx}/ProvisionalController" method="post">
                                    <input type="hidden" name="action" value="updateProvisionalReceipt">
                                    <input type="hidden" name="provisionalReceiptId" value="${receipt.provisionalReceiptId}">
                                    <div class="card">
                                        <div class="card-header">
                                            <h5>Details: ${receipt.receiptNumber}</h5>
                                        </div>
                                        <div class="card-block">
                                            <div class="table-responsive">
                                                <table class="table table-bordered table-hover">
                                                    <thead>
                                                        <tr>
                                                            <th>Product</th>
                                                            <th>Total Quantity</th>
                                                            <th>Grade A</th>
                                                            <th>Grade B</th>
                                                            <th>Damaged</th>
                                                            <th>Unit</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="d" items="${receiptDetails}">
                                                            <tr>
                                                                <td>
                                                                    <c:out value="${d.productName}" />
                                                                    <input type="hidden" name="detailId" value="${d.provisionalDetailId}">
                                                                </td>
                                                                <td><c:out value="${d.quantity}" /></td>
                                                                <td>
                                                                    <input class="form-control" type="number" name="gradeAQuantity"
                                                                           value="${d.gradeAQuantity}" min="0" step="0.001" required
                                                                           aria-label="Grade A quantity for ${d.productName}">
                                                                </td>
                                                                <td>
                                                                    <input class="form-control" type="number" name="gradeBQuantity"
                                                                           value="${d.gradeBQuantity}" min="0" step="0.001" required
                                                                           aria-label="Grade B quantity for ${d.productName}">
                                                                </td>
                                                                <td>
                                                                    <input class="form-control" type="number" name="damagedQuantity"
                                                                           value="${d.damagedQuantity}" min="0" step="0.001" required
                                                                           aria-label="Damaged quantity for ${d.productName}">
                                                                </td>
                                                                <td><c:out value="${d.unit}" /></td>
                                                            </tr>
                                                        </c:forEach>
                                                        <c:if test="${empty receiptDetails}">
                                                            <tr><td colspan="6" class="text-center">No product details found.</td></tr>
                                                        </c:if>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="card">
                                        <div class="card-header"><h5>Quality Inspection</h5></div>
                                        <div class="card-block">
                                            <div class="row">
                                                <div class="col-md-6 form-group">
                                                    <label for="result">Result</label>
                                                    <select class="form-control" id="result" name="result" required>
                                                        <option value="Pending" ${inspectionResult == 'Pending' ? 'selected' : ''}>Pending</option>
                                                        <option value="Passed" ${inspectionResult == 'Passed' ? 'selected' : ''}>Passed</option>
                                                        <option value="Failed" ${inspectionResult == 'Failed' ? 'selected' : ''}>Failed</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label for="inspectionNotes">Inspection Notes</label>
                                                <textarea class="form-control" id="inspectionNotes" name="inspectionNotes" rows="3"><c:out value="${inspection.notes}" /></textarea>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="text-right mt-3">
                                        <a href="${ctx}/ProvisionalController?action=listProvisionalReceipts" class="btn btn-secondary">
                                            <i class="feather icon-arrow-left"></i> Back
                                        </a>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="feather icon-save"></i> Save Changes
                                        </button>
                                    </div>
                                </form>
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