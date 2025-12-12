# UbuntuAirLab - Airport Ground Equipment Management System

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://adoptium.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A comprehensive JavaFX desktop application for managing airport ground operations, including real-time flight tracking, intelligent parking management, ML-powered predictions, and automated alerts.

## 🚀 Features

- **Real-Time Flight Tracking**: Integration with OpenSky Network for live aircraft positions
- **Intelligent Parking Management**: 18 parking spots (N1-N2, P1-P5, S1-S10B) with automated assignment
- **ML Predictions**: Delay forecasting, congestion analysis, and duration predictions
- **Smart Alerts**: Real-time notifications for critical events
- **Planning & Scheduling**: Advanced tools for flight and resource management
- **3D Visualization**: Interactive airport view with real-time updates
- **API Integration**: 36 endpoints fully integrated with UbuntuAirLab API
- **Batch Processing**: Mass prediction and data synchronization capabilities

## 📋 Prerequisites

- **Java Development Kit (JDK)**: 17 or higher
  - Download: [Eclipse Temurin (Adoptium)](https://adoptium.net/)
- **Apache Maven**: 3.6 or higher
  - Installation: [Maven Installation Guide](https://maven.apache.org/install.html)
- **Internet Connection**: Required for API access and real-time data

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/UbuntuairLab/ubuntuairlab-frontend.git
cd ubuntuairlab-frontend
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn javafx:run
```

## 📦 Building Executable

To create a portable executable JAR:

```bash
# Clean and package
mvn clean package -DskipTests

# JAR will be created at: target/ubuntu-air-lab-3.0.0.jar
```

### Running the JAR

```bash
java -Xmx1024m -Dfile.encoding=UTF-8 -jar target/ubuntu-air-lab-3.0.0.jar
```

## 🏗️ Project Structure

```
ubuntuairlab-frontend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/aige/apronsmart/
│       │       ├── AigApronSmartApplication.java    # Main entry point
│       │       ├── controllers/                      # UI Controllers
│       │       │   ├── LoginController.java
│       │       │   ├── DashboardController.java
│       │       │   ├── RegisterController.java
│       │       │   └── modules/                      # Module controllers
│       │       │       ├── AlertsController.java
│       │       │       ├── HistoryController.java
│       │       │       ├── NotificationsController.java
│       │       │       ├── PlanningController.java
│       │       │       ├── PostesController.java     # Parking management
│       │       │       ├── PredictionsController.java
│       │       │       ├── RadarController.java      # Real-time tracking
│       │       │       └── Visualization3dController.java
│       │       ├── models/                           # Data models
│       │       │   ├── Alert.java
│       │       │   ├── Flight.java
│       │       │   ├── ParkingAllocation.java
│       │       │   ├── ParkingSpot.java
│       │       │   ├── User.java
│       │       │   └── ...
│       │       ├── services/                         # Business logic
│       │       │   ├── BaseApiService.java           # Base HTTP client
│       │       │   ├── AuthService.java
│       │       │   ├── FlightService.java
│       │       │   ├── ParkingService.java
│       │       │   ├── PredictionService.java
│       │       │   ├── AlertService.java
│       │       │   ├── OpenSkyService.java           # Flight tracking
│       │       │   └── ...
│       │       └── utils/                            # Utilities
│       │           ├── Constants.java
│       │           ├── DateUtils.java
│       │           └── DialogUtils.java
│       └── resources/
│           ├── application.properties                # Configuration
│           ├── logback.xml                          # Logging config
│           ├── fxml/                                # UI layouts
│           │   ├── login.fxml
│           │   ├── dashboard.fxml
│           │   ├── register.fxml
│           │   └── modules/
│           ├── css/
│           │   └── main.css                         # Styles
│           ├── html/
│           │   └── visualization3d.html             # 3D view
│           └── images/                              # Icons & images
├── pom.xml                                          # Maven configuration
└── README.md
```

## 🔧 Configuration

### API Configuration

Edit `src/main/resources/application.properties`:

```properties
# API Configuration
api.base.url=https://air-lab.bestwebapp.tech/api/v1
api.timeout=30000

# Logging
logging.level=INFO

# Application
app.name=UbuntuAirLab
app.version=3.0.0
```

### Default Credentials

```
Username: admin
Password: admin123
```

## 🎯 Core Components

### 1. Authentication System (`AuthService`)

Handles user login, registration, and session management with JWT tokens.

```java
AuthService authService = AuthService.getInstance();
AuthResponse response = authService.login("admin", "admin123");
```

### 2. Flight Management (`FlightService`)

Provides access to flight data with filtering and pagination.

```java
FlightService flightService = FlightService.getInstance();
FlightsResponse flights = flightService.getFlights("active", "arrival", 50, 0, null);
```

### 3. Parking Management (`ParkingService`)

Manages parking spot allocations with automatic code mapping (P2 → API spot "2").

```java
ParkingService parkingService = ParkingService.getInstance();
Map<String, Object> result = parkingService.assignParking("icao24", "P2");
```

### 4. Real-Time Tracking (`OpenSkyService`)

Integrates with OpenSky Network for live aircraft positions.

```java
OpenSkyService openSkyService = OpenSkyService.getInstance();
List<Flight> liveFlights = openSkyService.getFlightsNearAirport(6.1656, 1.2544, 300.0);
```

### 5. ML Predictions (`PredictionService`)

Provides delay forecasts, congestion analysis, and flight duration estimates.

```java
PredictionService predictionService = PredictionService.getInstance();
Map<String, Object> delayPrediction = predictionService.predictDelay("icao24");
```

### 6. Alert System (`AlertService`)

Manages alerts with filtering, acknowledgment, and batch operations.

```java
AlertService alertService = AlertService.getInstance();
List<Alert> criticalAlerts = alertService.getAlerts("high", null, 100, 0);
```

## 📡 API Integration

The application integrates with 36 UbuntuAirLab API endpoints:

### Authentication (3 endpoints)
- POST `/auth/login` - User login
- POST `/auth/register` - User registration  
- POST `/auth/logout` - User logout

### Flights (9 endpoints)
- GET `/flights` - List all flights with filters
- GET `/flights/{icao24}` - Get flight details
- POST `/flights` - Create new flight
- PUT `/flights/{icao24}` - Update flight
- DELETE `/flights/{icao24}` - Delete flight
- GET `/flights/arrivals` - Get arrivals
- GET `/flights/departures` - Get departures
- GET `/flights/history` - Flight history
- GET `/flights/future` - Scheduled flights

### Parking (9 endpoints)
- GET `/parking/spots` - List all parking spots
- GET `/parking/spots/{id}` - Get spot details
- POST `/parking/spots` - Create parking spot
- PUT `/parking/spots/{id}` - Update spot
- DELETE `/parking/spots/{id}` - Delete spot
- GET `/parking/availability` - Check availability
- GET `/parking/allocations` - Current allocations
- POST `/parking/assign` - Assign spot to flight
- POST `/parking/release` - Release parking spot

### Predictions (5 endpoints)
- POST `/predictions/delay` - Predict flight delay
- POST `/predictions/congestion` - Predict congestion
- POST `/predictions/duration` - Predict duration
- POST `/predictions/batch` - Batch predictions
- GET `/predictions/models` - Available ML models

### Alerts (5 endpoints)
- GET `/alerts` - List all alerts
- GET `/alerts/{id}` - Get alert details
- POST `/alerts/acknowledge/{id}` - Acknowledge alert
- POST `/alerts/batch-acknowledge` - Batch acknowledge
- GET `/alerts/statistics` - Alert statistics

### Admin (3 endpoints)
- GET `/admin/users` - List all users
- GET `/admin/logs` - System logs
- GET `/admin/metrics` - System metrics

### Sync & Dashboard (2 endpoints)
- POST `/sync/flights` - Sync flight data
- GET `/dashboard/stats` - Dashboard statistics

## 🔍 Key Features Explained

### Parking Spot Code Mapping

The system uses a bidirectional mapping between UI codes and API numeric codes:

```java
// UI Code → API Code
P1 → "1" (Cargo)
P2 → "2" (Cargo)
P4 → "4" (Cargo)
S1 → "5" (Passenger)

// Example: Assigning flight to P2
parkingService.assignParking("ABC123", "P2");
// Internally converts P2 → "2" for API call
```

### Real-Time Flight Updates

The radar module auto-refreshes every 10 seconds, merging:
1. Local API flights
2. OpenSky Network live positions

```java
// In RadarController.java
private void enableAutoRefresh() {
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(10), e -> loadFlights())
    );
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}
```

### ML Prediction Workflow

```java
// Single prediction
Map<String, Object> result = predictionService.predictDelay("3c6444");

