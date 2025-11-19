# Copilot instructions for My Tasks (Android/Compose + Hilt + Room)

Purpose: give AI agents the minimum, specific context to be productive in this repo.

## Architecture in one screen
- Pattern: Clean MVVM on a single `:app` module.
  - UI: Jetpack Compose screens + Navigation. See `ui/features/**` and `ui/navigation/{Routes.kt,NavGraph.kt}`.
  - ViewModel: Hilt-injected, exposes StateFlow. Example: `ui/features/notes/NoteViewModel.kt`.
  - Domain: Use cases grouped as containers (e.g., `NoteUseCases`, `TaskUseCases`) in `domain/usecase/**`, provided via Hilt in `di/UseCaseModule.kt`.
  - Data: Room DAOs and entities in `data/local/{dao,entities,database}`; repositories in `data/repository/**` implement `domain/repository/**` and are bound in `di/RepositoryModule.kt`.
  - Core: Cross-cutting logic like selection lives in `core/SelectionStore.kt`.

## Data + DI conventions
- Room:
  - Entities/DAOs under `data/local/entities/*` and `data/local/dao/*` (e.g., `Note.kt`, `NoteDao.kt`). DAOs return `Flow` for streams; use archive flags instead of hard deletes when needed.
  - Database: `data/local/database/AppDatabase.kt`; migrations wired in `di/AppModule.kt` via `.addMigrations(MIGRATION_3_4 ... MIGRATION_7_8)`.
  - Room Gradle plugin writes schemas to `app/schemas/**` (see `app/build.gradle.kts` room { schemaDirectory(...) }). Update migrations when modifying schema.
- Dependency Injection (Hilt):
  - Bind repo interfaces in `di/RepositoryModule.kt` with `@Binds`.
  - Provide aggregated use-case bundles in `di/UseCaseModule.kt` (e.g., `NoteUseCases(getNotes, addNote, ...)`). Inject the bundle into ViewModels instead of individual use cases.
  - App is Hilt-enabled via `@HiltAndroidApp` on `MyTasksApp` and `@AndroidEntryPoint` on `MainActivity`.

## Navigation + deep links
- Routes are centralized in `ui/navigation/Routes.kt` with args and deep links (e.g., `Routes.Note.Add` = `note_screen/{noteId}/{folderId}`; deep link `mytasks://add_note`).
- Graph is composed in `ui/navigation/NavGraph.kt`; use `navArgument` types and `NavHost`.
- Deep-link handling is enabled in `AndroidManifest.xml` (`<data android:scheme="mytasks"/>`).

## Selection system (project-specific)
- Global, typed multi-selection managed by singleton `core/SelectionStore` with `StateFlow`s for selected items and a sealed `SelectionAction`.
- Actions supported: `Copy`, `Cut`, `Paste(folderId)`, `Pin`, `Archive/Unarchive`, and a two-step `Delete` → `DeleteConfirm(true)` workflow.
- Data operations are delegated to use cases: see `domain/usecase/selection/{PasteSelectionUseCase,DeleteSelectionUseCase}.kt`.
- UI/ViewModels interact only via `SelectionStore.onAction(...)` and `toggle{Note,Task,Folder}(...)`.

## Background work, alarms, widgets
- Background auto-archive runs daily via WorkManager: `worker/AutoArchiveWorker.kt`, configured in `MyTasksApp.kt` using `HiltWorkerFactory`.
- Alarms and boot receivers are registered in the manifest; `AlarmScheduler` is provided in `di/AppModule.kt`.
- Home screen widget built with Glance in `ui/features/widgets/Widget.kt`; buttons fire app deep links to routes.

## Build, run, test
- Tooling (see `gradle/libs.versions.toml`): Kotlin 2.0.21, AGP 8.10.1, Compose BOM 2025.06.01, Compose compiler ext 1.5.15, Hilt 2.55, Room 2.7.2, WorkManager 2.11.0.
- Android config (see `app/build.gradle.kts`): minSdk 34, targetSdk 35, compileSdk 36. JVM target 19. Hilt aggregating task disabled.
- Build (Windows PowerShell):
  - Debug: `./gradlew.bat assembleDebug`
  - Release: `./gradlew.bat assembleRelease` (outputs in `app/build/outputs/apk/release/`)
- Tests: `./gradlew.bat test` (unit) and `./gradlew.bat connectedAndroidTest` (instrumented). Test libs: JUnit, MockK, Turbine, Coroutines Test.

## How to add a new feature (example-driven)
- New entity + CRUD:
  1) Define entity in `data/local/entities/`. 2) Create DAO in `data/local/dao/` returning `Flow` for queries.
  3) Add repository implementation in `data/repository/` + interface in `domain/repository/`; bind in `di/RepositoryModule.kt`.
  4) Create use cases in `domain/usecase/<feature>/` and extend the `<Feature>UseCases` bundle + provider in `di/UseCaseModule.kt`.
- New screen:
  1) Add screen Composable and ViewModel (`@HiltViewModel`) under `ui/features/<feature>/`.
  2) Add a route in `ui/navigation/Routes.kt` and register in `NavGraph.kt`.
  3) Consume use-case bundle and, if needed, `SelectionStore` via constructor injection.

## Gotchas and tips
- Favor archive/unarchive operations over hard deletes (see DAOs like `NoteDao.archiveNotes(...)`).
- Selection actions that move/copy items must go through `SelectionStore` + selection use cases to keep behavior consistent.
- If you change Room schema, add a migration and verify JSON schemas under `app/schemas/**` are updated.
- Deep links must match `Routes.*.Add.deepLink` patterns and the manifest scheme.
