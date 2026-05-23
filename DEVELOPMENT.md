# Development Notes

## Target Stack

- Paper API: `1.21.11-R0.1-SNAPSHOT`
- Java: `21`
- Gradle Wrapper: `9.5.1`
- FAWE compile/runtime line: `2.15.1`
- FAWE BOM used at compile time: `com.intellectualsites.bom:bom-newest:1.56`

## Local Tooling

This repository uses local, portable tooling rather than system-wide installs.

- Portable JDK 21:
  - `.tools/jdks/jdk-21.0.11+10/`
- Temporary portable Gradle distribution used to generate the wrapper:
  - `.tools/gradle/gradle-9.5.1/`

The committed wrapper should be preferred for normal work:

- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

## Build Commands

Windows PowerShell example:

```powershell
$env:JAVA_HOME = (Resolve-Path '.tools\jdks\jdk-21.0.11+10').Path
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
.\gradlew.bat clean build
```

## Notes

- `.tools/downloads/`, `.tools/gradle/`, and `.tools/jdks/` are ignored by git.
- The wrapper is committed so future contributors do not need a machine-wide Gradle installation.
- HalalBuilds still expects FAWE to be installed on the target Paper server at runtime.

