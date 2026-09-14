<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String currentPage = request.getRequestURI();
    int currentCredits = 25; // Dynamic user balance from wallet session/service
%>
<header class="navbar">
    <div class="container navbar-container">
        <a href="buy-credits.jsp" class="navbar-brand">
            <div class="brand-icon">VS</div>
            <span>VoteSphere <span class="gradient-text">Lanka</span></span>
            <span class="brand-tag">Paid Vote Mgmt</span>
        </a>

        <nav>
            <ul class="nav-links">
                <li>
                    <a href="buy-credits.jsp" class="nav-link <%= currentPage.contains("buy-credits.jsp") || currentPage.contains("checkout.jsp") ? "active" : "" %>">
                        💎 Credit Store
                    </a>
                </li>
                <li>
                    <a href="credit-history.jsp" class="nav-link <%= currentPage.contains("credit-history.jsp") ? "active" : "" %>">
                        📜 Wallet & History
                    </a>
                </li>
                <li>
                    <a href="#" class="nav-link" onclick="alert('Live Reality Show Voting Arena is Active! Use your credits to vote.')">
                        ⭐ Live Contestants
                    </a>
                </li>
            </ul>
        </nav>

        <div class="user-widget">
            <a href="buy-credits.jsp" class="credit-badge" title="Click to Top Up Credits">
                <span class="credit-icon">⚡</span>
                <span id="nav-credit-balance"><%= currentCredits %></span> Credits
                <span style="font-size: 0.75rem; opacity: 0.8; margin-left: 2px;">(+)</span>
            </a>
            <div class="user-avatar" title="Logged in as Public Voter (Kasun Perera)">
                KP
            </div>
        </div>
    </div>
</header>
