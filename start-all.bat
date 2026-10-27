@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "PATH=%JAVA_HOME%\bin;C:\Users\abhij\.gemini\antigravity\scratch\maven\apache-maven-3.9.6\bin;%PATH%"

echo ========================================================
echo   Starting Spring Boot Microservices Full Stack App
echo ========================================================

echo Starting Product Service on Port 8081...
start "Product Service (:8081)" cmd /k "cd /d %~dp0product-service && java -jar target\product-service-1.0.0.jar"

echo Starting Order Service on Port 8082...
start "Order Service (:8082)" cmd /k "cd /d %~dp0order-service && java -jar target\order-service-1.0.0.jar"

echo Starting Frontend Web App on Port 3000...
start "Frontend (:3000)" cmd /k "cd /d %~dp0 && npx serve -l 3000"

echo All services launched!
echo Access the Frontend at: http://localhost:3000
