@echo off
rem taskkill /IM Mindustry.exe
rem echo Killed Mindustry
rem PING -n 3 127.0.0.1 > nul
rem echo Waited
move "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\build\libs\*.*" "C:\Users\Radek Augustyn\AppData\Roaming\Mindustry\mods\"
echo Moved
rmdir /s /q "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\build\"
rmdir /s /q "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\.gradle\"
echo Cleaned up
echo Starting...
rem start /B /d "C:\KubaPrograms\Mindustry\relase-141.2" Mindustry.exe
cd "C:\KubaPrograms\Mindustry\relase-145.1"
java -jar Mindustry.jar
echo Started