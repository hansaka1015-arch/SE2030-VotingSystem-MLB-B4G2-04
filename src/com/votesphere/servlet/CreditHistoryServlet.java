package com.votesphere.servlet;

import com.votesphere.dao.TransactionDAO;
import com.votesphere.dao.UserCreditDAO;
import com.votesphere.model.Transaction;
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
 * CreditHistoryServlet - Fetches transaction history logs and wallet statistics for display.
 */
@WebServlet(name = "CreditHistoryServlet", urlPatterns = {"/credit-history", "/CreditHistoryServlet"})
public class CreditHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TransactionDAO transactionDAO;
    private UserCreditDAO userCreditDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.transactionDAO = new TransactionDAO();
        this.userCreditDAO = new UserCreditDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            userId = 1;
            session.setAttribute("user_id", userId);
        }

        // Fetch transaction history
        List<Transaction> transactions = transactionDAO.getTransactionsByUserId(userId);
        request.setAttribute("transactions", transactions);

        // Fetch user wallet balance
        UserCredit userCredit = userCreditDAO.getUserCreditBalance(userId);
        request.setAttribute("userCredit", userCredit);

        // Forward to credit-history.jsp view
        request.getRequestDispatcher("credit-history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
