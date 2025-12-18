@echo off
echo =========================================
echo   RELOADING EPERSGEIST MICROSERVICES
echo =========================================
echo.

REM --- SERVICES TO RELOAD ---
set SERVICES=epersgeist servicio_mensajeria servicio_temperatura servicio_probabilidad

REM --- PROFILES TO USE ---
set PROFILES_ARGS=--profile kafka --profile app --profile micro --profile nosql

echo [1/3] Deteniendo servicios...
docker-compose %PROFILES_ARGS% stop %SERVICES%

echo.
echo [2/3] Rebuild de servicios con no-cache...
docker-compose %PROFILES_ARGS% build --no-cache %SERVICES%

echo.
echo [3/3] Levantando servicios actualizados...
docker-compose %PROFILES_ARGS% up -d --force-recreate %SERVICES%

echo.
echo =========================================
echo     RELOAD COMPLETO! 🚀
echo =========================================

pause