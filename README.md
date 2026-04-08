# Wanderly

**A location-aware Android travel companion with AI-powered recommendations, social features, and smart route planning.**

![Android](https://img.shields.io/badge/Android-26+-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20Storage-FFCA28?logo=firebase&logoColor=black)
![OpenAI](https://img.shields.io/badge/OpenAI-GPT--3.5-412991?logo=openai&logoColor=white)
![Google Maps](https://img.shields.io/badge/Google%20Maps-Places%20%7C%20Routing-4285F4?logo=googlemaps&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

---

## What It Does

Wanderly helps travelers discover, navigate, and share interesting places. It combines real-time location data with AI-driven travel advice and a social platform for sharing experiences.

**Core capabilities:**

| Feature | Description |
|---|---|
| **Nearby Discovery** | Finds churches, museums, parks, hotels, mountains, and libraries near you using Google Places API |
| **AI Travel Chatbot** | GPT-powered assistant that gives personalised recommendations based on your GPS location and preferences |
| **Smart Routing** | Calculates driving routes with distance and duration via OpenRouteService, displayed on Google Maps |
| **Social Posts** | Create and share travel posts with photos (uploaded to Cloudinary) and location tags |
| **Favorites** | Save and manage a personal collection of favorite places |
| **User Profiles** | Full authentication flow with profile pictures, email verification, and account management |
| **Preferences** | Questionnaire-based preference system that tailors recommendations to user interests |
| **Dark Mode** | System, dark, and light theme support |
| **Notifications** | Periodic background notifications via WorkManager |

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Platform** | Android (min SDK 26, target SDK 34) |
| **Language** | Java 11 |
| **Build** | Gradle (Kotlin DSL) |
| **Authentication** | Firebase Auth (email/password, email verification) |
| **Database** | Cloud Firestore |
| **File Storage** | Firebase Storage, Cloudinary (image uploads) |
| **Maps & Location** | Google Maps SDK, Google Places API, FusedLocationProvider |
| **Routing** | OpenRouteService API |
| **AI** | OpenAI Chat Completions API (GPT-3.5-turbo) |
| **Networking** | Volley (REST), OkHttp (OpenAI, Cloudinary) |
| **Image Loading** | Glide |
| **UI** | Material Design Components, SwipeRefreshLayout, CircleImageView |
| **Background** | AndroidX WorkManager |

---

## Project Structure

```
Wanderly-1.1.0/
├── app/
│   ├── build.gradle.kts              # App-level dependencies & config
│   ├── google-services.json          # Firebase config (gitignored)
│   └── src/main/
│       ├── AndroidManifest.xml       # Permissions & activity declarations
│       ├── java/com/example/anew/
│       │   ├── MainActivity.java             # Home screen — discovery, search, categories
│       │   ├── ChatActivity.java             # AI Travel Chatbot UI
│       │   ├── OpenAIHelper.java             # OpenAI API client
│       │   ├── ChatAdapter.java              # RecyclerView adapter for chat bubbles
│       │   ├── ChatMessage.java              # Chat message data model
│       │   ├── LoginActivity.java            # Email/password login
│       │   ├── RegisterActivity.java         # Registration + profile picture upload
│       │   ├── ProfileActivity.java          # Profile, settings, appearance
│       │   ├── EditProfileActivity.java      # Edit profile details
│       │   ├── QuestionnaireActivity.java    # Preference selection (categories)
│       │   ├── FavoritesActivity.java        # Saved favorite places
│       │   ├── PostsActivity.java            # Social feed
│       │   ├── CreatePostActivity.java       # Create post + Cloudinary upload
│       │   ├── PostsAdapter.java             # Posts RecyclerView adapter
│       │   ├── Post.java                     # Post data model
│       │   ├── MapActivity.java              # Route display with OpenRouteService
│       │   ├── KartaActivity.java            # Additional map view
│       │   ├── CordinatesFinderChurches.java # Google Places API — category search
│       │   ├── TheNearestChurch.java         # Nearest-place finder with distance matrix
│       │   ├── PlaceAdapter.java             # Favorites place adapter
│       │   ├── PlaceAdapter_2.java           # Category search place adapter
│       │   ├── Place.java                    # Favorite place model
│       │   ├── Place_2.java                  # Category place model
│       │   ├── User.java                     # User data model
│       │   ├── NotificationsActivity.java    # Notifications screen
│       │   ├── HelpCenterActivity.java       # Help & FAQ
│       │   ├── SourcesActivity.java          # Data source attributions
│       │   ├── BootReceiver.java             # Re-schedules notifications on boot
│       │   ├── HiWorker.java                 # Periodic notification worker
│       │   ├── MyApp.java                    # Application class
│       │   ├── CommentAdapter.java           # Comment display adapter
│       │   ├── CommentsAdapter.java          # Comments section adapter
│       │   ├── CustomSwipeRefreshLayout.java # Custom swipe gesture handling
│       │   └── InertialScrollView.java       # Custom scrolling behavior
│       └── res/
│           ├── layout/          # 25 XML layouts
│           ├── drawable/        # Icons, backgrounds, shapes
│           ├── anim/            # Slide animations
│           ├── values/          # Colors, strings, themes
│           └── values-night/    # Dark theme overrides
├── build.gradle.kts             # Project-level Gradle config
├── settings.gradle.kts          # Module settings
├── gradle/                      # Gradle wrapper & version catalog
├── .gitignore
└── README.md
```

---

## Setup & Installation

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 11+**
- **Android SDK 34**
- A physical device or emulator running **Android 8.0+ (API 26)**

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/wanderly.git
cd wanderly
```

### 2. Firebase Setup

1. Create a project at [Firebase Console](https://console.firebase.google.com/)
2. Enable **Authentication** (Email/Password provider)
3. Enable **Cloud Firestore**
4. Enable **Firebase Storage**
5. Download `google-services.json` and place it in `app/`

### 3. API Keys

Add your Google Maps API key to `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

Enable these APIs in your [Google Cloud Console](https://console.cloud.google.com/):
- Maps SDK for Android
- Places API
- Distance Matrix API

### 4. Build & Run

1. Open the project in Android Studio
2. Sync Gradle
3. Select a device/emulator
4. Click **Run ▶**

---

## How to Run

| Action | Method |
|---|---|
| **Build debug APK** | `./gradlew assembleDebug` |
| **Install on device** | `./gradlew installDebug` |
| **Run tests** | `./gradlew test` |
| **Lint check** | `./gradlew lint` |

---

## Environment Variables & API Keys

The following keys and services are required for full functionality:

| Key | Where to Set | Required | Purpose |
|---|---|---|---|
| `GOOGLE_MAPS_API_KEY` | `AndroidManifest.xml` | ✅ | Maps, Places, Distance Matrix |
| `OPENAI_API_KEY` | In-app settings dialog | ✅ | AI Travel Chatbot |
| `ORS_API_KEY` | `MapActivity.java` | ✅ | Route directions (OpenRouteService) |
| **Firebase Config** | `google-services.json` | ✅ | Auth, Firestore, Storage |
| **Cloudinary** | `RegisterActivity.java`, `CreatePostActivity.java` | ✅ | Profile picture & post image uploads |

### External Services

| Service | Purpose | Sign Up |
|---|---|---|
| [Firebase](https://firebase.google.com/) | Authentication, database, storage | Free tier available |
| [Google Cloud Platform](https://console.cloud.google.com/) | Maps, Places, Distance Matrix APIs | Free tier (with limits) |
| [OpenAI](https://platform.openai.com/) | GPT-3.5-turbo for travel chatbot | Pay-as-you-go |
| [OpenRouteService](https://openrouteservice.org/) | Driving route directions | Free tier available |
| [Cloudinary](https://cloudinary.com/) | Image hosting for posts & profiles | Free tier available |

---

## Architecture

```
┌──────────────────────────────────────────────────┐
│                   Android App                     │
│                                                   │
│  ┌──────────┐  ┌──────────┐  ┌────────────────┐  │
│  │ Discovery │  │  Social  │  │  AI Chatbot    │  │
│  │  Module   │  │  Module  │  │  Module        │  │
│  └─────┬────┘  └────┬─────┘  └───────┬────────┘  │
│        │             │                │            │
│  ┌─────▼─────────────▼────────────────▼────────┐  │
│  │              Activity Layer                  │  │
│  │  MainActivity · PostsActivity · ChatActivity │  │
│  └─────────────────────┬───────────────────────┘  │
│                        │                           │
│  ┌─────────────────────▼───────────────────────┐  │
│  │           Service / Helper Layer             │  │
│  │  OpenAIHelper · CordinatesFinderChurches     │  │
│  │  TheNearestChurch · Adapters                 │  │
│  └─────────────────────┬───────────────────────┘  │
└────────────────────────┼───────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   ┌────▼───┐    ┌──────▼──────┐   ┌─────▼─────┐
   │Firebase │    │Google APIs  │   │ OpenAI    │
   │Auth/DB/ │    │Maps/Places/ │   │ GPT-3.5   │
   │Storage  │    │Distance     │   │           │
   └─────────┘    └─────────────┘   └───────────┘
```

---

## Key Features in Detail

### AI Travel Chatbot
- Powered by OpenAI GPT-3.5-turbo
- Location-aware: uses GPS coordinates for relevant suggestions
- Preference-aware: incorporates user's saved category preferences
- Conversation memory: maintains chat context (up to 20 messages)
- API key stored locally in SharedPreferences (user-provided)

### Nearby Place Discovery
- Google Places API integration with keyword search
- Supports 6 categories: Churches, Hotels, Libraries, Parks, Museums, Mountains
- Distance calculation via Google Distance Matrix API
- Configurable search radius
- Duplicate filtering by place name + coordinates

### Social Platform
- Create posts with text, images, and location tags
- Image uploads to Cloudinary CDN
- Firestore-backed post storage with real-time loading
- Like and comment system
- Pull-to-refresh feed

---

## License

This project is available under the [MIT License](LICENSE).
