@echo off
taskkill /IM Mindustry.exe
echo Killed Mindustry
timeout 5 > nul
echo Waited
move "C:\Users\Radek Augustyn\Desktop\Kubik\MindustryToolkit\build\libs\*.*" "C:\Users\Radek Augustyn\AppData\Roaming\Mindustry\mods"
echo Moved
echo Starting...
start /B /d "C:\KubaPrograms\Mindustry\relase-140.3" Mindustry.exe
echo Started