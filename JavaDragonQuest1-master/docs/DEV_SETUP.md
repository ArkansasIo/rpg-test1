# Developer Setup

## 1. Prerequisites

- Windows PowerShell
- JDK 25 installed
- Git

Project uses bundled Ant:
- `apache-ant-1.10.14\bin\ant.bat`

## 2. Clone and Open

```powershell
git clone <repo-url>
cd JavaDragonQuest1-master
```

## 3. Build Commands

Compile:
```powershell
.\apache-ant-1.10.14\bin\ant.bat compile
```

Run:
```powershell
.\apache-ant-1.10.14\bin\ant.bat run
```

Create jar:
```powershell
.\apache-ant-1.10.14\bin\ant.bat jar
```

Clean:
```powershell
.\apache-ant-1.10.14\bin\ant.bat clean
```

## 4. Key Paths

- Source: `src/`
- Assets: `assets/res/`
- Build output: `build/`, `dist/`
- Documentation: `docs/`

## 5. Contribution Guidelines

- Keep runtime logic in `dq1.core`.
- Keep editor UI logic in `dq1.editor`.
- Update docs when adding systems or public APIs.
- Prefer additive changes over breaking editor compatibility.
