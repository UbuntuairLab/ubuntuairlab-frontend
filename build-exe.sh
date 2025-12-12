#!/bin/bash

# UbuntuAirLab - Build Executable Script
# Creates a native installer for Linux/Windows/macOS

set -e

echo "════════════════════════════════════════════════════════"
echo "  UbuntuAirLab - Building Native Executable"
echo "════════════════════════════════════════════════════════"

# Clean and build JAR with dependencies
echo ""
echo "📦 Step 1: Building JAR with dependencies..."
mvn clean package -DskipTests

# Check if JAR was created
if [ ! -f "target/ubuntu-air-lab-3.0.0.jar" ]; then
    echo "❌ Error: JAR file not found!"
    exit 1
fi

echo "✅ JAR built successfully"

# Create jpackage directory
echo ""
echo "📦 Step 2: Preparing jpackage resources..."
mkdir -p target/jpackage

# Build native installer using jpackage
echo ""
echo "📦 Step 3: Creating native installer..."
echo "   This may take a few minutes..."

jpackage \
  --input target \
  --name "UbuntuAirLab" \
  --main-jar ubuntu-air-lab-3.0.0.jar \
  --main-class com.aige.apronsmart.AigApronSmartApplication \
  --type deb \
  --app-version "3.0.0" \
  --vendor "AIGE" \
  --description "UbuntuAirLab - Airport Ground Equipment Management System" \
  --dest target/installer \
  --linux-shortcut \
  --linux-menu-group "Office" \
  --java-options '-Xmx1024m' \
  --java-options '-Dfile.encoding=UTF-8'

echo ""
echo "════════════════════════════════════════════════════════"
echo "✅ BUILD COMPLETE!"
echo "════════════════════════════════════════════════════════"
echo ""
echo "📦 Installer location: target/installer/"
echo ""
ls -lh target/installer/
echo ""
echo "🚀 To install on Ubuntu/Debian:"
echo "   sudo dpkg -i target/installer/ubuntuairlab_3.0.0-1_amd64.deb"
echo ""
echo "💡 To create Windows .exe or macOS .dmg:"
echo "   Run this script on the target platform"
echo ""
