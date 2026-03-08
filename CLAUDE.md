# Claude Code Instructions

## Spec-Driven Development

This project uses spec-driven development. All features are defined in `specs/` before implementation begins.

### Workflow

1. **Write the spec first.** Before implementing any feature, create or update a spec file in `specs/`. Use `specs/_template.spec.md` as the starting point.

2. **Implement against the spec.** When implementing, treat the spec as the source of truth. Each acceptance criterion should be satisfied.

3. **Mark criteria done.** As acceptance criteria are met, mark them with `[x]` in the spec file.

4. **Keep specs up to date.** If requirements change during implementation, update the spec before changing the code.

### Rules

- Do NOT implement features that lack a corresponding spec.
- Do NOT skip acceptance criteria — if a criterion cannot be met, flag it explicitly.
- When a user asks to build a feature, ask if a spec exists or offer to draft one first.
- Specs are the contract. Code serves the spec, not the other way around.

### File Naming

Spec files live in `specs/` and follow this convention:

```
specs/<feature-name>.spec.md
```

Examples:
- `specs/ranking-algorithm.spec.md`
- `specs/data-ingestion.spec.md`
- `specs/api-endpoints.spec.md`

### Project Stack

- Language: Kotlin Multiplatform (KMP)
- UI: Compose Multiplatform (Android, iOS, Web, Desktop)
- Shared business logic lives in the `common` module; platform-specific code is isolated
- Stack decisions should be recorded in `specs/decisions/` as ADRs (Architecture Decision Records)
