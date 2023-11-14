package com.mfahproj.webapp.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import com.mfahproj.webapp.Database;
import com.mfahproj.webapp.Session;
import com.mfahproj.webapp.Utils;
import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.mfahproj.webapp.models.Notification;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NotificationsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        get(exchange);
    }

    // Handles GET requests from the client.
    private void get(HttpExchange exchange) throws IOException {
        // Check if a session exists.
        String sessionId = Session.extractSessionId(exchange);
        Employee employee = Session.getEmployeeSession(sessionId);
        Member member = Session.getMemberSession(sessionId);
        if (employee == null && member == null) {
            // No prior session, send to login page.
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Loads the HTML for notifications.
        String response = Utils.dynamicNavigator(exchange, "notifications.html");

        List<String> sections = new Vector<String>();
        if (employee != null) {
            sections = NotificationsHandler.employeeNotifications(employee);
        } else {
            sections = NotificationsHandler.memberNotifications(member);
            response = MemberHandler.setNotifications(member, response);
        }

        // Account for no notifications.
        if (sections.size() == 0) {
            response = response.replace("{{sections}}", "<section><p>None.</p></section>");
        } else {
            String html_sections = String.join("\n", sections);
            response = response.replace("{{sections}}", html_sections);
        }

        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        return;

    }

    // Sort notifications based on if they have been checked or not, then on date.
    private static List<Notification> sortNotifications(List<Notification> notifications) {
        Collections.sort(notifications, (a, b) -> {
            // First, sort by hasChecked status
            if (a.getChecked() && !b.getChecked()) {
                return 1;
            } else if (!a.getChecked() && b.getChecked()) {
                return -1;
            } else {
                // Sort by time.
                return b.getTime().compareTo(a.getTime());
            }
        });

        return notifications;
    }

    // Get employee notifications as sections in HTML.
    private static List<String> employeeNotifications(Employee employee) {
        List<Notification> notifications = new Vector<Notification>();
        notifications = NotificationsHandler.sortNotifications(notifications);

        // Update that they have been seen.
        for (Notification notify : notifications) {
            notify.setChecked(true);
        }

        if (notifications.size() == 0) {
            return new Vector<String>();
        }

        // Update that they have been seen.
        Database.editNotificationsBatch(notifications, employee.getEmployeeId());

        return notifications.stream().map(Notification::asSection).collect(Collectors.toList());
    }

    // Get member notifications as sections in HTML.
    private static List<String> memberNotifications(Member member) {
        List<Notification> notifications = Database.getNotifications(member.getMemberId());
        notifications = NotificationsHandler.sortNotifications(notifications);

        // Update that they have been seen.
        for (Notification notify : notifications) {
            notify.setChecked(true);
        }

        Database.editNotificationsBatch(notifications, member.getMemberId());

        return notifications.stream().map(Notification::asSection).collect(Collectors.toList());
    }
}
