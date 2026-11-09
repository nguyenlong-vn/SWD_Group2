<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Sale Staff Dashboard</title>
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
		.rejection-notification { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
		.rejection-notification form { margin: 0; }
		.rejection-notification .close { position: static; padding: 0; }
		.dashboard-action { display: inline-flex; align-items: center; gap: 12px; min-height: 88px; padding: 18px; border: 1px solid #e5e9ef; border-radius: 4px; background: #fff; color: #202a35; }
		.dashboard-action:hover { border-color: #4680ff; color: #202a35; text-decoration: none; }
		.dashboard-action i { color: #4680ff; font-size: 24px; }
	</style>
</head>
<body>
	<div class="pcoded-wrapper">
		<jsp:include page="/include/sidebar.jsp" />
		<div class="pcoded-main-container">
			<jsp:include page="/include/header.jsp" />
			<div class="pcoded-content"><div class="pcoded-inner-content"><div class="main-body"><div class="page-wrapper">
				<div class="page-header"><div class="page-header-title"><h4>Sale Staff Dashboard</h4></div></div>
				<div class="page-body">
					<c:forEach var="notification" items="${rejectionNotifications}">
						<div class="alert ${notification.decision == 'Accepted' ? 'alert-success' : 'alert-danger'} rejection-notification" role="alert">
							<span>
								<c:choose>
									<c:when test="${notification.decision == 'Accepted'}"><strong>Order accepted:</strong> <c:out value="${notification.orderType}" /> <c:out value="${notification.orderNumber}" /> was accepted and marked Completed by the Warehouse Manager.</c:when>
									<c:otherwise><strong>Order rejected:</strong> <c:out value="${notification.orderType}" /> <c:out value="${notification.orderNumber}" /> was rejected by the Warehouse Manager.</c:otherwise>
								</c:choose>
							</span>
							<form method="post" action="${ctx}/SaleDashboard">
								<input type="hidden" name="action" value="dismissRejectionNotification">
								<input type="hidden" name="notificationId" value="${notification.notificationId}">
								<button type="submit" class="close" aria-label="Dismiss notification" title="Dismiss"><span aria-hidden="true">&times;</span></button>
							</form>
						</div>
					</c:forEach>
					<a class="dashboard-action" href="${ctx}/OutboundOrderController?action=getOutboundOrderForm">
						<i class="feather icon-plus-square"></i><span>Export Order</span>
					</a>
				</div>
			</div></div></div></div>
		</div>
	</div>
	<script src="${ctx}/assets/js/vendor-all.min.js"></script>
	<script src="${ctx}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
	<script src="${ctx}/assets/js/pcoded.min.js"></script>
	<script src="${ctx}/assets/js/main.js"></script>
</body>
</html>
