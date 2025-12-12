#!/bin/bash

# UbuntuAirLab - Simplified Build Script
# Creates a portable JAR executable

set -e

echo "════════════════════════════════════════════════════════"
echo "  UbuntuAirLab - Building Executable JAR"
echo "════════════════════════════════════════════════════════"

# Clean and build JAR with dependencies
echo ""
echo "📦 Building self-contained JAR..."
mvn clean package -DskipTests

# Check if JAR was created
if [ ! -f "target/ubuntu-air-lab-3.0.0.jar" ]; then
    echo "❌ Error: JAR file not found!"
    exit 1
fi

echo ""
echo "✅ JAR built successfully"

# Create distribution directory
echo ""
echo "📦 Creating distribution package..."
mkdir -p dist
cp target/ubuntu-air-lab-3.0.0.jar dist/

# Create launcher script for Linux/Mac
cat > dist/ubuntu-air-lab.sh << 'EOF'
#!/bin/bash
# UbuntuAirLab Launcher
cd "$(dirname "$0")"

# Run using the embedded JavaFX libraries in the JAR
java -Xmx1024m \
     -Dfile.encoding=UTF-8 \
     -Dprism.order=sw \
     -cp ubuntu-air-lab-3.0.0.jar \
     com.aige.apronsmart.AigApronSmartApplication "$@"
EOF
chmod +x dist/ubuntu-air-lab.sh

# Create launcher script for Windows
cat > dist/ubuntu-air-lab.bat << 'EOF'
@echo off
cd /d "%~dp0"
java -Xmx1024m -Dfile.encoding=UTF-8 -Dprism.order=sw -cp ubuntu-air-lab-3.0.0.jar com.aige.apronsmart.AigApronSmartApplication %*
EOF

# Create README
cat > dist/README.txt << 'EOF'
UbuntuAirLab v3.0.0 - Airport Ground Equipment Management System
=================================================================

REQUIREMENTS:
- Java 17 or higher (https://adoptium.net/)

INSTALLATION:
1. Extract this folder anywhere on your computer
2. Make sure Java is installed: java -version

RUNNING THE APPLICATION:

On Linux/Mac:
  ./ubuntu-air-lab.sh

On Windows:
  Double-click ubuntu-air-lab.bat
  or run: ubuntu-air-lab.bat

CONFIGURATION:
- API URL: https://air-lab.bestwebapp.tech/api/v1
- Default credentials:
  Username: admin
  Password: admin123

FEATURES:
✅ 36 API endpoints integrated
✅ Real-time flight tracking (OpenSky Network)
✅ Parking spot management with auto-assignment
✅ ML predictions for delays and congestion
✅ Alert system with notifications
✅ Planning and scheduling tools
✅ 3D airport visualization

DOCUMENTATION:
See the project repository for full documentation:
https://github.com/UbuntuairLab/ubuntuairlab-frontend

SUPPORT:
For issues or questions, please open an issue on GitHub.

© 2025 AIGE - All rights reserved
EOF

# Get JAR size
JAR_SIZE=$(du -h dist/ubuntu-air-lab-3.0.0.jar | cut -f1)

echo ""
echo "════════════════════════════════════════════════════════"
echo "✅ BUILD COMPLETE!"
echo "════════════════════════════════════════════════════════"
echo ""
echo "📦 Distribution package created in: ./dist/"
echo "📊 JAR size: $JAR_SIZE"
echo ""
echo "📁 Contents:"
ls -lh dist/
echo ""
echo "🚀 To run the application:"
echo ""
echo "   Linux/Mac:   cd dist && ./ubuntu-air-lab.sh"
echo "   Windows:     cd dist && ubuntu-air-lab.bat"
echo ""
echo "💾 To create a ZIP for distribution:"
echo "   zip -r ubuntu-air-lab-v3.0.0.zip dist/"
echo ""
