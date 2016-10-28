@echo off
goto :START
:START
java -Xmx512M -cp ".;lib/*" HgTable --script_mode true --javahome "%JAVA_HOME%"
if ERRORLEVEL 1 (
    goto :START
) else (
    GOTO :END
)
:END
pause