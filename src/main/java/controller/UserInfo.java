package controller;

import model.bean.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserInfoController", value = "/user-info")
public class UserInfo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/jsp; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        User admin = (User) req.getSession().getAttribute("auth");
        if (admin != null) {
            String page = req.getParameter("page");
            String framePath;
            if (page != null) {
                switch (page) {
                    case "information":
                        framePath = "/views/user-info/change_user_info.jsp";
                        break;
                    case "security-order":
                        framePath = "/views/user-info/security_for_order.jsp";
                        break;
                    case "order-history":
                        framePath = "/views/user-info/order_history.jsp";
                        break;
                    default:
                        framePath = "/";
                        break;
                }
                req.setAttribute("framePath", framePath);
                req.setAttribute("page", page);
            }
            req.getRequestDispatcher("views/user-info/user_info.jsp").forward(req, resp);
        } else {
            resp.sendRedirect("login");
        }
    }
}
