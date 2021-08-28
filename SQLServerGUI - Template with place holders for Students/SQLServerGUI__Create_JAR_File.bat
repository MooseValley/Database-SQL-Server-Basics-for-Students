REM -----------------------------
REM SQLServerGUI
REM -----------------------------
echo off
cls
REM

REM echo %JAVA_HOME%
REM echo %CLASS_PATH%

SET "dirlocation=%JAVA_HOME%\bin\"

echo "%dirlocation%"
del /q *.class
echo Create the Manifest file:
echo Main-Class: SQLServerGUI >MANIFEST.MF
echo .

"%dirlocation%javac.exe" SQLServerGUI.java

"%dirlocation%jar.exe" cfm SQLServerGUI.jar MANIFEST.MF *.class ..\icons\*.gif ..\icons\*.png  SQLServerGUI*.txt

REM
del /q *.class

:END
echo .
echo Finished!
pause