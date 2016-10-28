@echo off
goto :START
:START
java -Xmx512M -cp ".;lib/*" HgTable --GUIMode true --script_mode false --javahome "%JAVA_HOME%"
if ERRORLEVEL 1 (
    goto :START
) else (
    GOTO :END
)
:END