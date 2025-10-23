# My Tasks - Project Documentation

## Project Summary

### Application Name and Primary Function
**My Tasks** is a comprehensive productivity application for Android that helps users organize their daily activities. It provides a unified platform for managing tasks, notes, and events, with a focus on simplicity and ease of use.

### Target User/Audience
The application is designed for individuals seeking an all-in-one solution to manage personal and professional responsibilities, reminders, and ideas.

### Core Business Value/Problem Solved
My Tasks addresses the need for a centralized, secure, and easily accessible tool for personal organization. It helps users keep track of their to-do lists, important notes, and upcoming events, ensuring that nothing is forgotten. The inclusion of alarms, home-screen widgets, and optional biometric security enhances its value as a reliable productivity companion.

## Technical Stack & Environment

*   **Minimum SDK Version**: 34
*   **Target SDK Version**: 35
*   **Kotlin Version**: 2.0.21
*   **Gradle Plugin Version**: 8.10.1
*   **Java Version**: 19
*   **Build Configuration Variants**:
    *   `debug`: Includes a `.debug` application ID suffix for easy identification.
    *   `release`: Configured for production, with minification disabled. ProGuard rules are applied from `proguard-android-optimize.txt` and `proguard-rules.pro`.

## Dependency Manifest (Rebuild Checklist)

### Core & UI
*   `androidx.core:core-ktx:1.16.0`
*   `androidx.lifecycle:lifecycle-runtime-ktx:2.9.1`
*   `androidx.activity:activity-compose:1.10.1`
*   `androidx.compose:compose-bom:2025.06.01`
*   `androidx.compose.ui:ui`
*   `androidx.compose.ui:ui-graphics`
*   `androidx.compose.ui:ui-tooling-preview`
*   `androidx.compose.material3:material3`
*   `androidx.navigation:navigation-compose:2.9.1`
*   `io.github.jeziellago:compose-markdown:0.5.7`

### Dependency Injection (Hilt)
*   `com.google.dagger:hilt-android:2.55`
*   `com.google.dagger:hilt-compiler:2.55` (ksp)
*   `androidx.hilt:hilt-navigation-compose:1.2.0`

### Database (Room)
*   `androidx.room:room-runtime:2.7.2`
*   `androidx.room:room-ktx:2.7.2`
*   `androidx.room:room-compiler:2.7.2` (ksp)

### Biometric Authentication
*   `androidx.biometric:biometric:1.2.0-alpha05`
*   `androidx.appcompat:appcompat:1.7.1`

### Widget (Glance)
*   `androidx.glance:glance-appwidget:1.1.1`
*   `androidx.glance:glance-material3:1.1.1`

### JSON Serialization (Gson)
*   `com.google.code.gson:gson:2.11.0`

