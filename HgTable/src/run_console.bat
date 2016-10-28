@echo off
goto :START
:START
java -Xmx512M -cp ".;lib/*" HgTable --script_mode false --javahome "%JAVA_HOME%"
if ERRORLEVEL 1 (
    GOTO :START
) else (
    GOTO :END
)
:END
pause