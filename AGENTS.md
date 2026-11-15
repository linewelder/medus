# Medus

Android app (Kotlin, Jetpack Compose, Hilt, Room) for planning daily tasks and errands.

## Rules

- Let the code explain itself: use descriptive names, and extract hard-to-read conditions into a named `val` before the `if`.
- Avoid comments inside function bodies.
- This excludes comments that cannot be integrated into code: e.g. describing why a certain warning or deprecation is suppressed. Things like these are not relevant to the function's behaviour and should not be included in the KDoc.
- Don't extract a function (or a `remember*` helper) that is used only once just to give it a name or KDoc. Keep the code inline and give the resulting variable a clear name instead (e.g. `exerciseOrder`, not `order`).
- Write KDoc for classes, functions, properties and fields, unless the name already says everything (typical for one-liners).
- Write KDoc for functions in imperative.
- Edit plain text and numbers in-line with text fields. Use a dialog (opened from a `FakeOutlinedTextField`) only for values that aren't plain text or numbers (e.g. `SetTarget`, `SetResult`); those dialogs will get customised inputs.
- Commit messages follow Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `chore:`, `docs:`, `build:`), imperative mood, capitalized subject.
- Use extended commit messages for reasoning, not the list of changes. Only add them if the reason is not obvious.
- Do not sign commits as Co-Authored-By an AI agent.
- When a change is small and related to a previous one, prefer to amend commit instead of creating a new one.

## Workflow

- Ask clarifying questions before starting any complex or ambiguous task.
- Make minimal changes - do not refactor unrelated code.
- Do not do anything not requested, instead ask a follow up question, but only if something crucial was omitted in the request.
- When unsure between two approaches, explain both and let me choose.
