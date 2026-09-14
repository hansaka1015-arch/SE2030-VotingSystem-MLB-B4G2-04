package com.votesphere.servlet;

import com.votesphere.dao.CreditPackageDAO;
import com.votesphere.dao.TransactionDAO;
import com.votesphere.dao.UserCreditDAO;
import com.votesphere.model.CreditPackage;
import com.votesphere.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * ProcessPaymentServlet - Processes payment gateway checkout forms, logs transactions, and updates credit wallet balance.
 */
@WebServlet(name = "ProcessPaymentServlet", urlPatterns = {"/process-payment", "/ProcessPaymentServlet"})
public class ProcessPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CreditPackageDAO creditPackageDAO;
    private TransactionDAO transactionDAO;
    private UserCreditDAO userCreditDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.creditPackageDAO = new CreditPackageDAO();
        this.transactionDAO = new TransactionDAO();
        this.userCreditDAO = new UserCreditDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            userId = 1;
            session.setAttribute("user_id", userId);
        }

        // Retrieve form parameters
        String packageIdStr = request.getParameter("package_id");
        String packageName = request.getParameter("pkg_name");
        String paymentMethodType = request.getParameter("payment_method"); // 'card' or 'mobile'
        
        String amountStr = request.getParameter("amount");
        String creditsStr = request.getParameter("credits");

        int packageId = (packageIdStr != null && !packageIdStr.isEmpty()) ? Integer.parseInt(packageIdStr) : 2; // Default Fan Pack
        double amountLkr = (amountStr != null && !amountStr.isEmpty()) ? Double.parseDouble(amountStr) : 1000.00;
        int creditsAdded = (creditsStr != null && !creditsStr.isEmpty()) ? Integer.parseInt(creditsStr) : 55;

        // Fetch exact package details if package_id is valid
        CreditPackage pkg = creditPackageDAO.getPackageById(packageId);
        if (pkg != null) {
            packageName = pkg.getName();
            amountLkr = pkg.getPriceLkr();
            creditsAdded = pkg.getTotalCredits();
        } else if (packageName == null || packageName.isEmpty()) {
            packageName = "Fan Pack";
        }

        // Format payment method string
        String paymentMethodStr = "Visa ending in 8892";
        if ("mobile".equalsIgnoreCase(paymentMethodType)) {
            String provider = request.getParameter("mobile_provider");
            String mobileNum = request.getParameter("mobile_num");
            if (provider == null || provider.isEmpty()) provider = "eZ Cash";
            if (mobileNum == null || mobileNum.isEmpty()) mobileNum = "0779842105";
            paymentMethodStr = provider + " (" + mobileNum + ")";
        } else {
            String cardNum = request.getParameter("card_num");
            if (cardNum != null && cardNum.length() >= 4) {
                String last4 = cardNum.substring(cardNum.length() - 4);
                paymentMethodStr = "Card ending in " + last4;
            }
        }

        // Generate unique transaction ID
        String transactionId = "VSL-TXN-" + (100000 + (int)(Math.random() * 900000));

        // Create transaction object
        Transaction txn = new Transaction(transactionId, userId, packageId, amountLkr, creditsAdded, paymentMethodStr, "COMPLETED");

        // Save transaction to DB
        boolean txnSaved = transactionDAO.recordTransaction(txn);

        // Update user credit wallet in DB
        boolean walletUpdated = userCreditDAO.addCreditsToUser(userId, creditsAdded);

        System.out.println("[ProcessPaymentServlet] Transaction " + transactionId + " recorded: " + txnSaved + ", Wallet balance updated: " + walletUpdated);

        // Build redirect URL to payment-success.jsp
        String redirectUrl = "payment-success.jsp?" +
                "txnid=" + URLEncoder.encode(transactionId, StandardCharsets.UTF_8.name()) +
                "&pkg=" + URLEncoder.encode(packageName, StandardCharsets.UTF_8.name()) +
                "&credits=" + creditsAdded +
                "&amount=" + (int)amountLkr +
                "&method=" + URLEncoder.encode(paymentMethodStr, StandardCharsets.UTF_8.name());

        response.sendRedirect(redirectUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("buy-credits.jsp");
    }
}
