#!/bin/bash

# Create release ZIP with version and date

VERSION="3.0.0"
DATE=$(date +%Y%m%d)
RELEASE_NAME="ubuntu-air-lab-v${VERSION}-${DATE}"

echo "Creating release: ${RELEASE_NAME}.zip"

# Create ZIP
zip -r "${RELEASE_NAME}.zip" dist/

# Show result
ls -lh "${RELEASE_NAME}.zip"

echo ""
echo "✅ Release created: ${RELEASE_NAME}.zip"
echo ""
echo "📤 Ready for distribution!"
echo "   Size: $(du -h ${RELEASE_NAME}.zip | cut -f1)"
echo ""
