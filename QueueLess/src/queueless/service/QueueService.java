package queueless.service;

import queueless.model.*;

public class QueueService {
    private final QueueManager manager;

    public QueueService(QueueManager manager) {
        this.manager = manager;
    }

    public QueueToken joinQueue(String id, String name, ServiceType type, boolean priority) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("Student ID is required.");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Student name is required.");
        return manager.issueToken(new Student(id.trim(), name.trim()), type, priority);
    }

    public String status(QueueToken token) {
        if (token == null) return "No token selected.";
        if (token.getStatus() != TokenStatus.WAITING) {
            return "Token #" + token.getTokenNumber() + " is " + token.getStatus().name().toLowerCase() + ".";
        }
        int ahead = manager.peopleAhead(token);
        int eta = manager.estimatedMinutes(token, 2);
        return "Token #" + token.getTokenNumber() + " | " + ahead + " ahead | ETA ~ " + eta + " min";
    }
}
