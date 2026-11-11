# AGENTS.md

## Build & verify
- `./gradlew build` — compiles, applies Spotless formatting
  (palantirJavaFormat), runs tests, and produces the distributable jar via Shadow.
- Never hand-format Java; `build` depends on `spotlessApply`. Run
  `./gradlew spotlessApply` before finishing if you edited Java without building.
- Add or update tests when changing behavior.
- Report any build or test failures and anything that could not be verified.

## Code style
- Every **public** method gets a Javadoc comment. Private methods do not need one.
- Never use fully qualified names (e.g. `java.util.List`); use imports instead.
  Only use FQN to disambiguate conflicting simple names.
- Do not extract small logic blocks (~3–5 lines) into their own function; keep
  them inline for readability. Only factor out genuinely reusable or
  substantially large pieces.
- Keep code comments minimal: state what non-obvious code does in one short line.
  Never write verbose explanatory comments, vendor/host-specific anecdotes, or
  multi-line rationale essays — that context belongs in commit messages or chat,
  not the codebase.
- Follow the surrounding code’s structure, naming, and patterns.
- Keep changes focused. Do not add unrelated refactors, dependencies, or features.
- Reuse existing helpers rather than introducing duplicate implementations.

## Design conventions
- Follow `DESIGN.md` for user-facing text and interface styling.
- Keep the palette, formatting rules, and reusable visual tokens documented
  there as the single source of truth.
- Reuse existing colors and styles; do not introduce new hex colors without
  updating `DESIGN.md`.
- Keep headings, body text, secondary text, dynamic values, and interaction
  hints visually consistent.
- Use consistent success, error, and informational indicators.
- Do not rely on color alone to convey status.
- Include units with durations and other measured values.
- Keep descriptions concise and limited to one idea per line where practical.
- Provide clear navigation labels and meaningful empty states.
- Keep project branding and feature-specific presentation rules in
  `DESIGN.md`, not in these general development instructions.

## CI
- `.github/workflows/build.yml` — verifies on push and pull requests to `master`
  using `./gradlew build`.
- Upload `build/libs/*.jar` as a 14-day artifact only on manual workflow runs
  (`workflow_dispatch`).
- No tag is needed for CI-only changes.
- `.github/workflows/release.yml` — runs on tag pushes matching `v*.*.*`, runs
  `./gradlew shadowJar`, and publishes a GitHub Release containing the jar.
- Mark a release as a prerelease if its tag contains `-`.
- The release workflow does not run the full test/formatting build. Keep the
  full Build green before tagging.
- Both workflows run `chmod +x gradlew` first; `gradlew` must keep its `100755`
  Git mode.
- No Maven publishing — GitHub Releases are the distribution channel.

## Release
- The project version lives only in `build.gradle.kts`
  (`version = "x.y.z"`).
- Derive the jar filename and any resource version metadata from that version;
  do not maintain duplicate hardcoded versions.
- When stamping resources through `processResources`, keep
  `inputs.property("version", ...)` so version changes invalidate cached output.
- Release sequence:
  bump `version` in `build.gradle.kts` → commit → push `master` →
  wait for Build to pass → `git tag -a v<x.y.z> -m "Release <x.y.z>"` →
  `git push origin v<x.y.z>`.
- This sequence documents the maintainer’s release process; it does not
  authorize an agent to push or publish.
- The tag must equal the `build.gradle.kts` version; the release workflow must
  fail otherwise.
- Never move or re-push a tag once its release succeeded — cut a new patch
  version instead.
- Deleting a remote tag (`git push origin --delete v<x.y.z>`) is only for a
  release that never succeeded and requires explicit authorization.

## Git workflow
- **Maintain Atomic Commits:** Limit each commit to a single logical,
  reversible change. Never bundle unrelated refactors, formatting, and
  feature work.
- When a task is complete, stage changed files and create a commit.
- Stage only changes belonging to the task; do not include unrelated user work.
- Use Conventional Commits: `<type>(<scope>): <summary>`.
- Do not run `git push`.
- State the exact intent in the subject line. If you cannot describe the change
  concisely without vague phrasing (e.g., `fix: update code`,
  `refactor: misc changes`), the scope is too broad. Split it into smaller commits.
- Do not revert or discard changes you did not make.
- Do not amend commits or rewrite history without explicit authorization.
