#!/bin/bash

# AIGE-APRON-SMART Startup Script

echo "====================================="
echo "  AIGE-APRON-SMART v3.0"
echo "  Starting Application..."
echo "====================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Error: Java 17 or higher is required"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

echo "Java version: $(java -version 2>&1 | head -n 1)"
echo "Maven version: $(mvn -version | head -n 1)"
echo ""

# Clean and compile
echo "Compiling application..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Error: Compilation failed"
    exit 1
fi

echo ""
echo "Starting AIGE-APRON-SMART..."
echo ""

# Run the application
mvn javafx:run
