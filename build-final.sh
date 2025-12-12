#!/bin/bash

# UbuntuAirLab - Final Build Script
# Creates a truly portable executable using Maven exec

set -e

echo "════════════════════════════════════════════════════════"
echo "  UbuntuAirLab - Creating Portable Executable"
echo "════════════════════════════════════════════════════════"

# Build the JAR
echo ""
echo "📦 Step 1: Building JAR with dependencies..."
mvn clean package -DskipTests

if [ ! -f "target/ubuntu-air-lab-3.0.0.jar" ]; then
    echo "❌ Error: JAR file not found!"
    exit 1
fi

echo "✅ JAR built successfully"

# Create distribution directory
echo ""
echo "📦 Step 2: Creating distribution package..."
rm -rf dist
mkdir -p dist

# Copy JAR
cp target/ubuntu-air-lab-3.0.0.jar dist/

# Create comprehensive launcher
cat > dist/ubuntu-air-lab.sh << 'EOFSH'
#!/bin/bash

##############################################################################
# UbuntuAirLab - Airport Ground Equipment Management System v3.0.0
# Launcher Script for Linux/Mac
##############################################################################

cd "$(dirname "$0")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "═══════════════════════════════════════════════════════════"
echo "  UbuntuAirLab v3.0.0 - Starting..."
echo "═══════════════════════════════════════════════════════════"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Error: Java not found!${NC}"
    echo "Please install Java 17 or higher from: https://adoptium.net/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' | head -1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${YELLOW}⚠️  Warning: Java $JAVA_VERSION detected. Java 17+ recommended${NC}"
fi

echo -e "${GREEN}✓${NC} Java detected: $(java -version 2>&1 | head -n 1)"
echo ""
echo "🚀 Launching UbuntuAirLab..."
echo ""

# Run the application
java -Xmx1024m \
     -Dfile.encoding=UTF-8 \
     -Djavafx.preloader=com.sun.javafx.application.LauncherImpl \
     -jar ubuntu-air-lab-3.0.0.jar "$@"

EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo -e "${RED}❌ Application exited with error code: $EXIT_CODE${NC}"
    echo ""
    echo "Common solutions:"
    echo "  1. Ensure Java 17+ is installed"
    echo "  2. Check that no other instance is running"
    echo "  3. Verify internet connection for API access"
    exit $EXIT_CODE
fi
EOFSH

chmod +x dist/ubuntu-air-lab.sh

# Create Windows launcher
cat > dist/ubuntu-air-lab.bat << 'EOFBAT'
@echo off
REM UbuntuAirLab Launcher for Windows

echo ===============================================================
echo   UbuntuAirLab v3.0.0 - Starting...
echo ===============================================================

cd /d "%~dp0"

REM Check Java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    echo Please install Java 17 or higher from: https://adoptium.net/
    pause
    exit /b 1
)

echo Java detected: 
java -version
echo.
echo Launching UbuntuAirLab...
echo.

java -Xmx1024m -Dfile.encoding=UTF-8 -Djavafx.preloader=com.sun.javafx.application.LauncherImpl -jar ubuntu-air-lab-3.0.0.jar %*

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Application exited with error code: %ERRORLEVEL%
    echo.
    echo Common solutions:
    echo   1. Ensure Java 17+ is installed
    echo   2. Check that no other instance is running
    echo   3. Verify internet connection for API access
    pause
)
EOFBAT

# Create README
cat > dist/README.txt << 'EOFREADME'
═══════════════════════════════════════════════════════════════
  UbuntuAirLab v3.0.0
  Airport Ground Equipment Management System
═══════════════════════════════════════════════════════════════

📋 REQUIREMENTS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
• Java 17 or higher (Download: https://adoptium.net/)
• Internet connection for API access
• Minimum 2GB RAM
• Screen resolution: 1280x720 or higher

📦 INSTALLATION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Extract this ZIP file to any folder
2. Ensure Java is installed: java -version
3. Run the launcher for your platform

🚀 RUNNING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Linux/Mac:    ./ubuntu-air-lab.sh
Windows:      Double-click ubuntu-air-lab.bat

🔐 DEFAULT CREDENTIALS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Username: admin
Password: admin123

API Endpoint: https://air-lab.bestwebapp.tech/api/v1

✨ FEATURES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ 36 API Endpoints Integrated
✅ Real-time Flight Tracking (OpenSky Network)
✅ Intelligent Parking Management (18 spots: N1-N2, P1-P5, S1-S10B)
✅ ML-powered Predictions (delays, congestion, duration)
✅ Alert System with Smart Notifications
✅ Planning & Scheduling Tools
✅ 3D Airport Visualization
✅ Batch Prediction Processing
✅ Automated Data Synchronization

📚 DOCUMENTATION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
GitHub: https://github.com/UbuntuairLab/ubuntuairlab-frontend

Full guides available in repository:
- USER_GUIDE.md - Complete user manual
- API_CONTRACT.md - API documentation
- DEPLOYMENT.md - Deployment instructions
- QUICK_REFERENCE.md - Quick start guide

🐛 TROUBLESHOOTING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Problem: "Java not found"
Solution: Install Java from https://adoptium.net/

Problem: "JavaFX components missing"
Solution: Ensure you're using Java 17+ (not Java 8)

Problem: "Connection refused"
Solution: Check internet connection and API endpoint status

Problem: Application won't start
Solution: 
  1. Close any running instances
  2. Delete logs/aige-apron-smart.log
  3. Try running from command line to see errors

📧 SUPPORT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Report issues: https://github.com/UbuntuairLab/ubuntuairlab-frontend/issues

═══════════════════════════════════════════════════════════════
© 2025 AIGE - All rights reserved
═══════════════════════════════════════════════════════════════
EOFREADME

# Get JAR size
JAR_SIZE=$(du -h dist/ubuntu-air-lab-3.0.0.jar | cut -f1)

echo ""
echo "════════════════════════════════════════════════════════"
echo "✅ BUILD COMPLETE!"
echo "════════════════════════════════════════════════════════"
echo ""
echo "📦 Distribution: ./dist/"
echo "📊 JAR size: $JAR_SIZE"
echo ""
ls -lh dist/
echo ""
echo "🚀 Quick Test:"
echo "   cd dist && ./ubuntu-air-lab.sh"
echo ""
echo "📦 Create Distribution ZIP:"
echo "   ./create-release.sh"
echo ""
