<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Export Order</title>
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
        .order-line { display: grid; grid-template-columns: minmax(200px, 2fr) minmax(110px, 1fr) 90px 40px; gap: 10px; align-items: end; margin-bottom: 12px; }
        .unit-label { min-height: 38px; display: flex; align-items: center; }
        .line-remove { height: 38px; }
        @media (max-width: 760px) { .order-line { grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); } .line-product { grid-column: 1 / -1; } }
    </style>
</head>
<body>
    <div class="pcoded-wrapper">
        <jsp:include page="/include/sidebar.jsp" />
        <div class="pcoded-main-container">
            <jsp:include page="/include/header.jsp" />
            <div class="pcoded-content"><div class="pcoded-inner-content"><div class="main-body"><div class="page-wrapper">
                <div class="page-header"><div class="page-header-title"><h4>Create Export Order</h4></div></div>
                <div class="page-body">
                    <c:if test="${not empty exportOrderView.errorMessage}">
                        <div class="alert alert-danger" role="alert"><c:out value="${exportOrderView.errorMessage}" /></div>
                    </c:if>
                    <c:if test="${not empty exportOrderView.successMessage}">
                        <div class="alert alert-success" role="status"><c:out value="${exportOrderView.successMessage}" /></div>
                    </c:if>
                    <c:if test="${empty exportOrderView.customers}">
                        <div class="alert alert-warning">No active customers are available.</div>
                    </c:if>
                    <c:if test="${empty exportOrderView.products}">
                        <div class="alert alert-warning">No active products are available.</div>
                    </c:if>

                    <form action="${ctx}/OutboundOrderController" method="post" onsubmit="return confirm('Submit this export order?');">
                        <input type="hidden" name="action" value="processCreateOrder">
                        <div class="card">
                            <div class="card-header"><h5>Order Information</h5></div>
                            <div class="card-block">
                                <div class="form-row">
                                    <div class="form-group col-md-6">
                                        <label for="customerId">Customer</label>
                                        <select class="form-control" id="customerId" name="customerId" required>
                                            <option value="">Select customer</option>
                                            <c:forEach var="customer" items="${exportOrderView.customers}">
                                                <option value="${customer.customerId}"><c:out value="${customer.customerCode}" /> - <c:out value="${customer.customerName}" /></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-6">
                                        <label for="orderDate">Order Date</label>
                                        <input class="form-control" id="orderDate" name="orderDate" type="date" value="${today}" required>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5>Order Details</h5>
                                <button type="button" class="btn btn-outline-primary btn-sm" id="add-order-line" title="Add product line" aria-label="Add product line">
                                    <i class="feather icon-plus"></i> Add Product
                                </button>
                            </div>
                            <div class="card-block">
                                <div class="order-line font-weight-bold">
                                    <span class="line-product">Product</span><span>Quantity</span><span>Unit</span><span></span>
                                </div>
                                <div id="order-lines"></div>
                            </div>
                        </div>

                        <template id="order-line-template">
                            <div class="order-line">
                                <div class="line-product">
                                    <label class="sr-only">Product</label>
                                    <select class="form-control product-select" name="productId" required>
                                        <option value="">Select product</option>
                                        <c:forEach var="product" items="${exportOrderView.products}">
                                            <option value="${product.productId}" data-unit="<c:out value='${product.unit}' />"><c:out value="${product.productCode}" /> - <c:out value="${product.productName}" /></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div>
                                    <label class="sr-only">Quantity</label>
                                    <input class="form-control" name="quantity" type="number" min="0.001" step="0.001" placeholder="Quantity" required>
                                </div>
                                <div class="unit-label text-muted">-</div>
                                <button type="button" class="btn btn-outline-danger line-remove" title="Remove product line" aria-label="Remove product line">
                                    <i class="feather icon-trash-2"></i>
                                </button>
                            </div>
                        </template>

                        <div class="d-flex justify-content-end align-items-center mt-3" style="gap: 8px;">
                            <a href="${ctx}/SaleDashboard" class="btn btn-secondary"><i class="feather icon-arrow-left"></i> Back</a>
                            <button type="submit" class="btn btn-primary"><i class="feather icon-check"></i> Submit Export Order</button>
                        </div>
                    </form>
                </div>
            </div></div></div></div>
        </div>
    </div>
    <script>
        (function () {
            var container = document.getElementById('order-lines');
            var template = document.getElementById('order-line-template');
            var addButton = document.getElementById('add-order-line');

            function addLine() {
                container.appendChild(template.content.cloneNode(true));
            }

            addButton.addEventListener('click', addLine);
            container.addEventListener('change', function (event) {
                if (!event.target.classList.contains('product-select')) {
                    return;
                }
                var row = event.target.closest('.order-line');
                var option = event.target.options[event.target.selectedIndex];
                row.querySelector('.unit-label').textContent = option.dataset.unit || '-';
            });
            container.addEventListener('click', function (event) {
                var removeButton = event.target.closest('.line-remove');
                if (removeButton) {
                    removeButton.closest('.order-line').remove();
                    if (!container.querySelector('.order-line')) {
                        addLine();
                    }
                }
            });
            addLine();
        }());
    </script>
    <script src="${ctx}/assets/js/vendor-all.min.js"></script>
    <script src="${ctx}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="${ctx}/assets/js/pcoded.min.js"></script>
    <script src="${ctx}/assets/js/main.js"></script>
</body>
</html>