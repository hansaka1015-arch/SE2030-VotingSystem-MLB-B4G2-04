<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.votesphere.model.CreditPackage, com.votesphere.model.UserCredit" %>
<%
    // Retrieve dynamic data set by CreditStoreServlet, or fallback gracefully
    UserCredit userCredit = (UserCredit) request.getAttribute("userCredit");
    int currentCredits = (userCredit != null) ? userCredit.getBalance() : 25;

    @SuppressWarnings("unchecked")
    List<CreditPackage> packagesList = (List<CreditPackage>) request.getAttribute("packages");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vote Credit Store - VoteSphere Lanka</title>
    <meta name="description" content="Buy voting credit packages for Sri Lanka reality shows on VoteSphere Lanka. Instant delivery, multiple payment options in LKR.">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Header Navigation -->
    <jsp:include page="includes/navbar.jsp" />

    <!-- Main Content Area -->
    <main class="main-content">
        <div class="container animate-fade-in">
            
            <!-- Hero Header & Balance Summary Banner -->
            <div style="display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 1.5rem; margin-bottom: 2rem;">
                <div>
                    <h1 style="font-size: 2.2rem;">Vote Credit <span class="gradient-text">Packages</span></h1>
                    <p class="subtitle">Power up your votes for your favorite contestants in BrightStar Media's Live Elimination Rounds.</p>
                </div>
                
                <!-- Current Wallet Card -->
                <div class="glass-card" style="padding: 1rem 1.5rem; display: flex; align-items: center; gap: 1.25rem; background: rgba(245, 158, 11, 0.08); border-color: rgba(245, 158, 11, 0.3);">
                    <div style="font-size: 2.2rem;">👛</div>
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600;">Your Wallet Balance</div>
                        <div style="font-size: 1.6rem; font-weight: 800; color: var(--accent-gold); font-family: var(--font-heading);">
                            <%= currentCredits %> <span style="font-size: 1rem; font-weight: 600; color: var(--text-primary);">Credits Available</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Features Bar -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1rem; margin-bottom: 2.5rem;">
                <div style="background: rgba(255, 255, 255, 0.03); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem 1.25rem; display: flex; align-items: center; gap: 0.8rem;">
                    <span style="font-size: 1.4rem;">⚡</span>
                    <div>
                        <div style="font-weight: 700; font-size: 0.95rem;">Instant Top-Up</div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary);">Credits added within 500ms</div>
                    </div>
                </div>
                <div style="background: rgba(255, 255, 255, 0.03); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem 1.25rem; display: flex; align-items: center; gap: 0.8rem;">
                    <span style="font-size: 1.4rem;">🔒</span>
                    <div>
                        <div style="font-weight: 700; font-size: 0.95rem;">Secure LKR Checkout</div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary);">Cards & Mobile Wallets</div>
                    </div>
                </div>
                <div style="background: rgba(255, 255, 255, 0.03); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem 1.25rem; display: flex; align-items: center; gap: 0.8rem;">
                    <span style="font-size: 1.4rem;">🎁</span>
                    <div>
                        <div style="font-weight: 700; font-size: 0.95rem;">Bonus Credit Rewards</div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary);">Up to 20% extra free credits</div>
                    </div>
                </div>
            </div>

            <!-- Credit Package Tier Cards Grid -->
            <div class="packages-grid">
                
                <%
                    if (packagesList != null && !packagesList.isEmpty()) {
                        for (CreditPackage pkg : packagesList) {
                            boolean isPopular = pkg.getName().toLowerCase().contains("fan") && !pkg.getName().toLowerCase().contains("super");
                            boolean isSuper = pkg.getName().toLowerCase().contains("super");
                            boolean isMega = pkg.getName().toLowerCase().contains("mega");
                            String icon = isPopular ? "🔥" : (isSuper ? "👑" : (isMega ? "💎" : "🌱"));
                %>
                    <div class="package-card <%= isPopular ? "popular" : "" %>">
                        <% if (isPopular) { %><div class="popular-ribbon">⭐ Most Popular</div><% } %>
                        <div class="package-header">
                            <div class="package-icon"><%= icon %></div>
                            <h3 class="package-title"><%= pkg.getName() %></h3>
                            <div class="credits-count"><%= pkg.getCreditAmount() %> <span>Credits</span></div>
                            <% if (pkg.getBonusCredits() > 0) { %>
                                <span class="bonus-badge">🎁 +<%= pkg.getBonusCredits() %> FREE Bonus Credits</span>
                            <% } else { %>
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">Standard Voting Pack</div>
                            <% } %>
                            <div class="package-price">Rs. <%= (int)pkg.getPriceLkr() %> <span style="font-size: 0.9rem; color: var(--text-secondary);">LKR</span></div>
                            <div class="price-per-vote">Rs. <%= String.format("%.2f", pkg.getPriceLkr() / pkg.getTotalCredits()) %> per vote</div>
                        </div>
                        <ul class="package-features">
                            <li><%= pkg.getTotalCredits() %> Total Voting Credits</li>
                            <li>Valid for all Season Episodes</li>
                            <li>Sub-500ms Instant Processing</li>
                            <li>Voter Badge & Receipt Log</li>
                        </ul>
                        <button class="btn <%= isPopular ? "btn-primary" : (isSuper ? "btn-violet" : "btn-outline") %> btn-block" 
                                onclick="openCheckoutModal(<%= pkg.getPackageId() %>, '<%= pkg.getName() %>', <%= pkg.getCreditAmount() %>, <%= pkg.getBonusCredits() %>, <%= (int)pkg.getPriceLkr() %>)">
                            🛒 Buy <%= pkg.getName() %> (Rs. <%= (int)pkg.getPriceLkr() %>)
                        </button>
                    </div>
                <%
                        }
                    } else {
                %>
                <!-- Fallback Mock Packages (Used when running standalone without DB) -->
                <!-- Package 1: Starter Pack -->
                <div class="package-card">
                    <div class="package-header">
                        <div class="package-icon">🌱</div>
                        <h3 class="package-title">Starter Pack</h3>
                        <div class="credits-count">10 <span>Credits</span></div>
                        <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;">Standard Voting Pack</div>
                        <div class="package-price">Rs. 250 <span style="font-size: 0.9rem; color: var(--text-secondary);">LKR</span></div>
                        <div class="price-per-vote">Rs. 25.00 per vote</div>
                    </div>
                    <ul class="package-features">
                        <li>10 Paid Voting Credits</li>
                        <li>Valid for all Season Episodes</li>
                        <li>Sub-500ms Instant Processing</li>
                        <li>Standard Voter Badge</li>
                    </ul>
                    <button class="btn btn-outline btn-block" onclick="openCheckoutModal(1, 'Starter Pack', 10, 0, 250)">
                        🛒 Buy Now (Rs. 250)
                    </button>
                </div>

                <!-- Package 2: Fan Pack (POPULAR) -->
                <div class="package-card popular">
                    <div class="popular-ribbon">⭐ Most Popular</div>
                    <div class="package-header">
                        <div class="package-icon" style="background: rgba(245, 158, 11, 0.2); color: var(--accent-gold);">🔥</div>
                        <h3 class="package-title">Fan Pack</h3>
                        <div class="credits-count">50 <span>Credits</span></div>
                        <span class="bonus-badge">🎁 +5 FREE Bonus Credits</span>
                        <div class="package-price">Rs. 1,000 <span style="font-size: 0.9rem; color: var(--text-secondary);">LKR</span></div>
                        <div class="price-per-vote">Rs. 18.18 per vote (Save 27%)</div>
                    </div>
                    <ul class="package-features">
                        <li>55 Total Credits (50 + 5 Free)</li>
                        <li>High Priority Vote Queue</li>
                        <li>Valid for Elimination Shows</li>
                        <li>Fan Voter Badge & Profile Highlights</li>
                    </ul>
                    <button class="btn btn-primary btn-block" onclick="openCheckoutModal(2, 'Fan Pack', 50, 5, 1000)">
                        🚀 Buy Fan Pack (Rs. 1,000)
                    </button>
                </div>

                <!-- Package 3: Super Fan Pack -->
                <div class="package-card">
                    <div class="package-header">
                        <div class="package-icon" style="background: rgba(139, 92, 246, 0.2); color: var(--accent-violet);">👑</div>
                        <h3 class="package-title">Super Fan Pack</h3>
                        <div class="credits-count">150 <span>Credits</span></div>
                        <span class="bonus-badge" style="background: rgba(139, 92, 246, 0.2); color: var(--accent-violet); border-color: rgba(139, 92, 246, 0.3);">🎁 +25 FREE Bonus Credits</span>
                        <div class="package-price">Rs. 2,500 <span style="font-size: 0.9rem; color: var(--text-secondary);">LKR</span></div>
                        <div class="price-per-vote">Rs. 14.28 per vote (Save 42%)</div>
                    </div>
                    <ul class="package-features">
                        <li>175 Total Credits (150 + 25 Free)</li>
                        <li>VIP Priority Ingestion Queue</li>
                        <li>Valid for Grand Finale Live Voting</li>
                        <li>Super Fan Gold Profile Badge</li>
                    </ul>
                    <button class="btn btn-violet btn-block" onclick="openCheckoutModal(3, 'Super Fan Pack', 150, 25, 2500)">
                        ⚡ Buy Super Fan (Rs. 2,500)
                    </button>
                </div>

                <!-- Package 4: Mega Producer Pack -->
                <div class="package-card">
                    <div class="package-header">
                        <div class="package-icon" style="background: rgba(6, 182, 212, 0.2); color: var(--accent-cyan);">💎</div>
                        <h3 class="package-title">Mega Producer</h3>
                        <div class="credits-count">300 <span>Credits</span></div>
                        <span class="bonus-badge" style="background: rgba(6, 182, 212, 0.2); color: var(--accent-cyan); border-color: rgba(6, 182, 212, 0.3);">🎁 +60 FREE Bonus Credits</span>
                        <div class="package-price">Rs. 5,000 <span style="font-size: 0.9rem; color: var(--text-secondary);">LKR</span></div>
                        <div class="price-per-vote">Rs. 13.88 per vote (Save 45%)</div>
                    </div>
                    <ul class="package-features">
                        <li>360 Total Credits (300 + 60 Free)</li>
                        <li>Maximum Voting Ingestion Power</li>
                        <li>Dedicated Producer Support Line</li>
                        <li>Producer Badge & Hall of Fame</li>
                    </ul>
                    <button class="btn btn-outline btn-block" onclick="openCheckoutModal(4, 'Mega Producer Pack', 300, 60, 5000)" style="border-color: var(--accent-cyan); color: var(--accent-cyan);">
                        💎 Buy Mega Pack (Rs. 5,000)
                    </button>
                </div>
                <% } %>

            </div>

            <!-- Alternative Quick Checkout Callout -->
            <div class="glass-card" style="margin-top: 3.5rem; text-align: center; background: linear-gradient(135deg, rgba(31, 41, 55, 0.6) 0%, rgba(15, 23, 42, 0.8) 100%);">
                <h3 style="font-size: 1.4rem; margin-bottom: 0.5rem;">Need a Dedicated Payment Page?</h3>
                <p style="color: var(--text-secondary); max-width: 600px; margin: 0 auto 1.25rem;">
                    You can also proceed to our standalone checkout simulator for full gateway breakdown and multi-payment channel support.
                </p>
                <a href="checkout.jsp?pkg=fan_pack" class="btn btn-outline">
                    🖥️ Open Full Standalone Checkout Page →
                </a>
            </div>

        </div>
    </main>

    <!-- Payment Checkout Modal Window -->
    <div class="modal-overlay" id="checkoutModal">
        <div class="modal-container">
            <div class="modal-header">
                <div>
                    <h3 style="font-size: 1.25rem;" id="modal-pkg-name">Checkout & Payment</h3>
                    <div style="font-size: 0.82rem; color: var(--text-secondary);">VoteSphere Lanka Secure Payment Gateway</div>
                </div>
                <button class="modal-close" onclick="closeCheckoutModal()">✕</button>
            </div>
            
            <!-- ProcessPaymentServlet Form -->
            <form action="process-payment" method="POST" id="modalPaymentForm">
                <input type="hidden" name="package_id" id="modal_package_id" value="2">
                <input type="hidden" name="pkg_name" id="modal_pkg_name_input" value="Fan Pack">
                <input type="hidden" name="amount" id="modal_amount_input" value="1000">
                <input type="hidden" name="credits" id="modal_credits_input" value="55">

                <div class="modal-body">
                    
                    <!-- Package Summary Bar -->
                    <div class="summary-box" style="margin-bottom: 1.5rem;">
                        <div class="summary-row">
                            <span>Selected Tier:</span>
                            <strong id="modal-summary-tier" style="color: var(--text-primary);">Fan Pack</strong>
                        </div>
                        <div class="summary-row">
                            <span>Credits Included:</span>
                            <strong id="modal-summary-credits">50 + 5 Bonus = 55 Credits</strong>
                        </div>
                        <div class="summary-row total">
                            <span>Total Payable (LKR):</span>
                            <span id="modal-summary-price" class="gradient-text" style="font-size: 1.4rem;">Rs. 1,000.00</span>
                        </div>
                    </div>

                    <!-- Payment Method Selector Tabs -->
                    <label class="form-label">Select Payment Channel</label>
                    <div class="payment-methods-grid">
                        <div class="method-option">
                            <input type="radio" name="payment_method" id="m_card" value="card" checked onclick="togglePayMethod('card')">
                            <label for="m_card" class="method-label">
                                <span class="method-icon">💳</span>
                                <span style="font-weight: 600; font-size: 0.88rem;">Credit / Debit Card</span>
                                <span style="font-size: 0.75rem; color: var(--text-muted);">Visa, Mastercard</span>
                            </label>
                        </div>
                        <div class="method-option">
                            <input type="radio" name="payment_method" id="m_mobile" value="mobile" onclick="togglePayMethod('mobile')">
                            <label for="m_mobile" class="method-label">
                                <span class="method-icon">📱</span>
                                <span style="font-weight: 600; font-size: 0.88rem;">Mobile Wallet</span>
                                <span style="font-size: 0.75rem; color: var(--text-muted);">eZ Cash / Genie</span>
                            </label>
                        </div>
                    </div>

                    <!-- Card Form Fields -->
                    <div id="card-fields">
                        <div class="form-group">
                            <label class="form-label">Cardholder Name</label>
                            <input type="text" class="form-control" name="card_name" placeholder="Kasun Perera" value="Kasun Perera">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Card Number</label>
                            <input type="text" class="form-control" name="card_num" placeholder="4111 2222 3333 4444" value="4532 •••• •••• 8892">
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Expiry Date</label>
                                <input type="text" class="form-control" name="card_exp" placeholder="MM/YY" value="08/28">
                            </div>
                            <div class="form-group">
                                <label class="form-label">CVV Code</label>
                                <input type="password" class="form-control" name="card_cvv" placeholder="123" maxlength="4" value="882">
                            </div>
                        </div>
                    </div>

                    <!-- Mobile Wallet Fields -->
                    <div id="mobile-fields" style="display: none;">
                        <div class="form-group">
                            <label class="form-label">Mobile Wallet Operator</label>
                            <select class="form-control" name="mobile_provider">
                                <option value="eZ Cash">eZ Cash (Dialog)</option>
                                <option value="mCash">mCash (Mobitel)</option>
                                <option value="Genie">Genie Wallet</option>
                                <option value="FriMi">FriMi (NTB)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Mobile Number</label>
                            <input type="text" class="form-control" name="mobile_num" placeholder="0771234567" value="0779842105">
                        </div>
                    </div>

                    <!-- Submit Button -->
                    <div style="margin-top: 1.75rem;">
                        <button type="submit" class="btn btn-primary btn-block" id="btn-process-pay">
                            🔒 Confirm Payment & Add Credits
                        </button>
                        <div style="text-align: center; font-size: 0.78rem; color: var(--text-muted); margin-top: 0.75rem;">
                            🔒 256-Bit Encrypted Payment. Credits added instantly to your VoteSphere wallet.
                        </div>
                    </div>

                </div>
            </form>
        </div>
    </div>

    <!-- Footer -->
    <jsp:include page="includes/footer.jsp" />

    <!-- Interactive JavaScript Logic -->
    <script>
        function openCheckoutModal(id, name, credits, bonus, price) {
            const totalCredits = credits + bonus;
            
            document.getElementById('modal_package_id').value = id;
            document.getElementById('modal_pkg_name_input').value = name;
            document.getElementById('modal_amount_input').value = price;
            document.getElementById('modal_credits_input').value = totalCredits;

            document.getElementById('modal-pkg-name').innerText = `Checkout - ${name}`;
            document.getElementById('modal-summary-tier').innerText = name;
            document.getElementById('modal-summary-credits').innerText = `${credits} Base + ${bonus} Bonus = ${totalCredits} Credits`;
            document.getElementById('modal-summary-price').innerText = `Rs. ${price.toLocaleString()}.00 LKR`;
            
            document.getElementById('checkoutModal').classList.add('active');
        }

        function closeCheckoutModal() {
            document.getElementById('checkoutModal').classList.remove('active');
        }

        function togglePayMethod(method) {
            if (method === 'card') {
                document.getElementById('card-fields').style.display = 'block';
                document.getElementById('mobile-fields').style.display = 'none';
            } else {
                document.getElementById('card-fields').style.display = 'none';
                document.getElementById('mobile-fields').style.display = 'block';
            }
        }
    </script>
</body>
</html>
