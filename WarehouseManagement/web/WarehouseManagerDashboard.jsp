<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
	<title>${managerView.title}</title>
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
		.approval-table { min-width: 760px; background-color: #fff; }
		.approval-table th { white-space: nowrap; }
		.approval-table td { vertical-align: middle; }
		.approval-table thead th,
		.approval-table tbody td,
		.approval-table.table-hover tbody tr:hover > * { background-color: #fff; }
		.approval-actions { white-space: nowrap; }
		.approval-action-group,
		.approval-action-group form { display: flex; align-items: center; gap: 6px; }
		.approval-action-group form { margin: 0; }
		.manager-shortcuts { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; }
		.manager-shortcut { display: flex; align-items: center; gap: 14px; min-height: 92px; padding: 18px; border: 1px solid #e5e9ef; border-radius: 4px; background: #fff; color: #202a35; }
		.manager-shortcut:hover { border-color: #4680ff; color: #202a35; text-decoration: none; }
		.manager-shortcut i { color: #4680ff; font-size: 24px; }
		@media (max-width: 575px) { .pcoded-content { padding: 18px 12px; } }
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
								<div class="page-header-title"><h4>${managerView.title}</h4></div>
							</div>
							<div class="page-body">
								<c:if test="${not empty managerMessage}">
									<div class="alert alert-info" role="status"><c:out value="${managerMessage}" /></div>
								</c:if>

								<c:choose>
									<c:when test="${managerView.section == 'details'}">
										<div class="card">
											<div class="card-header"><h5><c:out value="${managerView.selectedOrder.orderNumber}" /></h5></div>
											<div class="card-block">
												<p><strong>Order Date:</strong> <c:out value="${managerView.selectedOrder.orderDate}" /></p>
												<p><strong>Supplier / Customer:</strong> <c:out value="${managerView.selectedOrder.partyName}" /></p>
												<p><strong>Created By:</strong> <c:out value="${managerView.selectedOrder.createdByName}" /></p>
											</div>
										</div>
										<div class="table-responsive">
											<table class="table table-hover approval-table">
												<thead><tr><th>Product</th><th>Quantity</th><th>Unit</th><th>Unit Price</th><th>Reason</th></tr></thead>
												<tbody>
													<c:forEach var="detail" items="${managerView.orderDetails}">
														<tr>
															<td><c:out value="${detail.productName}" /></td>
															<td><c:out value="${detail.quantity}" /></td>
															<td><c:out value="${detail.unit}" /></td>
															<td><c:choose><c:when test="${detail.unitPrice != null}"><c:out value="${detail.unitPrice}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
															<td><c:out value="${empty detail.reason ? '-' : detail.reason}" /></td>
														</tr>
													</c:forEach>
													<c:if test="${empty managerView.orderDetails}"><tr><td colspan="5" class="text-center">No order details found.</td></tr></c:if>
												</tbody>
											</table>
										</div>
										<a class="btn btn-secondary" href="${ctx}/ManagerDashboard?action=${managerView.returnSection}"><i class="feather icon-arrow-left"></i> Back to Orders</a>
									</c:when>
									<c:when test="${managerView.section == 'dashboard'}">
										<div class="manager-shortcuts">
											<a class="manager-shortcut" href="${ctx}/ApprovalController">
												<i class="feather icon-download"></i><span>Approve Import Orders</span>
											</a>
											<a class="manager-shortcut" href="${ctx}/ApprovalController?orderType=export">
												<i class="feather icon-upload"></i><span>Approve Export Orders</span>
											</a>
											<a class="manager-shortcut" href="${ctx}/ApprovalController?orderType=return">
												<i class="feather icon-rotate-ccw"></i><span>Approve Supplier Returns</span>
											</a>
										</div>
									</c:when>
									<c:otherwise>
										<div class="table-responsive">
											<table class="table table-hover approval-table">
												<thead>
													<tr>
														<th>No</th>
														<th>Order No.</th>
														<th>Order Date</th>
														<th>Supplier / Customer</th>
														<th>Created By</th>
														<th>Actions</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="order" items="${managerView.orders}" varStatus="row">
														<tr>
															<td><c:out value="${row.count}" /></td>
															<td><c:out value="${order.orderNumber}" /></td>
															<td><c:out value="${order.orderDate}" /></td>
															<td><c:out value="${order.partyName}" /></td>
															<td><c:out value="${order.createdByName}" /></td>
															<td class="approval-actions">
																<div class="approval-action-group">
																	<a class="btn btn-info btn-sm" href="${ctx}/ManagerDashboard?action=details&amp;orderType=${managerView.section}&amp;orderId=${order.orderId}">
																	<i class="feather icon-eye"></i> Details
																	</a>
																	<form method="post" action="${ctx}/ManagerDashboard" onsubmit="return confirm('Accept this order?');">
																	<input type="hidden" name="orderId" value="${order.orderId}">
																	<c:choose>
																		<c:when test="${managerView.section == 'imports'}"><input type="hidden" name="orderType" value="import"></c:when>
																		<c:when test="${managerView.section == 'exports'}"><input type="hidden" name="orderType" value="export"></c:when>
																		<c:otherwise><input type="hidden" name="orderType" value="return"></c:otherwise>
																	</c:choose>
																		<button type="submit" name="action" value="accept" class="btn btn-primary btn-sm">
																		<i class="feather icon-check"></i> Accept
																	</button>
																	</form>
																	<form method="post" action="${ctx}/ManagerDashboard" onsubmit="return confirm('Reject this order? This action cannot be undone.');">
																	<input type="hidden" name="orderId" value="${order.orderId}">
																	<c:choose>
																		<c:when test="${managerView.section == 'imports'}"><input type="hidden" name="orderType" value="import"></c:when>
																		<c:when test="${managerView.section == 'exports'}"><input type="hidden" name="orderType" value="export"></c:when>
																		<c:otherwise><input type="hidden" name="orderType" value="return"></c:otherwise>
																	</c:choose>
																		<button type="submit" name="action" value="reject" class="btn btn-outline-danger btn-sm">
																		<i class="feather icon-x"></i> Reject
																	</button>
																	</form>
																</div>
															</td>
														</tr>
													</c:forEach>
													<c:if test="${empty managerView.orders}">
														<tr><td colspan="6" class="text-center">No orders are waiting for approval.</td></tr>
													</c:if>
												</tbody>
											</table>
										</div>
									</c:otherwise>
								</c:choose>
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
