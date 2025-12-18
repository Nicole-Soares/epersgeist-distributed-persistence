@echo off
echo =========================================
echo   RELOADING EPERSGEIST MICROSERVICES
echo =========================================
echo.

REM --- SERVICES TO RELOAD ---
set SERVICES=epersgeist servicio_mensajeria servicio_temperatura servicio_probabilidad

echo [1/3] Deteniendo servicios...
docker compose stop %SERVICES%

echo.
echo [2/3] Rebuild de servicios con no-cache...
docker compose build --no-cache %SERVICES%

echo.
echo [3/3] Levantando servicios actualizados...
docker compose up -d --force-recreate %SERVICES%

echo.
echo =========================================
echo     RELOAD COMPLETO! 🚀
echo =========================================

pause