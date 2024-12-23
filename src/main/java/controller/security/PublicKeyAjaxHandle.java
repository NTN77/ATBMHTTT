package controller.security;

import model.service.ImageService;
import model.service.KeyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/remove-public-key-ajax-handle")
public class PublicKeyAjaxHandle extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String publicKeyId = req.getParameter("publicKeyId");
        if (action != null && publicKeyId != null) {
            if (action.equals("removePublicKey")) {
                KeyService.getInstance().removePublicKey(Integer.parseInt(publicKeyId));
            }
        }
    }
}
