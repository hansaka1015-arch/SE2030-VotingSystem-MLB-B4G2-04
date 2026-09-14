<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String pkg = request.getParameter("pkg");
    if (pkg == null || pkg.isEmpty()) pkg = "Fan Pack";
    
    String amountParam = request.getParameter("amount");
    int amount = (amountParam != null && !amountParam.isEmpty()) ? Integer.parseInt(amountParam) : 1000;
    
    String creditsParam = request.getParameter("credits");
    int totalCredits = (creditsParam != null && !creditsParam.isEmpty()) ? Integer.parseInt(creditsParam) : 55;
    
    int baseCredits = (int)(totalCredits * 0.9);
    int bonusCredits = totalCredits - baseCredits;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Checkout Simulator - VoteSphere Lanka</title>
    <meta name="description" content="Secure payment gateway simulator for VoteSphere Lanka credit purchases.">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Navigation Header -->
    <jsp:include page="includes/navbar.jsp" />

    <main class="main-content">
        <div class="container animate-fade-in">
            
            <!-- Page Title -->
            <div style="margin-bottom: 2rem;">
                <a href="buy-credits.jsp" style="color: var(--text-secondary); font-size: 0.9rem; font-weight: 500;">← Back to Package Store</a>
                <h1 style="font-size: 2rem; margin-top: 0.5rem;">Payment Gateway <span class="gradient-text">Simulator</span></h1>
                <p class="subtitle">Complete your transaction to instantly receive voting credits for reality show live episodes.</p>
            </div>

            <!-- ProcessPaymentServlet Form Wrapper -->
            <form action="process-payment" method="POST">
                <input type="hidden" name="package_id" value="2">
                <input type="hidden" name="pkg_name" value="<%= pkg %>">
                <input type="hidden" name="amount" value="<%= amount %>">
                <input type="hidden" name="credits" value="<%= totalCredits %>">

                <!-- Two Column Layout: Payment Form + Order Summary Sidebar -->
                <div class="checkout-wrapper">
                    
                    <!-- Left Column: Payment Form -->
                    <div class="glass-card">
                        <h3 style="font-size: 1.3rem; margin-bottom: 1.25rem;">Select Payment Method</h3>
                        
                        <!-- Payment Methods Selector Tabs -->
                        <div class="payment-methods-grid">
                            <div class="method-option">
                                <input type="radio" name="payment_method" id="chk_card" value="card" checked onclick="switchCheckoutTab('card')">
                                <label for="chk_card" class="method-label">
                                    <span class="method-icon">💳</span>
                                    <span style="font-weight: 700; font-size: 0.95rem;">Credit / Debit Card</span>
                                    <span style="font-size: 0.78rem; color: var(--text-secondary);">Visa, Mastercard</span>
                                </label>
                            </div>
                            <div class="method-option">
                                <input type="radio" name="payment_method" id="chk_mobile" value="mobile" onclick="switchCheckoutTab('mobile')">
                                <label for="chk_mobile" class="method-label">
                                    <span class="method-icon">📱</span>
                                    <span style="font-weight: 700; font-size: 0.95rem;">Mobile Wallet</span>
                                    <span style="font-size: 0.78rem; color: var(--text-secondary);">eZ Cash / Genie</span>
                                </label>
                            </div>
                        </div>

                        <!-- CARD METHOD FORM & LIVE PREVIEW -->
                        <div id="checkout-card-sec">
                            
                            <!-- Interactive Virtual Card Mockup -->
                            <div class="card-preview">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                                    <div class="card-chip"></div>
                                    <div style="font-family: var(--font-heading); font-weight: 800; font-size: 1.2rem; color: #fff; letter-spacing: 0.05em;">VISA</div>
                                </div>
                                <div class="card-number-display" id="card-num-display">4532 •••• •••• 8892</div>
                                <div class="card-meta">
                                    <div>
                                        <div style="font-size: 0.65rem; color: var(--text-muted);">Card Holder</div>
                                        <div id="card-holder-display" style="font-size: 0.9rem; color: #fff; font-weight: 600;">KASUN PERERA</div>
                                    </div>
                                    <div style="text-align: right;">
                                        <div style="font-size: 0.65rem; color: var(--text-muted);">Expires</div>
                                        <div id="card-exp-display" style="font-size: 0.9rem; color: #fff; font-weight: 600;">08/28</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Card Inputs -->
                            <div class="form-group">
                                <label class="form-label">Cardholder Name</label>
                                <input type="text" class="form-control" name="card_name" id="input_card_name" value="Kasun Perera" oninput="updateCardPreview()">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Card Number</label>
                                <input type="text" class="form-control" name="card_num" id="input_card_num" value="4532 9842 1058 8892" maxlength="19" oninput="updateCardPreview()">
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Expiry Date (MM/YY)</label>
                                    <input type="text" class="form-control" name="card_exp" id="input_card_exp" value="08/28" maxlength="5" oninput="updateCardPreview()">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">CVV Security Code</label>
                                    <input type="password" class="form-control" name="card_cvv" id="input_card_cvv" value="882" maxlength="4">
                                </div>
                            </div>
                        </div>

                        <!-- MOBILE WALLET FORM -->
                        <div id="checkout-mobile-sec" style="display: none;">
                            <div style="background: rgba(6, 182, 212, 0.08); border: 1px solid rgba(6, 182, 212, 0.25); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.5rem;">
                                <div style="font-weight: 700; color: var(--accent-cyan); margin-bottom: 0.25rem;">📱 Mobile Wallet Payment</div>
                                <div style="font-size: 0.85rem; color: var(--text-secondary);">Enter your Sri Lanka mobile wallet phone number. A PIN/OTP verification code prompt will follow.</div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Select Wallet Provider</label>
                                <select class="form-control" name="mobile_provider" id="mobile_provider_select">
                                    <option value="eZ Cash">Dialog eZ Cash</option>
                                    <option value="mCash">Mobitel mCash</option>
                                    <option value="Genie">Dialog Genie</option>
                                    <option value="FriMi">NTB FriMi</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Mobile Number</label>
                                <input type="tel" class="form-control" name="mobile_num" id="input_mobile_num" value="0779842105" placeholder="077XXXXXXX">
                            </div>

                            <div class="form-group">
                                <label class="form-label">OTP Verification Code (Simulated)</label>
                                <input type="text" class="form-control" id="input_mobile_otp" value="849201" placeholder="Enter 6-digit OTP">
                            </div>
                        </div>

                        <!-- Security & Terms -->
                        <div style="margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid var(--border-color); display: flex; align-items: center; justify-content: space-between; font-size: 0.82rem; color: var(--text-secondary);">
                            <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                                <input type="checkbox" checked style="accent-color: var(--accent-gold);"> Save payment details for future quick voting top-ups
                            </label>
                        </div>

                    </div>

                    <!-- Right Column: Order Summary Sidebar -->
                    <div>
                        <div class="glass-card">
                            <h3 style="font-size: 1.2rem; margin-bottom: 1.25rem;">Transaction Summary</h3>
                            
                            <div class="summary-box" style="background: transparent; padding: 0;">
                                <div class="summary-row">
                                    <span>Package Name:</span>
                                    <strong style="color: var(--text-primary);"><%= pkg %></strong>
                                </div>
                                <div class="summary-row">
                                    <span>Base Credits:</span>
                                    <span><%= baseCredits %> Credits</span>
                                </div>
                                <div class="summary-row">
                                    <span>Bonus Credits:</span>
                                    <span style="color: var(--accent-emerald); font-weight: 600;">+<%= bonusCredits %> Free</span>
                                </div>
                                <div class="summary-row">
                                    <span>Total Credits Added:</span>
                                    <strong style="color: var(--accent-gold); font-size: 1.1rem;"><%= totalCredits %> Credits</strong>
                                </div>
                                <div class="summary-row">
                                    <span>Payment Currency:</span>
                                    <span>LKR (Sri Lankan Rupee)</span>
                                </div>
                                <div class="summary-row">
                                    <span>Processing & Govt Tax:</span>
                                    <span style="color: var(--accent-emerald);">Rs. 0.00 (Waived)</span>
                                </div>
                                <div class="summary-row total">
                                    <span>Total Payable Amount:</span>
                                    <span class="gradient-text">Rs. <%= String.format("%,d", amount) %>.00</span>
                                </div>
                            </div>

                            <!-- Payment CTA Button -->
                            <div style="margin-top: 2rem;">
                                <button type="submit" class="btn btn-primary btn-block" id="btn-submit-payment">
                                    💳 Confirm Payment (Rs. <%= String.format("%,d", amount) %> LKR)
                                </button>
                                
                                <a href="buy-credits.jsp" class="btn btn-outline btn-block" style="margin-top: 0.75rem; font-size: 0.88rem;">
                                    Cancel & Choose Another Package
                                </a>
                            </div>

                            <!-- Trust Badges -->
                            <div style="margin-top: 1.75rem; text-align: center; font-size: 0.8rem; color: var(--text-muted); line-height: 1.5;">
                                <div>🛡️ Verified by VoteSphere Lanka Security</div>
                                <div>Sub-500ms Instant Wallet Credit Balance Sync</div>
                            </div>

                        </div>
                    </div>

                </div>
            </form>

        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="includes/footer.jsp" />

    <!-- JavaScript Handlers -->
    <script>
        function switchCheckoutTab(tab) {
            if (tab === 'card') {
                document.getElementById('checkout-card-sec').style.display = 'block';
                document.getElementById('checkout-mobile-sec').style.display = 'none';
            } else {
                document.getElementById('checkout-card-sec').style.display = 'none';
                document.getElementById('checkout-mobile-sec').style.display = 'block';
            }
        }

        function updateCardPreview() {
            const name = document.getElementById('input_card_name').value || 'KASUN PERERA';
            const num = document.getElementById('input_card_num').value || '4532 •••• •••• 8892';
            const exp = document.getElementById('input_card_exp').value || '08/28';

            document.getElementById('card-holder-display').innerText = name.toUpperCase();
            document.getElementById('card-num-display').innerText = num;
            document.getElementById('card-exp-display').innerText = exp;
        }
    </script>

</body>
</html>
