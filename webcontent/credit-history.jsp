<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.votesphere.model.Transaction, com.votesphere.model.UserCredit, java.text.SimpleDateFormat" %>
<%
    UserCredit userCredit = (UserCredit) request.getAttribute("userCredit");
    int currentBalance = (userCredit != null) ? userCredit.getBalance() : 80;

    @SuppressWarnings("unchecked")
    List<Transaction> txnList = (List<Transaction>) request.getAttribute("transactions");

    int totalPurchasedCredits = 0;
    double totalSpentLkr = 0.0;

    if (txnList != null) {
        for (Transaction t : txnList) {
            totalPurchasedCredits += t.getCreditsAdded();
            totalSpentLkr += t.getAmountLkr();
        }
    } else {
        totalPurchasedCredits = 235;
        totalSpentLkr = 3750.0;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallet & Transaction History - VoteSphere Lanka</title>
    <meta name="description" content="View user credit wallet balance, top-up logs, and paid vote transaction history on VoteSphere Lanka.">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Header Navigation -->
    <jsp:include page="includes/navbar.jsp" />

    <main class="main-content">
        <div class="container animate-fade-in">
            
            <!-- Page Title Header -->
            <div style="display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 1.5rem; margin-bottom: 2rem;">
                <div>
                    <h1 style="font-size: 2.2rem;">User Wallet & <span class="gradient-text">Transaction History</span></h1>
                    <p class="subtitle">Track credit top-ups, paid voting activity logs, and real-time wallet balance.</p>
                </div>
                <div>
                    <a href="buy-credits.jsp" class="btn btn-primary">
                        ➕ Top Up Credits
                    </a>
                </div>
            </div>

            <!-- Wallet Overview Stats Grid -->
            <div class="stats-grid">
                
                <div class="stat-card">
                    <div class="stat-icon gold">👛</div>
                    <div>
                        <div class="stat-number gradient-text"><%= currentBalance %></div>
                        <div class="stat-title">Current Available Credits</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon violet">💎</div>
                    <div>
                        <div class="stat-number"><%= totalPurchasedCredits %></div>
                        <div class="stat-title">Lifetime Credits Purchased</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon cyan">🗳️</div>
                    <div>
                        <div class="stat-number">155</div>
                        <div class="stat-title">Paid Votes Cast</div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon emerald">💳</div>
                    <div>
                        <div class="stat-number" style="font-size: 1.3rem;">Rs. <%= String.format("%,d", (int)totalSpentLkr) %></div>
                        <div class="stat-title">Total Amount Spent (LKR)</div>
                    </div>
                </div>

            </div>

            <!-- Transaction Logs Container -->
            <div class="glass-card">
                
                <!-- Toolbar: Search & Filters -->
                <div class="toolbar-container">
                    <div class="search-box">
                        <span class="search-icon">🔍</span>
                        <input type="text" class="form-control" id="searchTxnInput" placeholder="Search by ID or description..." onkeyup="filterTransactions()">
                    </div>
                    
                    <div class="filter-group">
                        <select class="form-control" id="filterTypeSelect" onchange="filterTransactions()" style="min-width: 170px;">
                            <option value="ALL">All Transaction Types</option>
                            <option value="TOP_UP">Credit Top-ups</option>
                            <option value="VOTE_SPENT">Votes Spent</option>
                            <option value="BONUS">Bonus Rewards</option>
                        </select>

                        <button class="btn btn-outline btn-sm" onclick="alert('Exporting transaction statement in CSV format...')">
                            📥 Export CSV
                        </button>
                    </div>
                </div>

                <!-- Transaction Logs Table -->
                <div class="table-responsive">
                    <table class="data-table" id="txnTable">
                        <thead>
                            <tr>
                                <th>Date & Time</th>
                                <th>Transaction ID</th>
                                <th>Activity / Package</th>
                                <th>Type</th>
                                <th>Credits (+/-)</th>
                                <th>Amount (LKR)</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            
                        <%
                            if (txnList != null && !txnList.isEmpty()) {
                                for (Transaction t : txnList) {
                                    String dateStr = (t.getTransactionDate() != null) ? sdf.format(t.getTransactionDate()) : "2026-09-14 13:24:57";
                                    String pkgName = (t.getPackageName() != null && !t.getPackageName().isEmpty()) ? t.getPackageName() : "Credit Top-up";
                        %>
                            <tr data-type="TOP_UP">
                                <td style="font-size: 0.85rem; color: var(--text-secondary);"><%= dateStr %></td>
                                <td class="txn-id"><%= t.getTransactionId() %></td>
                                <td>
                                    <strong><%= pkgName %></strong>
                                    <div style="font-size: 0.78rem; color: var(--text-muted);"><%= t.getPaymentMethod() %></div>
                                </td>
                                <td><span class="badge badge-success">Credit Top-up</span></td>
                                <td class="credit-added">+<%= t.getCreditsAdded() %> Credits</td>
                                <td style="font-weight: 600;">Rs. <%= String.format("%,d", (int)t.getAmountLkr()) %>.00</td>
                                <td><span class="badge badge-success">● <%= t.getStatus() %></span></td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <!-- Fallback Mock Logs when accessed directly without servlet -->
                            <tr data-type="TOP_UP">
                                <td style="font-size: 0.85rem; color: var(--text-secondary);">2026-09-14 13:24:57</td>
                                <td class="txn-id">VSL-TXN-9842105</td>
                                <td>
                                    <strong>Fan Pack Purchase</strong>
                                    <div style="font-size: 0.78rem; color: var(--text-muted);">Visa ending in 8892</div>
                                </td>
                                <td><span class="badge badge-success">Credit Top-up</span></td>
                                <td class="credit-added">+55 Credits</td>
                                <td style="font-weight: 600;">Rs. 1,000.00</td>
                                <td><span class="badge badge-success">● COMPLETED</span></td>
                            </tr>

                            <tr data-type="VOTE_SPENT">
                                <td style="font-size: 0.85rem; color: var(--text-secondary);">2026-09-14 11:15:30</td>
                                <td class="txn-id">VSL-VOT-4102981</td>
                                <td>
                                    <strong>Paid Vote: Episode 8 Live</strong>
                                    <div style="font-size: 0.78rem; color: var(--text-muted);">Contestant #04 (Nimali Perera)</div>
                                </td>
                                <td><span class="badge badge-pending" style="background: rgba(139, 92, 246, 0.15); color: var(--accent-violet); border-color: rgba(139, 92, 246, 0.3);">Vote Deduction</span></td>
                                <td class="credit-deducted">-10 Credits</td>
                                <td>-</td>
                                <td><span class="badge badge-success">● ACCEPTED</span></td>
                            </tr>

                            <tr data-type="TOP_UP">
                                <td style="font-size: 0.85rem; color: var(--text-secondary);">2026-09-07 19:40:12</td>
                                <td class="txn-id">VSL-TXN-8723910</td>
                                <td>
                                    <strong>Super Fan Pack Purchase</strong>
                                    <div style="font-size: 0.78rem; color: var(--text-muted);">eZ Cash (0779842105)</div>
                                </td>
                                <td><span class="badge badge-success">Credit Top-up</span></td>
                                <td class="credit-added">+175 Credits</td>
                                <td style="font-weight: 600;">Rs. 2,500.00</td>
                                <td><span class="badge badge-success">● COMPLETED</span></td>
                            </tr>

                            <tr data-type="BONUS">
                                <td style="font-size: 0.85rem; color: var(--text-secondary);">2026-09-01 10:00:00</td>
                                <td class="txn-id">VSL-BON-1002934</td>
                                <td>
                                    <strong>Season Launch Loyalty Bonus</strong>
                                    <div style="font-size: 0.78rem; color: var(--text-muted);">Early Voter Reward</div>
                                </td>
                                <td><span class="badge badge-pending" style="background: rgba(6, 182, 212, 0.15); color: var(--accent-cyan); border-color: rgba(6, 182, 212, 0.3);">Bonus Credit</span></td>
                                <td class="credit-added">+5 Credits</td>
                                <td>-</td>
                                <td><span class="badge badge-success">● COMPLETED</span></td>
                            </tr>

                            <tr data-type="TOP_UP">
                                <td style="font-size: 0.85rem; color: var(--text-secondary);">2026-08-25 14:10:05</td>
                                <td class="txn-id">VSL-TXN-7612049</td>
                                <td>
                                    <strong>Starter Pack Purchase</strong>
                                    <div style="font-size: 0.78rem; color: var(--text-muted);">Mastercard ending in 1102</div>
                                </td>
                                <td><span class="badge badge-success">Credit Top-up</span></td>
                                <td class="credit-added">+10 Credits</td>
                                <td style="font-weight: 600;">Rs. 250.00</td>
                                <td><span class="badge badge-success">● COMPLETED</span></td>
                            </tr>
                        <% } %>

                        </tbody>
                    </table>
                </div>

                <!-- Table Pagination -->
                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1.5rem; flex-wrap: wrap; gap: 1rem; font-size: 0.88rem; color: var(--text-secondary);">
                    <div>Showing <strong style="color: var(--text-primary);">1 to <%= (txnList != null && !txnList.isEmpty()) ? txnList.size() : 5 %></strong> of <%= (txnList != null && !txnList.isEmpty()) ? txnList.size() : 18 %> transaction logs</div>
                    <div style="display: flex; gap: 0.5rem;">
                        <button class="btn btn-outline btn-sm" disabled style="opacity: 0.5;">Previous</button>
                        <button class="btn btn-primary btn-sm">1</button>
                        <button class="btn btn-outline btn-sm">2</button>
                        <button class="btn btn-outline btn-sm">3</button>
                        <button class="btn btn-outline btn-sm">Next</button>
                    </div>
                </div>

            </div>

        </div>
    </main>

    <!-- Footer -->
    <jsp:include page="includes/footer.jsp" />

    <!-- JavaScript Filter Functionality -->
    <script>
        function filterTransactions() {
            const query = document.getElementById('searchTxnInput').value.toLowerCase();
            const filterType = document.getElementById('filterTypeSelect').value;
            const rows = document.querySelectorAll('#txnTable tbody tr');

            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                const rowType = row.getAttribute('data-type');

                const matchesQuery = text.includes(query);
                const matchesType = (filterType === 'ALL' || rowType === filterType);

                if (matchesQuery && matchesType) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        }
    </script>
</body>
</html>
