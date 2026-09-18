package queueless.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QueueToken implements Comparable<QueueToken> {
    private final int tokenNumber;
    private final Student student;
    private final ServiceType serviceType;
    private final boolean priority;
    private final LocalDateTime createdAt;
    private TokenStatus status;

    public QueueToken(int tokenNumber, Student student, ServiceType serviceType, boolean priority) {
        this.tokenNumber = tokenNumber;
        this.student = student;
        this.serviceType = serviceType;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.status = TokenStatus.WAITING;
    }

    public int getTokenNumber() { return tokenNumber; }
    public Student getStudent() { return student; }
    public ServiceType getServiceType() { return serviceType; }
    public boolean isPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }

    public String csv() {
        return tokenNumber + "," + clean(student.getId()) + "," + clean(student.getName()) + ","
                + serviceType.name() + "," + priority + "," + createdAt + "," + status.name();
    }

    private String clean(String s) {
        return s.replace(",", " ");
    }

    public String createdTime() {
        return createdAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public int compareTo(QueueToken other) {
        if (priority != other.priority) {
            return priority ? -1 : 1;
        }
        return createdAt.compareTo(other.createdAt);
    }
}
