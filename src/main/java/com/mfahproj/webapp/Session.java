package com.mfahproj.webapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.mfahproj.webapp.models.Employee;
import com.mfahproj.webapp.models.Member;
import com.sun.net.httpserver.HttpExchange;

public class Session {
    // Time until sessions expires with no refreshing.
    // 1000ms * 60 (seconds) * 15 = 15 minutes.
    private static final long TIMEOUT = 1000 * 60 * 15;

    // Holds all of the current employee and member sessions and timestamp.
    private static Map<String, Member> member_sessions = new HashMap<>();
    private static Map<String, Employee> employee_sessions = new HashMap<>();

    // Controls access to the share referenes.
    private static Semaphore semMember = new Semaphore(1, true);
    private static Semaphore semEmployee = new Semaphore(1, true);

    // Starts a scheduled task the cleans up expired sessions.
    public static void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            public void run() {
                System.out.println("Purging expired sessions.");
                Session.purgeExpiredSessions();
                System.out.println("Purging expired sessions, complete.");
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.HOURS);
    }

    // Check if a session exists locally.
    public static boolean exists(String sessionId) {
        // Check if a member exists.
        try {
            Session.semMember.acquire();
            if (Session.member_sessions.get(sessionId) != null) {
                return true;
            }

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock for get member session.");
        } finally {
            Session.semMember.release();
        }

        // Check if a employee exists.
        try {
            Session.semEmployee.acquire();
            if (Session.employee_sessions.get(sessionId) != null) {
                return true;
            }

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock for get employee session.");
        } finally {
            Session.semEmployee.release();
        }

        return false;
    }

    // Check if a timestamp has reached expiration.
    private static boolean isExpired(long lastLong) {
        long oldest = System.currentTimeMillis() - Session.TIMEOUT;
        return lastLong < oldest;
    }

    // Check if a session has reached expiration.
    public static boolean isExpired(String sessionId) {
        if (sessionId == null) {
            return false;
        }

        long timestamp = 0;
        // Check if it is a member session.
        try {
            Session.semMember.acquire();
            Member member = Session.member_sessions.get(sessionId);
            if (member != null) {
                timestamp = member.getLastLogin().getTime();
            }

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock for get member session.");
        } finally {
            Session.semMember.release();
        }

        // Check if member expired.
        if (timestamp > 0 && Session.isExpired(timestamp)) {
            return true;
        }

        // Check if it is an employee session.
        try {
            Session.semEmployee.acquire();
            Employee employee = Session.employee_sessions.get(sessionId);
            if (employee != null) {
                timestamp = employee.getLastLogin().getTime();
            }

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock for get employee session.");
        } finally {
            Session.semEmployee.release();
        }

        return Session.isExpired(timestamp);
    }

    // // // // // // // // // // // // // // // // //
    // GETTERS

    // Gets a members session.
    public static Member getMemberSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }

        Member member = null;

        try {
            Session.semMember.acquire();
            member = Session.member_sessions.get(sessionId);
            if (member == null) {
                return null;
            }

            // Session expired.
            if (Session.isExpired(member.getLastLogin().getTime())) {
                Session.member_sessions.remove(sessionId);
                return null;
            }

            // Update the last login time for the employee to refresh session time.
            member.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
            Session.member_sessions.put(sessionId, member);

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock for get member session.");
        } finally {
            Session.semMember.release();
        }

        return member;
    }

    // Gets an employees session.
    public static Employee getEmployeeSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }

        Employee employee = null;

        try {
            Session.semEmployee.acquire();
            employee = Session.employee_sessions.get(sessionId);
            if (employee == null) {
                return null;
            }

            // Session expired.
            if (Session.isExpired(employee.getLastLogin().getTime())) {
                Session.employee_sessions.remove(sessionId);
                return null;
            }

            // Update the last login time for the employee to refresh session time.
            employee.setLastLogin(new java.sql.Date(System.currentTimeMillis()));
            Session.employee_sessions.put(sessionId, employee);

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock for get employee session.");
        } finally {
            Session.semEmployee.release();
        }

        return employee;
    }

    // // // // // // // // // // // // // // // // //
    // SETTERS

    // Creates a new session for a member.
    public static String newMemberSession(Member member) {
        String sessionId = UUID.randomUUID().toString();
        Session.updateMemberSession(sessionId, member);
        return sessionId;
    }

    // Creates a new session for an employee.
    public static String newEmployeeSession(Employee employee) {
        String sessionId = UUID.randomUUID().toString();
        Session.updateEmployeeSession(sessionId, employee);
        return sessionId;
    }

    // Assigns or updates a members session.
    public static void updateMemberSession(String sessionId, Member member) {
        if (sessionId == null) {
            return;
        }

        try {
            Session.semMember.acquire();
            member = Session.member_sessions.put(sessionId, member);

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock during update member session.");
        } finally {
            Session.semMember.release();
        }
    }

    // Assigns or updates an employees session.
    public static void updateEmployeeSession(String sessionId, Employee employee) {
        if (sessionId == null) {
            return;
        }

        try {
            Session.semEmployee.acquire();
            employee = Session.employee_sessions.put(sessionId, employee);

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock during update employee session.");
        } finally {
            Session.semEmployee.release();
        }
    }

    // // // // // // // // // // // // // // // // //
    // CHECKERs

    // Checks and removes any expired sessions.
    public static void purgeExpiredSessions() {
        // Obtain all current member sessions.
        Set<String> sessions = null;
        try {
            Session.semMember.acquire();
            sessions = Session.member_sessions.keySet();

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock getting all member sessions.");
        } finally {
            Session.semMember.release();
        }

        // Iterate all sessions calling 'getMemberSession', theres a check in that
        // function to remove if expired.
        if (sessions != null) {
            for (String sessionId : sessions) {
                if (Session.getMemberSession(sessionId) == null) {
                    System.out.printf("Expired session: %s\n", sessionId);
                }
            }
        }

        // Obtain all current employee sessions.
        sessions = null;
        try {
            Session.semEmployee.acquire();
            sessions = Session.employee_sessions.keySet();

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock getting all employee sessions.");
        } finally {
            Session.semEmployee.release();
        }

        // Iterate all sessions calling 'getEmployeeSession', theres a check in that
        // function to remove if expired.
        if (sessions != null) {
            for (String sessionId : sessions) {
                if (Session.getEmployeeSession(sessionId) == null) {
                    System.out.printf("Expired session: %s\n", sessionId);
                }
            }
        }
    }

    // Attempt to extract a Session Id from a request.
    public static String extractSessionId(HttpExchange exchange) {
        // Extract cookie.
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null && cookie.startsWith("SESSIONID=")) {
            // Get session info.
            String[] info = cookie.split("=");
            return info.length < 2 ? null : info[1];
        }

        return null;
    }

    // Remove all sessions by Id.
    public static void killSession(String sessionId) {
        // Remove from members.
        try {
            Session.semMember.acquire();
            Session.member_sessions.remove(sessionId);

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock killing member sessions.");
        } finally {
            Session.semMember.release();
        }

        // Remove from employees.
        try {
            Session.semEmployee.acquire();
            Session.employee_sessions.remove(sessionId);

        } catch (Exception e) {
            System.err.println("Interrupted while waiting for lock killing employee sessions.");
        } finally {
            Session.semEmployee.release();
        }
    }
}
