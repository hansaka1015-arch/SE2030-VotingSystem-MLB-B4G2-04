package com.votesphere.servlet;

import com.votesphere.dao.CreditPackageDAO;
import com.votesphere.dao.UserCreditDAO;
import com.votesphere.model.CreditPackage;
import com.votesphere.model.UserCredit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * CreditStoreServlet - Serves credit package store and current user wallet balance.
 */
@WebServlet(name = "CreditStoreServlet", urlPatterns = {"/buy-credits", "/CreditStoreServlet"})
public class CreditStoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CreditPackageDAO creditPackageDAO;
    private UserCreditDAO userCreditDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.creditPackageDAO = new CreditPackageDAO();
        this.userCreditDAO = new UserCreditDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Determine current logged-in user (default ID = 1 for Kasun Perera)
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            userId = 1;
            session.setAttribute("user_id", userId);
        }

        // Fetch active credit packages
        List<CreditPackage> packages = creditPackageDAO.getAllActivePackages();
        request.setAttribute("packages", packages);

        // Fetch current user wallet balance
        UserCredit userCredit = userCreditDAO.getUserCreditBalance(userId);
        request.setAttribute("userCredit", userCredit);

        // Forward to buy-credits.jsp view
        request.getRequestDispatcher("buy-credits.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