### Testing
*   `junit:junit:4.13.2` (test)
*   `androidx.test.ext:junit:1.2.1` (androidTest)
*   `androidx.test.espresso:espresso-core:3.6.1` (androidTest)
*   `androidx.compose.ui:ui-test-junit4` (androidTest)
*   `io.mockk:mockk:1.13.5` (test)
*   `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3` (test)
alisa.sinan.mytasks.test/com.sinan.turbine:turbine:0.13.0` (test)

### Plugins
*   `com.android.application`: 8.10.1
*   `org.jetbrains.kotlin.android`: 2.0.21
*   `org.jetbrains.kotlin.plugin.compose`: 2.0.21
*   `com.google.dagger.hilt.android`: 2.55
*   `com.google.devtools.ksp`: 2.0.21-1.0.27
*   `androidx.room`: 2.7.2

## Application Architecture & Structure

### Primary Architectural Pattern
The application follows the **MVVM (Model-View-ViewModel)** pattern, integrated with the principles of **Clean Architecture**. This layered approach promotes a separation of concerns, which enhances maintainability, scalability, and testability.

*   **UI Layer (View)**: Composable functions that observe `StateFlow` from ViewModels to render the UI. User interactions are captured and forwarded to the ViewModel.
*   **ViewModel**: Manages and exposes UI state using `StateFlow`. It communicates with the domain layer (use cases) to execute business logic and update the state.
*   **Domain Layer**: Contains business logic encapsulated in use cases. This layer is independent of the UI and data layers and relies on repository interfaces.
*   **Data Layer**: Implements the repository interfaces defined in the domain layer. It is responsible for managing data from local sources (Room database).

### Module Breakdown
While the project is organized within a single `:app` module, it is logically structured into packages that reflect the Clean Architecture principles:

*   `ui`: Contains all UI-related components, including Composable screens, ViewModels, and navigation logic.
*   `domain`: Includes use cases and repository interfaces, defining the core business rules.
*   `data`: Consists of repository implementations, Room database definitions (DAOs, entities), and data models.
*   `di`: Contains Hilt modules for dependency injection, which wire together the different layers of the application.

### Data/Control Flow Diagram
1.  **UI (View)**: A user action (e.g., clicking a button to add a task) triggers a function call in the corresponding **ViewModel**.
2.  **ViewModel**: The ViewModel invokes a specific **Use Case** from the domain layer (e.g., `AddTaskUseCase`).
3.  **Use Case**: The use case executes its business logic and communicates with one or more **Repository Interfaces**.
4.  **Repository (Implementation)**: The repository implementation in the data layer interacts with the **Room Database** to perform the requested operation (e.g., inserting a new task).
5.  **Data Flow Back**: The data flows back through the layers: `Room Database` → `Repository` → `Use Case` → `ViewModel`. The ViewModel then updates its `StateFlow`, which causes the UI to be recomposed with the new state.

## Data Layer Details

### API Specification
The application does not consume any remote APIs. All data is stored locally on the device.

### Persistence Schema
The application uses Room for local persistence. The database schema is defined as follows:

*   **`notes` Table (`Note.kt`)**: Stores user-created notes.
    *   `id`: `Long` (Primary Key, Auto-generated)
    *   `folderId`: `Long` (Foreign key to the `folders` table)
    *   `title`: `String`
    *   `content`: `String`
    *   `timestamp`: `LocalDateTime`

*   **`tasks` Table (`Task.kt`)**: Stores to-do items.
    *   `id`: `Long` (Primary Key, Auto-generated)
    *   `folderId`: `Long` (Foreign key to the `folders` table)
    *   `title`: `String`
    *   `description`: `String`
    *   `isCompleted`: `Boolean`
    *   `timestamp`: `LocalDateTime`
    *   `due`: `LocalDateTime?`

*   **`events` Table (`Event.kt`)**: Stores calendar events.
    *   `id`: `Long` (Primary Key, Auto-generated)
    *   `folderId`: `Long` (Foreign key to the `folders` table)
    *   `title`: `String`
    *   `description`: `String`
    *   `timestamp`: `LocalDateTime`
    *   `start`: `LocalDateTime?`
    *   `end`: `LocalDateTime?`

*   **`folders` Table (`Folder.kt`)**: Organizes notes, tasks, and events.
    *   `folderId`: `Long` (Primary Key, Auto-generated)
    *   `name`: `String`
    *   `parentFolderId`: `Long?` (Self-referencing for nested folders)
    *   `isLocked`: `Boolean`

*   **`alarm` Table (`Alarm.kt`)**: Stores alarms associated with tasks or events.
    *   `alarmId`: `Long` (Primary Key, Auto-generated)
    *   `isTask`: `Boolean` (True if the alarm is for a task, false for an event)
    *   `taskId`: `Long` (Foreign key to the `tasks` or `events` table)
    *   `time`: `Long`

### Domain Models
The domain models are represented by the same data classes used for the Room entities (`Note`, `Task`, `Event`, `Folder`, `Alarm`). These models are used consistently across all layers of the application, from the database to the UI.

## Presentation & UI Layer

### Navigation Graph
The application uses Jetpack Compose Navigation to manage screen transitions. The navigation graph is defined in `NavGraph.kt` and includes the following primary destinations:

*   **Home Screen (`HomeScreen`)**: The main entry point, providing access to notes, tasks, events, and folders.
*   **Note List (`NoteListScreen`)**: Displays a list of all notes.
*   **Add/Edit Note (`AddEditNoteScreen`)**: A screen for creating or modifying a note. It is accessed with a `noteId` and an optional `folderId`.
*   **Task List (`TaskListScreen`)**: Displays a list of all tasks.
*   **Add/Edit Task (`AddEditTaskScreen`)**: A screen for creating or modifying a task. It is accessed with a `taskId` and an optional `folderId`.
*   **Event List (`EventListScreen`)**: Displays a calendar view of events.
*   **Add/Edit Event (`AddEditEventScreen`)**: A screen for creating or modifying an event. It is accessed with an `eventId`, an optional `folderId`, and an optional `date`.
*   **Folder List (`FolderListScreen`)**: Displays a list of folders and their contents. It is accessed with a `folderId` to support nested folders.
*   **Backup/Restore (`BackupScreen`)**: A screen for backing up and restoring application data.

The application also supports deep linking for adding new notes, tasks, and events, as well as navigating to the home screen.

### State Management
UI state is managed using **ViewModels**, which follow the MVVM architectural pattern. Each screen has a corresponding ViewModel that holds the UI state as a `StateFlow` and exposes it to the Composables. User events are handled by the ViewModel, which communicates with the domain layer to perform business logic and updates the state accordingly.

### Key Custom Components
*   **Markdown Editor/Viewer**: The application includes a custom markdown editor for creating and editing notes, which are then rendered as styled text.
*   **Biometric Authentication Prompt**: A reusable function (`showBiometricsAuthentication`) that wraps the BiometricPrompt API to provide a consistent way of authenticating users before granting access to locked content.
*   **Glance Widget**: A home screen widget (`MyGlanceWidget`) that provides quick actions to create new notes, tasks, and events.
*   **Alarm Scheduler**: A custom scheduler (`AlarmScheduler`) that uses `AlarmManager` to set and cancel alarms for tasks and events.

## Build & Deployment

### Build Instructions
To build the application, execute the following Gradle task from the project's root directory:

```bash
./gradlew assembleRelease
```

This will generate a release-signed APK in the `app/build/outputs/apk/release` directory.

### Signing Information
To sign the release build, you will need to create a `keystore.properties` file in the root directory with the following template:

```properties
storePassword=<YOUR_STORE_PASSWORD>
keyAlias=<YOUR_KEY_ALIAS>
keyPassword=<YOUR_KEY_PASSWORD>
storeFile=<PATH_TO_YOUR_KEYSTORE_FILE>
```

The `app/build.gradle.kts` file must be configured to read these properties and apply them to the `release` build type.

### ProGuard/R8 Rules
The project is configured to use ProGuard for code shrinking and obfuscation in the `release` build type. The default Android optimization rules (`proguard-android-optimize.txt`) are applied, along with a project-specific `proguard-rules.pro` file. No custom rules have been added, so the default configuration is used.
