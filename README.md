# RideManagement - Java Swing (Dark Theme)

## What’s included
- `model.Ride` - Ride model (serializable)
- `storage.RideStorage` - file persistence (`rides.dat`)
- `ui.*` - Swing UI classes: `LoginPage`, `HomePage`, `AddRidePage`, `ViewRidesPage`
- `app.Main` - application entry point
- `.vscode/launch.json` - a VS Code launch configuration
- `compile_and_run.sh` and `compile_and_run.bat` - simple scripts to compile & run

## How to open in VS Code
1. Install **Java Extension Pack** in VS Code (by Microsoft).
2. Open this folder (`RideManagement`) in VS Code.
3. Open `src/app/Main.java` and press the Run ▶️ icon, or use the Run panel and choose `Launch Main`.

## How to compile & run from terminal
### Linux / macOS
```bash
cd RideManagement
./compile_and_run.sh
```
### Windows (PowerShell or CMD)
```powershell
cd RideManagement
compile_and_run.bat
```

## Notes
- Rides are persisted to `rides.dat` in the project folder.
- The UI uses a simple dark theme with contrast accents.
