<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Purchase Staff Dashboard</title>
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
							<div class="page-header"><div class="page-header-title"><h4>Purchase Staff Dashboard</h4></div></div>
							<div class="page-body">
								<c:forEach var="notification" items="${rejectionNotifications}">
									<div class="alert ${notification.decision == 'Accepted' ? 'alert-success' : 'alert-danger'} rejection-notification" role="alert">
										<span>
											<c:choose>
												<c:when test="${notification.decision == 'Accepted'}"><strong>Order accepted:</strong> <c:out value="${notification.orderType}" /> <c:out value="${notification.orderNumber}" /> was accepted and marked Completed by the Warehouse Manager.</c:when>
												<c:otherwise><strong>Order rejected:</strong> <c:out value="${notification.orderType}" /> <c:out value="${notification.orderNumber}" /> was rejected by the Warehouse Manager.</c:otherwise>
											</c:choose>
										</span>
										<form method="post" action="${ctx}/PurchaseDashboard">
											<input type="hidden" name="action" value="dismissRejectionNotification">
											<input type="hidden" name="notificationId" value="${notification.notificationId}">
											<button type="submit" class="close" aria-label="Dismiss notification" title="Dismiss"><span aria-hidden="true">&times;</span></button>
										</form>
									</div>
								</c:forEach>
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
