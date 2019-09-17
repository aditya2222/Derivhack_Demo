
package com.algorand.demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet(
        name = "ObjectInputServlet",
        urlPatterns = "/InputObject"
)
public class ObjectInputServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String classPath = "org.isda.cdm."+req.getParameter("cdminput");
        String template = "";
        try {
            template = JSONTemplate.toJSONTemplate(classPath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("template", template);
        RequestDispatcher view = req.getRequestDispatcher("template.jsp");
        view.forward(req, resp);

    }
}