package com.votingsystem.controller;

import com.google.gson.Gson;
import com.votingsystem.dao.ContestantDAO;
import com.votingsystem.model.Contestant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/contestants")
public class ContestantServlet extends HttpServlet {
    private final ContestantDAO contestantDAO = new ContestantDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int showId = 1;
        String showParam = request.getParameter("showId");
        if (showParam != null && !showParam.isEmpty()) {
            try {
                showId = Integer.parseInt(showParam);
            } catch (NumberFormatException ignored) {}
        }

        List<Contestant> list = contestantDAO.getContestantsByShow(showId);
        String jsonResponse = gson.toJson(list);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                String code = request.getParameter("code");
                String name = request.getParameter("name");
                String bio = request.getParameter("bio");
                String image = request.getParameter("image");
                String status = request.getParameter("status");
                if (status == null || status.trim().isEmpty()) {
                    status = "ACTIVE";
                }

                int showId = 1;
                String showParam = request.getParameter("showId");
                if (showParam != null && !showParam.isEmpty()) {
                    showId = Integer.parseInt(showParam);
                }

                Contestant contestant = new Contestant(code, name, bio, image, status, showId);
                boolean success = contestantDAO.addContestant(contestant);
                result.put("success", success);
                result.put("id", contestant.getId());
                result.put("message", success ? "Contestant added successfully" : "Failed to add contestant");

            } else if ("update".equals(action) || "edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String code = request.getParameter("code");
                String name = request.getParameter("name");
                String bio = request.getParameter("bio");
                String image = request.getParameter("image");
                String status = request.getParameter("status");

                int showId = 1;
                String showParam = request.getParameter("showId");
                if (showParam != null && !showParam.isEmpty()) {
                    showId = Integer.parseInt(showParam);
                }

                Contestant contestant = new Contestant(id, code, name, bio, image, status, showId);
                boolean success = contestantDAO.updateContestant(contestant);
                result.put("success", success);
                result.put("message", success ? "Contestant updated successfully" : "Failed to update contestant");

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = contestantDAO.deleteContestant(id);
                result.put("success", success);
                result.put("message", success ? "Contestant removed successfully" : "Failed to remove contestant");

            } else if ("updateStatus".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String status = request.getParameter("status");
                boolean success = contestantDAO.updateContestantStatus(id, status);
                result.put("success", success);
                result.put("message", success ? "Status updated" : "Failed to update status");

            } else if ("updateBio".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String bio = request.getParameter("bio");
                boolean success = contestantDAO.updateContestantBio(id, bio);
                result.put("success", success);
                result.put("message", success ? "Bio updated" : "Failed to update bio");

            } else {
                result.put("success", false);
                result.put("message", "Invalid action specified: " + action);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Server error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}
