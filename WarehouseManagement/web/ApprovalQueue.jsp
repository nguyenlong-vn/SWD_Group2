<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
	<title>${approvalQueueView.title}</title>
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
								<div class="page-header-title"><h4>${approvalQueueView.title}</h4></div>
							</div>
							<div class="page-body">
								<c:if test="${not empty approvalQueueView.message}">
									<div class="alert alert-info" role="status"><c:out value="${approvalQueueView.message}" /></div>
								</c:if>

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
											<c:forEach var="order" items="${approvalQueueView.pendingOrders}" varStatus="row">
												<tr>
													<td><c:out value="${row.count}" /></td>
													<td><c:out value="${order.orderNumber}" /></td>
													<td><c:out value="${order.orderDate}" /></td>
													<td><c:out value="${order.partyName}" /></td>
													<td><c:out value="${order.createdByName}" /></td>
													<td class="approval-actions">
														<div class="approval-action-group">
															<a class="btn btn-info btn-sm" href="${ctx}/ManagerDashboard?action=details&amp;orderType=${orderType}&amp;orderId=${order.orderId}">
															<i class="feather icon-eye"></i> Details
															</a>
															<form method="post" action="${ctx}/ApprovalController" onsubmit="return confirm('Accept this order?');">
															<input type="hidden" name="orderType" value="${orderType}">
															<input type="hidden" name="orderId" value="${order.orderId}">
																<button type="submit" name="action" value="approve" class="btn btn-primary btn-sm">
																<i class="feather icon-check"></i> Accept
															</button>
															</form>
															<form method="post" action="${ctx}/ApprovalController" onsubmit="return confirm('Reject this order? This action cannot be undone.');">
															<input type="hidden" name="orderType" value="${orderType}">
															<input type="hidden" name="orderId" value="${order.orderId}">
																<button type="submit" name="action" value="reject" class="btn btn-outline-danger btn-sm">
																<i class="feather icon-x"></i> Reject
															</button>
															</form>
														</div>
													</td>
												</tr>
											</c:forEach>
											<c:if test="${empty approvalQueueView.pendingOrders}">
												<tr><td colspan="6" class="text-center">No orders are waiting for approval.</td></tr>
											</c:if>
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
	<script src="${ctx}/assets/js/vendor-all.min.js"></script>
	<script src="${ctx}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
	<script src="${ctx}/assets/js/pcoded.min.js"></script>
	<script src="${ctx}/assets/js/main.js"></script>
</body>
</html>
