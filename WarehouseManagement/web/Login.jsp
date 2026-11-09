<%--
    Trang đăng nhập hệ thống Warehouse Management.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Login - Warehouse Management</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <link rel="stylesheet" href="${ctx}/assets/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/feather/css/feather.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/fontawesome/css/fontawesome-all.min.css">
    <link rel="stylesheet" href="${ctx}/assets/fonts/datta/datta-icon.css">
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/pages/pages.css">
</head>
<body>
    <div class="theme-loader">
        <div class="ball-scale">
            <div class="contain">
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
                <div class="ring"><div class="frame"></div></div>
            </div>
        </div>
    </div>

    <div class="auth-wrapper">
        <div class="auth-content container">
            <div class="card">
                <div class="row align-items-center">
                    <div class="col-md-12">
                        <div class="card-body">
                            <h4 class="mb-3 f-w-400">Fruit and Vegetable Warehouse Management System</h4>

                            <c:if test="${not empty loginView && loginView.hasError()}">
                                <div class="alert alert-danger mb-3">
                                    <c:out value="${loginView.errorMessage}" />
                                </div>
                            </c:if>

                            <form action="${ctx}/Login" method="post">
                                <div class="input-group mb-3">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text"><i class="feather icon-user"></i></span>
                                    </div>
                                    <input type="text" name="username" class="form-control"
                                           placeholder="Username" required>
                                </div>
                                <div class="input-group mb-4">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text"><i class="feather icon-lock"></i></span>
                                    </div>
                                    <input type="password" name="password" class="form-control"
                                           placeholder="Password" required>
                                </div>
                                <button type="submit" class="btn btn-primary btn-block mb-4">
                                    Login
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${ctx}/assets/js/vendor-all.min.js"></script>
    <script src="${ctx}/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="${ctx}/assets/js/pcoded.min.js"></script>
</body>
</html>
