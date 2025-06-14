# UnitCircleApp

An interactive JavaFX application for exploring the unit circle, trigonometric functions, and special right triangles. This educational tool supports multiple modes that help students visually and interactively learn sine, cosine, and tangent across quadrants.

---

## 📦 Project Overview

This project is a trigonometry learning tool with three interactive modes:

- **Exploration Mode**: Freely explore the unit circle by clicking labeled angle buttons.
- **Unit Circle Game Mode**: A 2-minute timed game where users match angles based on trigonometric values.
- **Special Triangles Mode**: Pop-up overlays display properly oriented 30-60-90 and 45-45-90 triangles.

---

## 🧰 Features and Technologies

- 🔵 **JavaFX GUI** with dynamic layout and scale-aware transformations
- 🟠 **Trigonometric overlays** that hide quadrants depending on sin/cos/tan positivity
- 🟢 **Interactive scoring and timer logic** for timed games
- 🟣 **Weighted angle generation** based on user mistakes for personalized practice
- 🟤 **Popup triangle diagrams** with orientation based on unit circle quadrants
- ⚪ Modular use of:
  - `Map`, `Set`, `List`
  - Lambdas and `Stream` filtering
  - `Timeline` animation
  - File I/O with fallback safety
- 🧪 Fully integrated **JUnit 5** and **TestFX** GUI testing
- 📊 **JaCoCo** test coverage reporting
- 🧱 UML diagram via PlantUML in both `.txt` and `.png` formats

---

## 📁 Installation & Setup

> ⚠️ Assumes Java 17+ and Maven are installed and configured correctly.

### 1. Clone the Repository

```bash
git clone https://github.com/penpro/CSIIFinal.git
cd CSIIFinal
```

### 2. Build and Run with Maven

```bash
mvn clean javafx:run
```

---

## 🖼️ Sample Output

### Unit Circle with Overlays

<img width="748" alt="image" src="https://github.com/user-attachments/assets/2a11d6ba-ca64-4e89-9308-1fd6846b7c8f" />

*Image: Quadrants are hidden to restrict options based on sine positivity.*

### Special Triangle Display

<img width="748" alt="image" src="https://github.com/user-attachments/assets/728e64c5-3437-4101-855f-21b749d510af" />

*Image: Special triangle rendered and rotated for Quadrant III.*

---

## ⚙️ Configuration Options

- **Modes**:
  - Exploration
  - Unit Circle Game
  - Special Triangles
- **Function Display Options**:
  - Degrees
  - Radians
  - sin(x)
  - cos(x)
  - tan(x)

---

## 🧪 Testing

This app uses a combination of JUnit 5 and TestFX for both logic and GUI testing.

### ✅ Run Tests

```bash
mvn test
```

### 📊 View Code Coverage

```bash
mvn jacoco:report
```

Open the coverage report:

```bash
target/site/jacoco/index.html
```

Includes GUI testing of angle button behavior, scene initialization, and game-mode interactions using TestFX. High coverage achieved by simulating button clicks for each angle.

---

## 🧠 Software Design

### Primary Classes

- `UnitCircleApp.java`: JavaFX Application main class
- `HighScoreManager.java`: Manages high score I/O and entry
- `ScoreHistoryManager.java`: Tracks and displays session history
- `SpecialTriangle.java`: Draws and rotates triangle diagrams based on angle/quadrant

### UML

![UnitCircleApp_UML](https://github.com/user-attachments/assets/689b9be7-2f71-425c-816a-1c50f7ded08c)

See `docs/uml.txt` for the source PlantUML code used to generate this diagram.

---

## 📚 Citations and References

1. JavaFX official site – https://openjfx.io  
2. Khan Academy – Unit Circle & Trigonometry – https://www.khanacademy.org/math/trigonometry  
3. Desmos Graphing Calculator – https://www.desmos.com  
4. JavaFX Timeline API – https://docs.oracle.com/javase/8/javafx/api/javafx/animation/Timeline.html  
5. TestFX Documentation – https://github.com/TestFX/TestFX  
6. JaCoCo Test Coverage Tool – https://www.jacoco.org/jacoco/  
7. Oracle JavaFX Sample Projects – https://docs.oracle.com/javase/8/javase-clienttechnologies.htm  
8. Stack Overflow: JavaFX layout tips – https://stackoverflow.com/questions/33016243  
9. JavaFX ComboBox API – https://openjfx.io/javadoc/17/javafx.controls/javafx/scene/control/ComboBox.html  
10. TestFX GitHub Examples – https://github.com/TestFX/TestFX/tree/master/tests  
11. PlantUML Reference Guide – https://plantuml.com/class-diagram  
12. Material Design UI concepts – https://m3.material.io/  
13. Effective Java (Joshua Bloch) – Chapter on Enums and Lambdas  
14. JavaFX Circle class – https://docs.oracle.com/javafx/2/api/javafx/scene/shape/Circle.html  
15. JavaFX Scene Graph overview – https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/Node.html
16. ChatGPT made my readme pretty because I 100% can't be bothered

---

## ❗ Challenges

- Handling correct quadrant overlays dynamically for different trig functions
- Implementing smooth and visually accurate triangle transformations
- Debugging GUI state for timed mode using TestFX headless test runners
- Ensuring consistent layout across resizes and DPI scaling

---

## 📌 Notes

- Unit tests are in `src/test/java/`
- UML source and image output are in `docs/`
- Run `mvn javafx:run` for GUI launch, or `mvn test` for CLI test runs
- Tested on IntelliJ IDEA 2024.3 with JavaFX and Maven plugins

---

© 2025 Wesley Weaver. For educational use.
