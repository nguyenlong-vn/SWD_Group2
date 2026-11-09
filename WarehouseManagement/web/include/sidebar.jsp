<%--
    Sidebar dùng chung - menu phân quyền theo session attribute "role".
    Vai trò: Administrator, WarehouseManager, WarehouseStaff, PurchaseStaff, SaleStaff.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="role" value="${sessionScope.role}" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:choose>
    <c:when test="${role == 'Administrator'}">
        <c:set var="dashboardHref" value="${ctx}/AdminDashboard" />
    </c:when>
    <c:when test="${role == 'WarehouseManager'}">
        <c:set var="dashboardHref" value="${ctx}/ManagerDashboard" />
    </c:when>
    <c:when test="${role == 'WarehouseStaff'}">
        <c:set var="dashboardHref" value="${ctx}/StaffDashboard" />
    </c:when>
    <c:when test="${role == 'PurchaseStaff'}">
        <c:set var="dashboardHref" value="${ctx}/PurchaseDashboard" />
    </c:when>
    <c:when test="${role == 'SaleStaff'}">
        <c:set var="dashboardHref" value="${ctx}/SaleDashboard" />
    </c:when>
    <c:otherwise>
        <c:set var="dashboardHref" value="${ctx}/Login" />
    </c:otherwise>
</c:choose>
<!-- [ navigation menu ] start -->
<nav class="pcoded-navbar menupos-fixed menu-light brand-blue">
    <style>
        .pcoded-navbar .header-logo .mobile-menu,
        .pcoded-navbar .mobile-menu,
        .pcoded-header .mobile-menu {
            display: none !important;
        }
    </style>
    <div class="navbar-wrapper">
        <div class="navbar-brand header-logo">
            <a href="${dashboardHref}" class="b-brand">
                <span class="b-title">Warehouse Manage System</span>
            </a>
        </div>
        <div class="navbar-content scroll-div">
            <ul class="nav pcoded-inner-navbar">

                <!-- ================= Administrator ================= -->
                <c:if test="${role == 'Administrator'}">
                    <li class="nav-item pcoded-menu-caption"><label>Overview</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/AdminDashboard" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-home"></i></span>
                            <span class="pcoded-mtext">Dashboard</span>
                        </a>
                    </li>

                    <li class="nav-item pcoded-menu-caption"><label>System Administration</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/UserManagement" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-users"></i></span>
                            <span class="pcoded-mtext">User Management</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${ctx}/RoleManagement" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-shield"></i></span>
                            <span class="pcoded-mtext">Role Management</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${ctx}/SystemLogs" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-list"></i></span>
                            <span class="pcoded-mtext">System Logs</span>
                        </a>
                    </li>

                    <li class="nav-item pcoded-menu-caption"><label>Reports</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/Reports" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-bar-chart-2"></i></span>
                            <span class="pcoded-mtext">Reports</span>
                        </a>
                    </li>
                </c:if>

                <!-- ================= WarehouseManager ================= -->
                <c:if test="${role == 'WarehouseManager'}">
                    <li class="nav-item pcoded-menu-caption"><label>Overview</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/ManagerDashboard" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-home"></i></span>
                            <span class="pcoded-mtext">Dashboard</span>
                        </a>
                    </li>

                    <li class="nav-item pcoded-menu-caption"><label>Approvals</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/ApprovalController" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-download"></i></span>
                            <span class="pcoded-mtext">Approve Import Orders</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${ctx}/ApprovalController?orderType=export" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-upload"></i></span>
                            <span class="pcoded-mtext">Approve Export Orders</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${ctx}/ApprovalController?orderType=return" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-rotate-ccw"></i></span>
                            <span class="pcoded-mtext">Approve Supplier Returns</span>
                        </a>
                    </li>
                </c:if>

                <!-- ================= WarehouseStaff ================= -->
                <c:if test="${role == 'WarehouseStaff'}">
                    <li class="nav-item pcoded-menu-caption"><label>Overview</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/WarehouseStaffDashboard.jsp" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-home"></i></span>
                            <span class="pcoded-mtext">Dashboard</span>
                        </a>
                    </li>

                    <li class="nav-item pcoded-menu-caption"><label>Provisional</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/CreateProvisionalReceipt.jsp" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-plus-square"></i></span>
                            <span class="pcoded-mtext">Provisional Receipt</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${ctx}/ProvisionalController?action=listProvisionalReceipts" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-check-square"></i></span>
                            <span class="pcoded-mtext">Provisional Check</span>
                        </a>
                    </li>
                </c:if>

                <!-- ================= PurchaseStaff ================= -->
                <c:if test="${role == 'PurchaseStaff'}">
                    <li class="nav-item">
                        <a href="${ctx}/PurchaseDashboard" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-home"></i></span>
                            <span class="pcoded-mtext">Dashboard</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${ctx}/PurchaseController?action=receivedOrderList" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-inbox"></i></span>
                            <span class="pcoded-mtext">List Received Order</span>
                        </a>
                    </li>
                </c:if>

                <!-- ================= SaleStaff ================= -->
                <c:if test="${role == 'SaleStaff'}">
                    <li class="nav-item pcoded-menu-caption"><label>Overview</label></li>
                    <li class="nav-item">
                        <a href="${ctx}/SaleDashboard" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-home"></i></span>
                            <span class="pcoded-mtext">Dashboard</span>
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="${ctx}/OutboundOrderController?action=getOutboundOrderForm" class="nav-link">
                            <span class="pcoded-micon"><i class="feather icon-plus-square"></i></span>
                            <span class="pcoded-mtext">Export Order</span>
                        </a>
                    </li>
                </c:if>

            </ul>
        </div>
    </div>
</nav>
<!-- [ navigation menu ] end -->