// Batch predictions (up to 50 flights)
List<String> icao24List = Arrays.asList("3c6444", "abc123", "def456");
Map<String, Object> batchResult = predictionService.batchPredict(icao24List);
```

## 🐛 Troubleshooting

### Issue: JavaFX runtime components missing

**Solution**: Ensure Java 17+ is installed. JavaFX is bundled with the shaded JAR.

### Issue: Connection refused / API errors

**Solution**: 
- Check internet connection
- Verify API endpoint: `https://air-lab.bestwebapp.tech/api/v1`
- Check firewall settings

### Issue: Compilation errors

**Solution**: Clean and rebuild:
```bash
mvn clean compile
```

### Issue: Parking assignments don't update UI

**Solution**: This is a known API limitation. Ensure:
1. Flight exists in the system (check Radar Live module)
2. Use correct ICAO24 format (6-character hex)
3. Spot code matches: P1, P2, P4, or S1

## 🧪 Testing

Run tests:
```bash
mvn test
```

Integration test (requires API access):
```bash
mvn test -Dtest=ApiIntegrationTest
```

## 📊 Performance

- **JAR Size**: 58MB (includes all dependencies)
- **Memory**: Recommended 1GB (-Xmx1024m)
- **Startup Time**: ~3-5 seconds
- **API Response**: Average 200-500ms

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **AIGE Team** - *Initial work* - [UbuntuairLab](https://github.com/UbuntuairLab)

## 🙏 Acknowledgments

- [OpenJFX](https://openjfx.io/) - JavaFX framework
- [OkHttp](https://square.github.io/okhttp/) - HTTP client
- [Jackson](https://github.com/FasterXML/jackson) - JSON processing
- [OpenSky Network](https://opensky-network.org/) - Real-time flight data
- [ControlsFX](https://controlsfx.github.io/) - Enhanced UI controls

## 📞 Support

For issues and questions:
- GitHub Issues: [Report a bug](https://github.com/UbuntuairLab/ubuntuairlab-frontend/issues)
- Email: support@ubuntuairlab.com

## 🔗 Links

- [API Documentation](https://air-lab.bestwebapp.tech/docs)
- [OpenSky Network API](https://openskynetwork.github.io/opensky-api/)
- [JavaFX Documentation](https://openjfx.io/javadoc/21/)

---

**Built with ❤️ by AIGE Team**
