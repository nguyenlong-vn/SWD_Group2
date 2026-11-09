<%--
    Header dùng chung cho các vai trò: Administrator, WarehouseManager,
    WarehouseStaff, PurchaseStaff, SaleStaff.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!-- [ Header ] start -->
<div style="background-color:transparent; height:60px; width:300px; display:flex; align-items:center; justify-content:flex-end; padding:0 18px; position:fixed; top:0; right:0; z-index:1028; box-sizing:border-box;">
    <div style="display:flex; align-items:center; gap:12px;">
        <a href="${ctx}/UserProfile" style="text-decoration:none; color:#1b1c1d; font-size:14px; padding:6px 14px; border:1px solid #ced4da; background:#ffffff; border-radius:4px; display:inline-flex; align-items:center;">
            <i class="feather icon-user" style="margin-right:6px;"></i> My Profile
        </a>
        <a href="${ctx}/Logout" style="text-decoration:none; color:#dc3545; font-size:14px; padding:6px 14px; border:1px solid #dc3545; background:#ffffff; border-radius:4px; display:inline-flex; align-items:center;">
            <i class="feather icon-log-out" style="margin-right:4px;"></i> Logout
        </a>
    </div>
</div>
<!-- [ Header ] end -->



