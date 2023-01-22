@echo off
taskkill /IM Mindustry.exe
echo Killed Mindustry
PING -n 3 127.0.0.1 > nul
echo Waited
move "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\build\libs\*.*" "C:\Users\Radek Augustyn\AppData\Roaming\Mindustry\mods\"
echo Moved
rmdir /s /q "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\build\"
rmdir /s /q "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\.gradle\"
echo Cleaned up
echo Starting...
start /B /d "C:\KubaPrograms\Mindustry\relase-141.2" Mindustry.exe
echo Started