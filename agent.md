# MyTasks - Android Task Management Application Documentation

## Project Summary

### Application Name and Primary Function
MyTasks is a comprehensive task and note management Android application that provides users with the ability to manage tasks, notes, events, and reminders with folder organization and biometric security features.

### Target User/Audience
- Individual users seeking a unified solution for task, note, and event management
- Users who need secure storage of sensitive notes/tasks with biometric protection
- Users requiring calendar-based event tracking and task scheduling
- Users who want quick access to their tasks/notes through home screen widgets

### Core Business Value/Problem Solved
- Unified management of tasks, notes, and events in a single application
- Secure information storage with biometric authentication
- Organized content management through hierarchical folder structure
- Flexible reminder system with customizable notifications
- Quick access through home screen widgets

## Technical Stack & Environment

### Android Configuration
- **Minimum SDK**: 34 (Android 14)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 36

### Language & Build Tools
- **Kotlin Version**: 2.0.21
- **Java Compatibility**: Version 19
- **Gradle Plugin Version**: 8.10.1
- **Kotlin Compiler Extension Version**: 1.5.15

### Build Configuration
```groovy
buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
    debug {
        applicationIdSuffix = ".debug"
    }
}
```

## Dependency Manifest

### Core Android & Jetpack
- AndroidX Core KTX: 1.16.0
- Lifecycle Runtime KTX: 2.9.1
- Activity Compose: 1.10.1
- Navigation Compose: 2.9.1

### UI Framework
- Jetpack Compose BOM: 2025.06.01
  - compose-ui
  - compose-ui-graphics
  - compose-ui-tooling
  - compose-material3

### Dependency Injection
- Hilt Android: 2.55
- Hilt Navigation Compose: 1.2.0
- Hilt Compiler KSP

### Database & Persistence
- Room Runtime: 2.7.2
- Room KTX
- Room Compiler

### Security
- Biometric: 1.2.0-alpha05
- AppCompat: 1.7.1

### Widget Support
- Glance AppWidget: 1.1.1
- Glance Material3: 1.1.1

### Additional Libraries
- Gson: 2.11.0
- Compose Markdown: 0.5.7

### Testing Dependencies
- JUnit: 4.13.2
- Espresso Core: 3.6.1
- MockK: 1.13.5
- Turbine: 0.13.0
- Coroutines Test: 1.7.3

## Application Architecture & Structure

### Primary Architectural Pattern
The application follows MVVM (Model-View-ViewModel) with Clean Architecture principles:
- **Presentation Layer**: Jetpack Compose UI + ViewModels
- **Domain Layer**: Use Cases & Repository Interfaces
- **Data Layer**: Room Database + Repository Implementations

### Data Models

#### Database Entities
1. **Note Entity**
```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val folderId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)
```

2. **Task Entity**
```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val folderId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val due: LocalDateTime? = null
)
```

3. **Event Entity**
```kotlin
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val folderId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null
)
```

4. **Folder Entity**
```kotlin
@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true) val folderId: Long = 0L,
    val name: String,
    val parentFolderId: Long? = 0L,
    @ColumnInfo(defaultValue = "0")
    val isLocked: Boolean = false
)
```

5. **Alarm Entity**
```kotlin
@Entity(tableName = "alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val alarmId: Long = 0L,
    val isTask: Boolean,
    val taskId: Long,
    val time: Long
)
```

### Key Components

#### Database
- Room Database (AppDatabase) version 4
- Uses TypeConverters for LocalDateTime serialization
- Implements auto-migrations
- Includes DAOs for all entities:
  - NoteDao
  - TaskDao
  - FolderDao
  - EventDao
  - AlarmDao

#### Repositories
All repositories follow interface-based design with concrete implementations:
- NoteRepository
- TaskRepository
- EventRepository
- FolderRepository
- AlarmRepository

#### ViewModels
- NoteViewModel
- TaskViewModel
- EventViewModel
- AlarmViewModel
- BackupViewModel

#### Core Features
1. **Folder Management**
   - Hierarchical folder structure
   - Folder locking with biometric authentication
   - Copy and delete operations with content

2. **Task Management**
   - Task creation and editing
   - Due date setting
   - Completion status tracking
   - Multiple reminders per task

3. **Note Management**
   - Text note creation and editing
   - Markdown support
   - Timestamp tracking

4. **Event Management**
   - Event scheduling with start/end times
   - Calendar view
   - Event reminders

5. **Reminder System**
   - Alarm scheduling
   - Notification management
   - Snooze functionality

6. **Widget Support**
   - Quick access toolbar widget
   - Task/Note creation shortcuts
   - Configurable layout based on size

7. **Backup System**
   - JSON-based export/import
   - All entities included in backup
   - Preserves relationships between entities

## Build & Deployment

### Prerequisites
1. Android Studio Hedgehog or newer
2. JDK 19
3. Android SDK with API 34-36

### Build Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build the project using Gradle wrapper:
```bash
./gradlew assembleDebug   # For debug build
./gradlew assembleRelease # For release build
```

### ProGuard Configuration
- MinifyEnabled is currently set to false
- Default Android ProGuard rules are included
- Additional ProGuard rules can be added in proguard-rules.pro

## Testing Strategy

### Unit Tests
- JUnit 4 for unit testing
- MockK for mocking
- Turbine for Flow testing
- Coroutines test utilities for suspend functions

### UI Tests
- Compose UI testing with AndroidJUnit4
- Espresso for Android UI testing
- Screenshot testing (planned)

### Integration Tests
- Room database testing
- Repository integration tests
- ViewModel integration tests

### Test Coverage
Currently includes:
- Basic instrumented tests
- Room database operations
- Repository operations

## Additional Notes

### Security Considerations
- Biometric authentication for locked folders
- No sensitive data stored in plain text
- Secure backup/restore functionality

### Performance Considerations
- Lazy loading of data
- Efficient database queries with Room
- Coroutines for async operations
- Compose recomposition optimization

### Future Enhancements
- Enhanced widget customization
- Cloud backup integration
- Dark/Light theme support
- Multi-language support