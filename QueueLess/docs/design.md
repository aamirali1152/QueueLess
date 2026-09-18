# QueueLess Design Notes

## Architecture

Presentation Layer
-> QueueLessApp (Swing UI)

Service Layer
-> QueueService
-> QueueManager

Model Layer
-> Student
-> QueueToken
-> ServiceType
-> TokenStatus

Persistence Layer
-> QueueStore

Utility Layer
-> Validator

## Workflow

Student details
-> validation
-> select service
-> issue token
-> insert into PriorityQueue
-> calculate people ahead
-> estimate waiting time
-> staff calls next
-> serving
-> completed/cancelled

## Data Structure Choice

PriorityQueue is used because it allows priority-assisted students to be served before normal students while preserving arrival order among equal priorities.

HashMap/EnumMap is used to maintain separate queues for different service types.

ArrayList is used for history because the application needs ordered access to completed and cancelled records.

## Non-functional requirements

- Performance: queue operations should remain responsive for normal campus queue sizes.
- Usability: common actions are available through a small Swing interface.
- Reliability: invalid input is rejected and queue state is saved to CSV.
- Maintainability: responsibilities are divided among model, service, storage and utility packages.
- Error handling: invalid data and invalid queue actions produce user-facing messages.
