# Images Directory

Place your image assets here:

## Required Images

### logo.png
- **Purpose**: Application logo displayed on login screen and title bar
- **Recommended size**: 300x100 pixels
- **Format**: PNG with transparent background
- **Location**: `/src/main/resources/images/logo.png`

## Additional Images (Optional)

You can add additional images here such as:
- Icons for buttons
- Background images
- Status indicators
- Custom markers for maps

## Usage in Code

To load images in JavaFX:

```java
// In Java code
Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));

// In FXML
<ImageView>
    <image>
        <Image url="@/images/logo.png"/>
    </image>
</ImageView>
```

## Note

Currently, the application will work without the logo file, but it's recommended to add it for a professional appearance.
