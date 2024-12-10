package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/signature-tool")
public class DownloadSignatureApp extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = getServletContext().getRealPath("signature.exe");
        java.io.File file = new java.io.File(filePath);
        if (file.exists()) {
            resp.setContentType("application/octet-stream");
            resp.setContentLength((int) file.length());
            resp.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            java.nio.file.Files.copy(file.toPath(), resp.getOutputStream());
            resp.getOutputStream().flush();
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }
}
