package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.bootstrap.DolphinPlatformBootstrap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HealthCheckServlet extends HttpServlet  {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(DolphinPlatformBootstrap.getInstance().isUp()) {
            resp.getWriter().print("OK");
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }
}
