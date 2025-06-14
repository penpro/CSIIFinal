
# UnitCircleApp

An interactive JavaFX application for exploring the unit circle, trigonometric functions, and special right triangles. This educational tool supports exploration and game modes that help students visually and interactively learn sine, cosine, and tangent across quadrants.

## 📦 Project Overview

This project is a trigonometry learning tool with three interactive modes:

- **Exploration Mode**: View and click angles on the unit circle.
- **Unit Circle Game**: Timed mode to test your knowledge of degrees, radians, and trig functions.
- **Special Triangles Mode**: View dynamically oriented and labeled 30-60-90 and 45-45-90 triangles.

## 🧰 Features and Technologies

- **JavaFX GUI** with responsive layout
- Dynamic **unit circle rendering**
- Real-time **score tracking and game logic**
- Custom overlay system for **quadrant filtering**
- Weighted random selection for **target angles**
- Pop-up canvas displays for **triangle visualizations**
- **Lambdas**, **Maps**, **Sets**, **JavaFX Canvas**, and **Animation Timelines**
- Persistent **high score tracking** with name entry
- Testable logic and **JaCoCo code coverage support**

---

## 📁 Installation & Setup

> ⚠️ Assumes Java 17+ and Maven are already installed.

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

![Unit Circle Screenshot](images/unit_circle_overlay.png)
*Image: Overlays restrict quadrant view in sin(x) mode.*

### Special Triangle Display

![Triangle Pop-up](images/special_triangle_popup.png)
*Image: 30-60-90 triangle oriented to match QIII.*

---

## ⚙️ Configuration Options

- **Modes**: Exploration, Unit Circle, Special Triangles
- **Angle display**: Degrees, Radians, sin(x), cos(x), tan(x)
- **Timed mode**: 2-minute countdown for scoring

---

## 🧪 Testing

This project supports code coverage via [JaCoCo](https://www.jacoco.org/jacoco/):

- Run tests:
```bash
mvn test
```

- Generate coverage report:
```bash
mvn jacoco:report
```

- View report in: `target/site/jacoco/index.html`

---

## 🧠 Software Design

The project uses a modular structure:

- `UnitCircleApp.java`: Main class and GUI logic
- `SpecialTriangle.java`: Triangle rendering and transformation
- `HighScoreManager.java`: Persistent score tracking
- `ScoreHistoryManager.java`: Popup and logging of recent scores
- Overlay logic for quadrant disambiguation

Refer to [`docs/uml.txt`](docs/uml.txt) or the included PNG for class relationships.

---

## 📚 Citations and References

- JavaFX official docs: [https://openjfx.io](https://openjfx.io)
- Trig identities and angle references: Khan Academy, Desmos
- Fonts and style inspiration from open-source JavaFX demos

---

## ❗ Challenges

- Rotating triangles to match quadrant orientation while preserving proportions
- Resolving thread violations during animation (e.g., JavaFX `showAndWait`)
- Designing for both pedagogical clarity and gameplay functionality

---

## 📌 Notes

- See `src/test/` for JUnit 5 tests
- Sample UML files are located in `docs/`
- Compatible with IntelliJ IDEA with Maven support

---

© 2025 Wesley Weaver. For educational use.
