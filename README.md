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
