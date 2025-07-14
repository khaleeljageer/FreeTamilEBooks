# FTE Books - Free Tamil EBooks Android App

<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="FTE Books Logo" width="120" height="120">
</p>

A modern Android application for browsing, downloading, and reading free Tamil eBooks. Built with Jetpack Compose and following Clean Architecture principles.

## 📱 Features

- **Browse Tamil EBooks**: Explore a curated collection of free Tamil books
- **Search Functionality**: Find books by title, author, or category
- **Offline Reading**: Download books for offline access
- **EPUB Reader**: Built-in EPUB reader with advanced features
- **Category Organization**: Books organized by different categories
- **Download Management**: Track download progress with notifications
- **Beautiful UI**: Modern Material 3 design with Tamil font support

## 🏗️ Architecture

This app follows **Clean Architecture** principles with **MVVM** pattern:

### Layers
- **Presentation Layer**: Jetpack Compose UI, ViewModels, Navigation
- **Domain Layer**: Use Cases, Domain Models, Business Logic
- **Data Layer**: Repositories, Local & Remote Data Sources

### Key Components
- **UI**: Jetpack Compose with Material 3
- **Navigation**: Navigation Compose with type-safe routing
- **DI**: Dagger Hilt for dependency injection
- **Database**: Room for local storage
- **Networking**: Retrofit for API communication
- **EPUB Reading**: Readium Toolkit integration

## 🛠️ Tech Stack

### Core Android
- **Kotlin** - Modern programming language
- **Jetpack Compose** - Declarative UI toolkit
- **Material 3** - Latest Material Design
- **Navigation Compose** - Type-safe navigation
- **ViewModel & LiveData** - MVVM architecture components

### Data & Storage
- **Room** - Local database (v2.7.2)
- **DataStore** - Modern SharedPreferences replacement
- **Retrofit** - REST API client (v3.0.0)
- **Gson** - JSON serialization

### Dependency Injection
- **Dagger Hilt** - Dependency injection framework

### Image Loading & UI
- **Coil** - Modern image loading library (v3.2.0)
- **Lottie** - Animation library (v6.6.7)
- **Material Icons Extended** - Comprehensive icon set

### File & Download Management
- **PRDownloader** - Advanced download library
- **Accompanist Permissions** - Permission handling

### EPUB Reading
- **Readium Kotlin Toolkit** (v3.1.1) - Professional EPUB reader
- **Media3** - Media playback for TTS features
- **Timber** - Enhanced logging

### Development Tools
- **Detekt** - Static code analysis
- **KSP** - Kotlin Symbol Processing

## 📦 Project Structure

```
app/
├── src/main/java/com/jskaleel/fte/
│   ├── core/                    # Core utilities and models
│   ├── data/                    # Data layer
│   │   ├── model/              # Data models
│   │   ├── repository/         # Repository implementations
│   │   └── source/             # Data sources (local/remote)
│   ├── di/                     # Dependency injection modules
│   ├── domain/                 # Domain layer
│   │   ├── model/              # Domain models
│   │   └── usecase/            # Business logic use cases
│   └── ui/                     # Presentation layer
│       ├── navigation/         # Navigation setup
│       ├── screens/            # UI screens
│       ├── theme/              # App theming
│       └── utils/              # UI utilities
└── res/                        # Resources (layouts, strings, assets)

epub/                           # EPUB reader module
├── src/main/java/com/jskaleel/epub/
│   ├── data/                   # EPUB data management
│   ├── domain/                 # EPUB domain logic
│   ├── drm/                    # DRM management
│   ├── reader/                 # Core reading functionality
│   └── utils/                  # EPUB utilities
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17+
- Android SDK 26+ (minimum)
- Gradle 8.11.1

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/FreeTamilEBooks.git
   cd FreeTamilEBooks
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 🔧 Development

### Code Quality
The project uses Detekt for static code analysis:
```bash
./gradlew detekt
```

### Testing
Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

### Architecture Guidelines
- Follow Clean Architecture principles
- Use MVVM pattern for UI components
- Implement Repository pattern for data access
- Use Use Cases for business logic
- Apply SOLID principles
- Write unit tests for business logic

## 📚 Key Features Deep Dive

### Book Management
- **Synchronization**: 24-hour automatic sync with remote repository
- **Offline-First**: Local storage with Room database
- **Categories**: Organized book browsing experience
- **Search**: Full-text search across titles and authors

### Download System
- **Background Downloads**: Continue downloads in background
- **Progress Tracking**: Real-time download progress
- **Notification Support**: Download status notifications
- **Storage Management**: Efficient file organization

### EPUB Reader
- **Professional Reading**: Powered by Readium Toolkit
- **Customization**: Font size, theme, and reading preferences
- **TTS Support**: Text-to-speech functionality
- **Bookmarks & Highlights**: Reading progress tracking

## 🌍 Tamil Language Support

The app is specifically designed for Tamil language content:
- **Tamil Font**: Custom Marutham font for better readability
- **Localized UI**: Tamil strings and interface elements
- **Unicode Support**: Full Tamil Unicode character support
- **Cultural Context**: Designed for Tamil reading experience

## 🤝 Contributing

This project is part of the FreeTamilEBooks initiative, which provides free Tamil literature under Creative Commons license.

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

The books available through this app are provided under Creative Commons licenses, ensuring free access to Tamil literature.

## 🙏 Acknowledgments

- **FreeTamilEbooks.com** - Source of free Tamil literature
- **Readium Foundation** - EPUB reading technology
- **Project Madurai** - Tamil digitization efforts
- **Free Software Foundation Tamil Nadu** - Open source advocacy
- **Yavarukkum Software Foundation** - Community support

## 📞 Contact

- **Email**: freetamilebooksteam@gmail.com
- **Facebook**: [FreeTamilEbooks](https://www.facebook.com/FreeTamilEbooks)
- **Website**: [FreeTamilEbooks.com](https://freetamilebooks.com)

## 🔄 Build Information

- **Application ID**: `com.jskaleel.fte`
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Version**: 1.0

---

**Made with ❤️ for Tamil Literature Lovers**