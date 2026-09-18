# QueueLess

## Virtual Campus Queue Management System

QueueLess is a Java Swing application for managing virtual queues for campus services. Students can take a digital token, see their queue position and estimated waiting time, and cancel a waiting token. Staff can call the next token and complete the serving token.

## Features

- Digital token generation
- Separate queues for multiple campus services
- Priority assistance using `PriorityQueue`
- Queue position and estimated waiting time
- Call-next and serving status
- Token cancellation and completion
- CSV-based local persistence
- Input validation and error handling
- Object-oriented modular structure

## Technologies

- Java
- Java Swing
- Java Collections Framework
- `PriorityQueue`
- `EnumMap`
- File I/O / CSV
- Exception handling
- Object-oriented programming

## Requirements

Java 17 or newer.

## Run

From the project root:

```bash
javac -d out $(find src -name '*.java')
java -cp out queueless.app.QueueLessApp
```

On Windows PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out queueless.app.QueueLessApp
```

## Demo flow

1. Enter a student ID and name.
2. Select a campus service.
3. Enable priority assistance when required.
4. Click **Join Queue**.
5. Observe the generated token, queue position and ETA.
6. Use **Call Next** to move the next token into `SERVING`.
7. Select a token in the table and use **Complete Selected**.
8. A waiting token can be cancelled with **Cancel My Token**.
9. Queue records are persisted in `data/queue.csv`.

## Project structure

```text
QueueLess/
├── data/
├── docs/
│   ├── diagrams/
│   └── screenshots/
├── src/
│   └── queueless/
│       ├── app/
│       ├── model/
│       ├── service/
│       ├── store/
│       └── util/
├── README.md
├── statement.md
└── .gitignore
```

## Testing

The project should be checked for:

- Valid and invalid student input
- Normal queue registration
- Priority queue registration
- Queue position and ETA
- Call-next operation
- Serving status
- Token cancellation
- Token completion
- Empty queue handling
- CSV persistence

## Screenshots

Working screenshots are available in `docs/screenshots/`.

## Future enhancements

- Client/server networking
- QR token verification
- Notifications
- Admin authentication
- Daily queue analytics
- Database-backed persistence
