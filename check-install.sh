#!/bin/bash

# AIGE-APRON-SMART Installation Checker

echo "======================================"
echo "  AIGE-APRON-SMART v3.0"
echo "  Installation Checker"
echo "======================================"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check Java
echo -n "Checking Java... "
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "${GREEN}✓ Java $JAVA_VERSION installed${NC}"
    else
        echo -e "${RED}✗ Java version too old (need 17+)${NC}"
    fi
else
    echo -e "${RED}✗ Java not found${NC}"
    echo "  Install from: https://adoptium.net/"
fi

# Check Maven
echo -n "Checking Maven... "
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version 2>&1 | head -n 1 | awk '{print $3}')
    echo -e "${GREEN}✓ Maven $MVN_VERSION installed${NC}"
else
    echo -e "${RED}✗ Maven not found${NC}"
    echo "  Install from: https://maven.apache.org/download.cgi"
fi

# Check project structure
echo ""
echo "Checking project structure..."

check_file() {
    if [ -f "$1" ]; then
        echo -e "  ${GREEN}✓${NC} $1"
    else
        echo -e "  ${RED}✗${NC} $1 (missing)"
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "  ${GREEN}✓${NC} $1/"
    else
        echo -e "  ${RED}✗${NC} $1/ (missing)"
    fi
}

# Check key files
check_file "pom.xml"
check_file "run.sh"
check_file "README.md"
check_file "src/main/java/com/aige/apronsmart/AigApronSmartApplication.java"
check_file "src/main/resources/application.properties"
check_file "src/main/resources/css/main.css"

# Check directories
check_dir "src/main/java/com/aige/apronsmart/controllers"
check_dir "src/main/java/com/aige/apronsmart/models"
check_dir "src/main/java/com/aige/apronsmart/services"
check_dir "src/main/resources/fxml"

# Count files
echo ""
echo "Project statistics:"
JAVA_FILES=$(find src -name "*.java" 2>/dev/null | wc -l)
FXML_FILES=$(find src -name "*.fxml" 2>/dev/null | wc -l)
echo "  Java files: $JAVA_FILES"
echo "  FXML files: $FXML_FILES"

# Check optional files
echo ""
echo "Optional components:"
if [ -f "src/main/resources/images/logo.png" ]; then
    echo -e "  ${GREEN}✓${NC} Logo image found"
else
    echo -e "  ${YELLOW}⚠${NC} Logo image not found (add to src/main/resources/images/logo.png)"
fi

# Backend check
echo ""
echo -n "Checking backend API... "
API_URL=$(grep "api.base.url" src/main/resources/application.properties | cut -d'=' -f2)
echo "URL: $API_URL"
if command -v curl &> /dev/null; then
    if curl -s --connect-timeout 2 "$API_URL/health" &> /dev/null; then
        echo -e "  ${GREEN}✓${NC} Backend is reachable"
    else
        echo -e "  ${YELLOW}⚠${NC} Backend not reachable (this is normal if not started yet)"
    fi
else
    echo -e "  ${YELLOW}⚠${NC} curl not found (cannot check backend)"
fi

# Summary
echo ""
echo "======================================"
if command -v java &> /dev/null && command -v mvn &> /dev/null; then
    echo -e "${GREEN}✓ Ready to build and run!${NC}"
    echo ""
    echo "Next steps:"
    echo "  1. mvn clean install"
    echo "  2. mvn javafx:run"
    echo "  OR"
    echo "  ./run.sh"
else
    echo -e "${RED}✗ Installation incomplete${NC}"
    echo ""
    echo "Please install missing dependencies:"
    [ ! command -v java &> /dev/null ] && echo "  - Java 17+ (https://adoptium.net/)"
    [ ! command -v mvn &> /dev/null ] && echo "  - Maven 3.6+ (https://maven.apache.org/)"
fi
echo "======================================"
