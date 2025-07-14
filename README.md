# FreeTamilEBooks

FreeTamilEBooks is a simple and beautiful Android app for reading Tamil e-books from the FreeTamilEBooks.com website. The app is built with modern Android development tools and follows the latest architectural guidelines.

## Screenshots

*(placeholder for screenshots)*

## Features

*   **Browse and download** a vast collection of Tamil e-books.
*   **Read e-books** in EPUB format with a clean and user-friendly interface.
*   **Search** for your favorite books and authors.
*   **Bookshelf** to manage your downloaded e-books.
*   **Dark mode** support for a comfortable reading experience.

## Tech Stack and Architecture

The app is built with a 100% Kotlin-first approach and follows the official Android architecture guidelines.

*   **UI:** Jetpack Compose for building a declarative and modern UI.
*   **Architecture:** MVVM (Model-View-ViewModel) with a layered architecture inspired by Clean Architecture.
*   **Dependency Injection:** Hilt for managing dependencies.
*   **Asynchronous Programming:** Kotlin Coroutines for managing background threads.
*   **Networking:** Retrofit for making network requests to the FreeTamilEBooks.com API.
*   **Database:** Room for caching data and managing the bookshelf.
*   **Image Loading:** Coil for loading images efficiently.
*   **E-book Reader:** Readium Kotlin Toolkit for rendering EPUB files.
*   **Navigation:** Jetpack Navigation for navigating between screens.
*   **UI Components:** Material 3 for a modern and beautiful UI.

## Getting Started

To build and run the app, you'll need:

1.  Android Studio Iguana | 2023.2.1 or later.
2.  Kotlin plugin 2.2.0 or later.

Clone the repository and open it in Android Studio.

```bash
git clone https://github.com/jskaleel/FreeTamilEBooks.git
```

The app should build and run without any additional configuration.

## Project Structure

The project is divided into two modules:

*   **`app`:** The main application module, containing the UI and feature-related code.
*   **`epub`:** A library module for handling EPUB files, based on the Readium Kotlin Toolkit.

The `app` module follows a layered architecture:

*   **`ui`:** Contains all the UI-related code, including Composables, ViewModels, and Navigation.
*   **`domain`:** Contains the business logic of the app, including UseCases and domain models.
*   **`data`:** Contains the data layer of the app, including repositories, data sources (remote and local), and data models.
*   **`di`:** Contains the Hilt modules for dependency injection.
*   **`core`:** Contains utility classes and extension functions.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
