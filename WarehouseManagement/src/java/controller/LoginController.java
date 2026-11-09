package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Users;
import service.LoginService;
import view.LoginView;

@WebServlet(name = "LoginController", urlPatterns = {"/Login"})
public class LoginController extends HttpServlet {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Users user = loginService.login(username, password);

        if (user == null) {
            LoginView loginView = new LoginView();
            loginView.setErrorMessage("Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa.");
            request.setAttribute("loginView", loginView);
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
            return;
        }

        String roleKey = loginService.toRoleKey(user.getRoleName());

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("role", roleKey);

        LoginView loginView = new LoginView();
        loginView.setUser(user);
        loginView.setRoleKey(roleKey);

        response.sendRedirect(request.getContextPath() + loginView.getDashboardHref());
    }
}
