package controller;

import model.bean.Category;
import model.bean.Product;
import model.bean.Tip;
import model.dao.CategoryDAO;
import model.dao.ProductDAO;
import model.service.CategoryService;
import model.service.ProductService;
import model.service.TipService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Home extends HttpServlet {
   ProductService productService = new ProductService();
    CategoryService categoryService = new CategoryService();
    private static final Logger logger = LoggerFactory.getLogger(Home.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {


            List<Category> categoryList = categoryService.getInstance().getALl();
            logger.info("Danh sách category từ service: {}", categoryList);
            req.setAttribute("cate_list", categoryList);
            RequestDispatcher rd = req.getRequestDispatcher("views/MenuBar/menu.jsp");
            rd.forward(req, resp);
        } catch (Exception e) {
    logger.error("Lôỗi khi lấy danh sách cate", e);
        e.printStackTrace();
        }

    }

}
