# Medus

Android app (Kotlin, Jetpack Compose, Hilt, Room) for planning daily tasks and errands.

## Architecture

### Gradle modules

`:app` contains Android entry points, Compose UI, navigation, theming, and ViewModels. Keep each screen and its ViewModel together under `io.github.linewelder.medus.ui.screens.<feature>`. Keep navigation in `ui/Navigation.kt` and theme definitions in `ui.theme`.

`:core:model` defines shared Kotlin models such as `Task`, `Area`, and the typed identifier `Id<T>`. Keep this module independent of Android UI and Room.

`:core:database` handles Room persistence. Keep entities and DAOs in `core.database.model`, type converters in `core.database.converters`, and the database, Hilt provider, and migrations in `core.database`. Export Room schema snapshots to this module's `schemas/` directory and add a migration when changing the persisted schema.

`:core:data` exposes repository APIs to the app, coordinates database operations, and maps between Room entities and shared models.

`:app` depends on `:core:data`. The data module exposes `:core:model` to callers and uses `:core:database` internally. The database module also uses shared model types, including typed IDs.

### Data access and mapping

- Put app-facing retrieval functions in the repository for the entity being retrieved, under `io.github.linewelder.medus.core.data.repositories` in `:core:data`. For example, put task lookups in `TaskRepository` and area lookups in `AreaRepository`. Keep SQL queries in the corresponding Room DAO in `:core:database`.
- Inject repositories into ViewModels with Hilt. Have repositories expose shared models, typed IDs, or operation results to callers. Use `Flow` for observed queries and suspend functions for one-shot lookups and writes.
- Put mappers between Room entities and shared models, in either direction, in `io.github.linewelder.medus.core.data.mappings` in `:core:data`. Follow the existing entity-to-model convention, such as `TaskEntity.asExternalModel()` in `mappings/Task.kt`, and apply the mapping in the repository before returning data to the app.
- Keep writes that must succeed together in a repository transaction. For example, `TaskRepository` saves a new area and its task in the same transaction.

### Compose components

- Compose screens from focused composables. Extract distinct UI sections and behaviors into named composables even when there is only one call site.
- Keep a composable that belongs exclusively to one parent as a private, top-level composable in the parent's Kotlin file. Put reusable components in their own files under `io.github.linewelder.medus.ui.components` in `:app`. Use a subpackage for a cohesive group of components, as with `ui.components.calendar`.
- Give reusable components explicit inputs and callbacks. Keep reusable behavior such as drag-to-reorder gestures, animations, and autoscroll inside the component, and wrap third-party UI behavior before using it in a screen. Let the screen supply content and handle feature-specific actions.

## Rules

- Let the code explain itself: use descriptive names, and extract hard-to-read conditions into a named `val` before the `if`.
- Avoid comments inside function bodies.
- This excludes comments that cannot be integrated into code: e.g. describing why a certain warning or deprecation is suppressed. Things like these are not relevant to the function's behaviour and should not be included in the KDoc.
- Keep single-use logic and `remember*` helpers inline when extraction would only add a name or KDoc. Use descriptive local variables such as `exerciseOrder`. Follow the Compose component rules above for distinct UI sections and behaviors.
- Write KDoc for classes, functions, properties and fields, unless the name already says everything (typical for one-liners).
- Write KDoc for functions in imperative, except for @Composable UI components.
- Edit plain text and numbers in-line with text fields. Use a dialog (opened from a `FakeOutlinedTextField`) only for values that aren't plain text or numbers (e.g. `SetTarget`, `SetResult`); those dialogs will get customised inputs.
- Commit messages follow Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `chore:`, `docs:`, `build:`), imperative mood, capitalized subject.
- Use extended commit messages for reasoning, not the list of changes. Only add them if the reason is not obvious.
- Do not sign commits as Co-Authored-By an AI agent.
- When a change is small and related to a previous one, prefer to amend commit instead of creating a new one.
- Do not repeat entity names in DAOs and Repositories. Do not call a method `findAreaById` in an `AreaRepository`, use `findById` instead.

## Workflow

- Ask clarifying questions before starting any complex or ambiguous task.
- Make minimal changes - do not refactor unrelated code.
- Do not do anything not requested, instead ask a follow up question, but only if something crucial was omitted in the request.
- When unsure between two approaches, explain both and let me choose.
