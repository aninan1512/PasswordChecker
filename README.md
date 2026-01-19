# PasswordChecker (Kotlin + JavaFX)

A desktop **Password Strength Checker** application built with **Kotlin** and **JavaFX**.  
The app evaluates password strength in real time, highlights security requirements, and helps users generate strong passwords.

---

## Features
- Real-time password strength scoring (0–100)
- Strength levels from *Very Weak* to *Very Strong*
- Rule-based validation:
  - Minimum length
  - Uppercase & lowercase letters
  - Numbers & special characters
  - Detection of common passwords
  - Detection of repeated and sequential patterns
- Show / Hide password toggle
- Strong password generator with configurable options
- Copy to clipboard and clear functionality

---

## Screenshots

### Main Interface
![Main Interface](screenshots/main-ui.png)

### Strong Password Output
![Strong Password Output](screenshots/strong-password.png)

---

## Tech Stack
- **Language:** Kotlin (JVM)
- **UI Framework:** JavaFX
- **Build Tool:** Gradle
- **JDK:** 21

---

## How to Run

### Prerequisites
- JDK 21 installed
- Gradle (or Gradle wrapper)

### Run the application

**Windows**
```bash
gradlew run
