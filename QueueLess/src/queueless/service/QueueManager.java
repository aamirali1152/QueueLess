package queueless.service;

import queueless.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueManager {
    private final Map<ServiceType, PriorityQueue<QueueToken>> waiting = new EnumMap<>(ServiceType.class);
    private final List<QueueToken> history = new ArrayList<>();
    private final Map<ServiceType, Integer> counters = new EnumMap<>(ServiceType.class);

    public QueueManager() {
        for (ServiceType type : ServiceType.values()) {
            waiting.put(type, new PriorityQueue<>());
            counters.put(type, 1);
        }
    }

    public synchronized QueueToken issueToken(Student student, ServiceType type, boolean priority) {
        if (student == null || type == null) {
            throw new IllegalArgumentException("Student and service are required.");
        }
        int number = counters.get(type);
        counters.put(type, number + 1);

        QueueToken token = new QueueToken(number, student, type, priority);
        waiting.get(type).offer(token);
        history.add(token);
        return token;
    }

    public synchronized QueueToken callNext(ServiceType type) {
        QueueToken token = waiting.get(type).poll();
        if (token == null) return null;
        token.setStatus(TokenStatus.SERVING);
        return token;
    }

    public synchronized boolean complete(QueueToken token) {
        if (token == null || token.getStatus() != TokenStatus.SERVING) return false;
        token.setStatus(TokenStatus.COMPLETED);
        return true;
    }

    public synchronized boolean cancel(QueueToken token) {
        if (token == null || token.getStatus() != TokenStatus.WAITING) return false;
        boolean removed = waiting.get(token.getServiceType()).remove(token);
        if (removed) token.setStatus(TokenStatus.CANCELLED);
        return removed;
    }

    public synchronized int peopleAhead(QueueToken target) {
        if (target == null || target.getStatus() != TokenStatus.WAITING) return 0;
        int count = 0;
        for (QueueToken token : waiting.get(target.getServiceType())) {
            if (token.compareTo(target) < 0) count++;
        }
        return count;
    }

    public synchronized int estimatedMinutes(QueueToken target, int minutesPerPerson) {
        return peopleAhead(target) * minutesPerPerson;
    }

    public synchronized List<QueueToken> active(ServiceType type) {
        List<QueueToken> result = new ArrayList<>(waiting.get(type));
        Collections.sort(result);
        return result;
    }

    public synchronized List<QueueToken> history() {
        return new ArrayList<>(history);
    }

    public synchronized int waitingCount(ServiceType type) {
        return waiting.get(type).size();
    }
}
