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

        if ("updateStatus".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");
            boolean success = contestantDAO.updateContestantStatus(id, status);
            result.put("success", success);
        } else if ("updateBio".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String bio = request.getParameter("bio");
            boolean success = contestantDAO.updateContestantBio(id, bio);
            result.put("success", success);
        } else if ("add".equals(action)) {
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String bio = request.getParameter("bio");
            String image = request.getParameter("image");
            int showId = Integer.parseInt(request.getParameter("showId"));

            Contestant contestant = new Contestant(code, name, bio, image, "ACTIVE", showId);
            boolean success = contestantDAO.addContestant(contestant);
            result.put("success", success);
        } else {
            result.put("success", false);
            result.put("message", "Invalid action specified");
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}
