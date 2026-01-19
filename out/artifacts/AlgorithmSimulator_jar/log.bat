@echo off
echo Starting Algorithm Simulator with logging...
echo ========================================
echo.

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

REM Get current date and time for log filename
set datetime=%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set datetime=%datetime: =0%

REM Run your EXE and capture ALL output to log file
echo Running application... Output will be saved to logs/app_log_%datetime%.txt
echo.

AlgorithmSimulator.exe > logs/app_log_%datetime%.txt 2>&1

echo.
echo Application finished. Check the log file for details.
echo Log location: logs/app_log_%datetime%.txt
echo.
pause
