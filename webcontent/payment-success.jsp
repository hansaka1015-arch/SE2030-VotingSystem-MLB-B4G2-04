<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat, java.util.Date" %>
<%
    String pkg = request.getParameter("pkg");
    if (pkg == null || pkg.isEmpty()) pkg = "Fan Pack";
    
    String creditsParam = request.getParameter("credits");
    int creditsAdded = (creditsParam != null && !creditsParam.isEmpty()) ? Integer.parseInt(creditsParam) : 55;
    
    String amountParam = request.getParameter("amount");
    int amountPaid = (amountParam != null && !amountParam.isEmpty()) ? Integer.parseInt(amountParam) : 1000;
    
    String txnId = request.getParameter("txnid");
    if (txnId == null || txnId.isEmpty()) txnId = "VSL-TXN-9842105";
    
    String payMethod = request.getParameter("method");
    if (payMethod == null || payMethod.isEmpty()) payMethod = "Visa ending in 8892";
    
    int prevBalance = 25;
    int updatedBalance = prevBalance + creditsAdded;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formattedDate = sdf.format(new Date());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Successful - VoteSphere Lanka</title>
    <meta name="description" content="Payment confirmation and receipt for VoteSphere Lanka credits purchase.">
    <link rel="stylesheet" href="css/style.css">
    <style>
        @media print {
            .navbar, .footer, .no-print {
                display: none !important;
            }
            body {
                background: #fff !important;
                color: #000 !important;
            }
            .glass-card {
                background: #fff !important;
                color: #000 !important;
                border: 1px solid #ccc !important;
                box-shadow: none !important;
            }
            .gradient-text, .gradient-text-violet {
                background: none !important;
                -webkit-text-fill-color: #000 !important;
                color: #000 !important;
            }
        }
    </style>
</head>
<body>

    <!-- Navigation Header -->
    <jsp:include page="includes/navbar.jsp" />

    <main class="main-content">
        <div class="container receipt-wrapper animate-fade-in">
            
            <!-- Success Badge Header -->
            <div class="success-badge-container">
                <div class="success-icon-ring">
                    ✓
                </div>
                <h1 style="font-size: 2.2rem;">Payment <span class="gradient-text">Successful!</span></h1>
                <p class="subtitle">Your transaction has been approved and processed instantly. Credits have been loaded into your VoteSphere Lanka Wallet.</p>
            </div>

            <!-- Wallet Balance Update Card -->
            <div class="balance-update-card">
                <div class="balance-step">
                    <span class="step-label">Previous Balance</span>
                    <span class="step-value" style="color: var(--text-secondary);"><%= prevBalance %> Credits</span>
                </div>
                <div class="balance-arrow">➕</div>
                <div class="balance-step">
                    <span class="step-label">Credits Added</span>
                    <span class="step-value" style="color: var(--accent-emerald);">+<%= creditsAdded %></span>
                </div>
                <div class="balance-arrow">➔</div>
                <div class="balance-step">
                    <span class="step-label">Updated Balance</span>
                    <span class="step-value new"><%= updatedBalance %> <span style="font-size: 1rem; color: var(--text-primary);">Credits</span></span>
                </div>
            </div>

            <!-- Receipt Detail Glass Card -->
            <div class="glass-card">
                <div style="display: flex; justify-content: space-between; align-items: center; padding-bottom: 1rem; border-bottom: 1px solid var(--border-color);">
                    <div>
                        <h3 style="font-size: 1.25rem;">Transaction Summary Receipt</h3>
                        <div style="font-size: 0.8rem; color: var(--text-muted);">BrightStar Media Payment Gateway Ingestion</div>
                    </div>
                    <span class="badge badge-success">● COMPLETED</span>
                </div>

                <table class="receipt-table">
                    <tr>
                        <td>Transaction Reference ID:</td>
                        <td class="txn-id"><%= txnId %></td>
                    </tr>
                    <tr>
                        <td>Date & Timestamp:</td>
                        <td><%= formattedDate %> IST</td>
                    </tr>
                    <tr>
                        <td>Account Holder:</td>
                        <td>Kasun Perera (Voter ID: #VSL-9821)</td>
                    </tr>
                    <tr>
                        <td>Package Purchased:</td>
                        <td><strong><%= pkg %></strong></td>
                    </tr>
                    <tr>
                        <td>Total Credits Awarded:</td>
                        <td><strong style="color: var(--accent-gold);"><%= creditsAdded %> Voting Credits</strong></td>
                    </tr>
                    <tr>
                        <td>Payment Channel:</td>
                        <td><%= payMethod %></td>
                    </tr>
                    <tr>
                        <td>Billing Currency:</td>
                        <td>LKR (Sri Lankan Rupee)</td>
                    </tr>
                    <tr style="border-bottom: none;">
                        <td style="font-size: 1.1rem; font-weight: 700; color: var(--text-primary);">Total Amount Paid:</td>
                        <td style="font-size: 1.3rem; font-weight: 800;" class="gradient-text">Rs. <%= String.format("%,d", amountPaid) %>.00 LKR</td>
                    </tr>
                </table>

                <div style="background: rgba(255, 255, 255, 0.03); border: 1px dashed var(--border-color); border-radius: var(--radius-sm); padding: 0.9rem 1.25rem; font-size: 0.82rem; color: var(--text-secondary); text-align: center; margin-top: 1rem;">
                    💡 Receipt reference logged under immutable security audit log (`audit_logs.log_id #884920`).
                </div>

                <!-- Actions Button Group -->
                <div class="no-print" style="display: flex; gap: 1rem; flex-wrap: wrap; margin-top: 2rem;">
                    <a href="buy-credits.jsp" class="btn btn-primary" style="flex: 1; min-width: 200px;" onclick="alert('Proceeding to Live Voting Arena! Use your new credits to vote for contestants.')">
                        ⭐ Cast Votes Now
                    </a>
                    <a href="credit-history.jsp" class="btn btn-violet" style="flex: 1; min-width: 180px;">
                        📜 View Wallet History
                    </a>
                    <button class="btn btn-outline" onclick="window.print()" title="Print or save as PDF">
                        🖨️ Download Receipt
                    </button>
                </div>

            </div>

        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="includes/footer.jsp" />

</body>
</html>
