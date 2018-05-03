package com.canoo.dp.impl.platform.server.metrics.servlet;

import com.canoo.dp.impl.platform.core.http.ConnectionUtils;
import com.canoo.platform.core.PlatformConfiguration;
import io.prometheus.client.exporter.common.TextFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrometheusPushGatewayProxyServlet extends HttpServlet {

    private final String pushGatewayEndpoint;

    public PrometheusPushGatewayProxyServlet(final PlatformConfiguration configuration) {
        pushGatewayEndpoint = null;
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);

        final String url = null;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Content-Type", TextFormat.CONTENT_TYPE_004);
        if (!req.getMethod().equals("DELETE")) {
            connection.setDoOutput(true);
        }
        connection.setRequestMethod(req.getMethod());

        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        connection.connect();

        try {
            if (!req.getMethod().equals("DELETE")) {
                final String content = ConnectionUtils.readUTF8Content(req.getInputStream());
                ConnectionUtils.writeUTF8Content(connection.getOutputStream(), content);
            }

            int response = connection.getResponseCode();
            if (response != HttpURLConnection.HTTP_ACCEPTED) {
                throw new IOException("Response code from " + url + " was " + response);
            }
        } finally {
            connection.disconnect();
        }
    }
}
