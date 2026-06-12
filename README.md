Status Report: June 6, 2026
Added Student and Registration Module in the repository

Status Report: June 12, 2026
Finished Adding FXMLs and Controllers - procceed with backend and database

Status Report: June 13, 2026 
Polished System (fixed encountered errors and non-functional buttons); waiting for QA/Testers to test the program before deploying the application.

For your Information:

* **Language:** Java 17
* **GUI Framework:** JavaFX
* **Build Tool:** Maven
* **Database:** Supabase / JDBC

```text
FindIT-Final-Project-OOP-BSIT/
├── pom.xml                               # Maven dependencies and Java 17 compiler config
├── README.md                             # Project documentation
├── src/
│   ├── create_user_tables.sql            # SQL script to initialize the database schema
│   │
│   └── main/
│       ├── java/com/example/findit/
│       │   ├── Launcher.java             # Safely launches the JavaFX application
│       │   ├── ProjectApplication.java   # Main JavaFX Stage and Scene configuration
│       │   ├── module-info.java          # Java Module System configuration
│       │   │
│       │   ├── controllers/              # UI LOGIC & EVENT HANDLERS
│       │   │   ├── MainPortalController.java
│       │   │   ├── RegistrationController.java
│       │   │   ├── admin/                # Admin-specific logic (Dashboard, Claims, Matches)
│       │   │   └── user/                 # User-specific logic (Forms, Gallery, Navigation)
│       │   │
│       │   ├── dao/                      # DATABASE OPERATIONS (Data Access Objects)
│       │   │   ├── ActivityLogDAO.java
│       │   │   └── UserDAO.java
│       │   │
│       │   ├── model/                    # DATA BLUEPRINTS
│       │   │   └── User.java
│       │   │
│       │   └── util/                     # HELPER CLASSES
│       │       └── DBConnection.java     # JDBC connection credentials
│       │
│       └── resources/com/example/findit/
│           ├── Registration.fxml
│           ├── assets/                   # ALL IMAGES & ICONS (png, jpg)
│           │   └── yellow_icons/         # Active-state dynamic UI icons
│           │
│           └── views/                    # FXML VISUAL LAYOUTS
│               ├── admin/                # Admin portal screens (AdminDashboard, Claims, etc.)
│               └── user/                 # User portal screens (FoundForm, Dashboard, etc.)
