# 🚀 Algorithm Simulator - Professional Edition v2.0

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-24-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-green.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Educational-purple.svg)]()

> **A comprehensive, professional-grade JavaFX desktop application for algorithm visualization, learning, and analysis.**

### 👨‍💻 Developer
**Ankit Raj**  
📧 [ar443203@gmail.com](mailto:ar443203@gmail.com)

---

![Algorithm Simulator Banner](docs/images/banner.png)

---

## 📋 Application Overview

**Algorithm Simulator Professional Edition v2.0** is an advanced educational tool that transforms abstract algorithmic concepts into interactive, visual experiences. The application combines theoretical knowledge with practical implementation through real-time visualizations, comprehensive code examples, and performance analysis tools.

### ✨ What's New in v2.0

| Feature | Description |
|---------|-------------|
| 🔐 **Secure Configuration** | Environment-based credential management |
| 📊 **Professional Logging** | SLF4J + Logback with rolling file logs |
| 🎨 **Jitter-Free UI** | Smooth hover effects without animation loops |
| 🧹 **Code Quality** | AlertHelper, InputValidator, Cleanable interfaces |
| 📦 **New Modules** | Queue Operations & Hash Table visualizations |

---

## 🏗️ Core Architecture

### Modern JavaFX Design
- **Professional UI**: Clean, modern interface with AtlantaFX theme integration
- **Responsive Layout**: Adaptive design that works across different screen sizes
- **Multi-Window Support**: Independent module windows with proper lifecycle management
- **Advanced Theme System**: Dynamic light/dark theme switching with live updates
- **CSS-Powered Styling**: Professional styling with custom CSS variables

### Enterprise-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Algorithm Simulator                       │
├─────────────────────────────────────────────────────────────┤
│  Presentation Layer                                          │
│  ├── Controllers (FXML-bound)                               │
│  ├── Views (FXML + CSS)                                     │
│  └── Animations (AnimationService)                          │
├─────────────────────────────────────────────────────────────┤
│  Service Layer                                               │
│  ├── NavigationService (Window Management)                  │
│  ├── ThemeManager (Theme Switching)                         │
│  ├── TooltipService (Rich Tooltips)                         │
│  ├── AlertHelper (Unified Dialogs)                          │
│  └── PreferencesManager (Settings Persistence)              │
├─────────────────────────────────────────────────────────────┤
│  Data Layer                                                  │
│  ├── Models (Stack, Queue, LinkedList, HashTable, Graph)    │
│  ├── Repositories (Code Examples)                           │
│  └── Configuration (JSON, Properties)                       │
├─────────────────────────────────────────────────────────────┤
│  Infrastructure                                              │
│  ├── SLF4J + Logback (Logging)                              │
│  ├── InputValidator (Validation)                            │
│  └── Cleanable Interface (Resource Management)              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📚 Algorithm Modules

### 1. 📚 Stack Data Structure
**Interactive Stack Visualization with Professional Animations**

- **Push/Pop/Peek/Clear** operations with smooth animations
- **Capacity Management** with visual progress bar
- **Smart Color Coding**: Red (near full), Orange (getting full), Green (normal)
- **Complete Code Examples** with syntax highlighting

### 2. 🔗 LinkedList Data Structure
**Complete LinkedList Implementation with Bidirectional Operations**

- **Insert**: Beginning, End, Position-based
- **Delete**: By value or by position
- **Search**: Sequential search with visual progression
- **Node Visualization**: Circular nodes with arrow connections

### 3. 📊 Queue Operations *(New in v2.0)*
**FIFO Data Structure with Professional Animations**

- **Enqueue/Dequeue/Front/Rear** operations
- **Horizontal visualization** showing queue flow
- **Capacity tracking** with color-coded progress
- **Real-time size and element tracking**

### 4. #️⃣ Hash Table *(New in v2.0)*
**Key-Value Storage with Collision Handling Visualization**

- **Insert/Search/Delete** operations
- **Hash function visualization**
- **Collision handling** (chaining) with visual representation
- **Load factor monitoring**

### 5. 📈 Sorting Algorithms
**Comprehensive Sorting Algorithm Visualization (9 Algorithms)**

| Basic | Advanced | Specialized |
|-------|----------|-------------|
| Bubble Sort | Merge Sort | Shell Sort |
| Selection Sort | Quick Sort | Radix Sort |
| Insertion Sort | Heap Sort | Counting Sort |

- **Real-time Bar Chart** visualization
- **Step Control**: Play, pause, step-by-step, speed control
- **Statistics**: Comparisons, swaps, execution time

### 6. 🔍 Searching Algorithms
**Interactive Search Algorithm Demonstrations**

- **Linear Search**: Sequential element checking
- **Binary Search**: Divide-and-conquer with range visualization
- **Interpolation Search**: Smart position estimation

### 7. 🕸️ Graph Algorithms
**Advanced Graph Theory Visualization**

- **Traversals**: BFS, DFS
- **Shortest Path**: Dijkstra's, A* (basic)
- **Topological Sort**
- **Interactive Canvas**: Drag-and-drop positioning

### 8. 📉 Performance Analysis
**Algorithm Complexity Comparison**

- **Time Complexity Curves**: O(1), O(log n), O(n), O(n log n), O(n²)
- **Side-by-side comparison** of algorithms
- **Recommendations** based on use cases

---

## 🔧 Technical Specifications

### Requirements
| Component | Version |
|-----------|---------|
| Java | 21+ |
| JavaFX | 24.0.2 |
| Maven | 3.8+ |

### Dependencies
```xml
<!-- Core -->
<dependency>javafx-controls, javafx-fxml</dependency>
<dependency>atlantafx-base (Theme Engine)</dependency>
<dependency>ikonli-javafx (Icons)</dependency>
<dependency>controlsfx (Enhanced Controls)</dependency>

<!-- Logging -->
<dependency>slf4j-api:2.0.9</dependency>
<dependency>logback-classic:1.4.14</dependency>

<!-- Email (Optional) -->
<dependency>jakarta.mail:2.0.1</dependency>
```

### Build & Run
```bash
# Clone and build
git clone https://github.com/yourusername/algorithm-simulator.git
cd algorithm-simulator
mvn clean install

# Run the application
mvn javafx:run
```

---

## 📊 Logging System

Professional logging with SLF4J + Logback:

```
logs/
├── algorithm-simulator.log        # Current log
├── algorithm-simulator.2026-01-03.log  # Historical
└── algorithm-simulator.2026-01-02.log
```

**Features**:
- Colored console output
- Daily rolling file logs (30-day retention)
- 100MB total size cap
- DEBUG level for com.simulator package

---

## ⚙️ Configuration

### Email Service (Optional)
For feedback functionality, configure via environment variables:
```bash
# Windows PowerShell
$env:EMAIL_SENDER = "your-email@gmail.com"
$env:EMAIL_PASSWORD = "your-app-password"
$env:EMAIL_RECIPIENT = "feedback@example.com"

# Linux/Mac
export EMAIL_SENDER="your-email@gmail.com"
export EMAIL_PASSWORD="your-app-password"
export EMAIL_RECIPIENT="feedback@example.com"
```

Or create `src/main/resources/config/email.properties`:
```properties
email.sender=your-email@gmail.com
email.password=your-app-password
email.recipient=feedback@example.com
```

> ⚠️ **Security Note**: Never commit credentials to version control. The `email.properties` file is excluded in `.gitignore`.

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+W` | Close current window |
| `Ctrl+T` | Toggle theme |
| `Ctrl+M` | Maximize/minimize |
| `F11` | Fullscreen mode |
| `Esc` | Close current window |
| `Ctrl+Q` | Quit application |

---

## 🎨 Theme System

- **PrimerLight** - Clean, GitHub-inspired light theme
- **PrimerDark** - Modern dark theme
- **Nord** - Arctic-inspired color palette
- **Cupertino** - Apple-inspired design

Themes apply live across all open windows.

---

## 🎯 Target Audience

- 🎓 **Computer Science Students** (Undergraduate/Graduate)
- 👨‍🏫 **CS Educators and Professors**
- 💼 **Technical Interview Candidates**
- 👨‍💻 **Professional Developers** (Reference/Review)
- 🏆 **Coding Competition Participants**

---

## 📁 Project Structure

```
algorithm-simulator/
├── src/main/java/com/simulator/
│   ├── AlgorithmSimulatorApplication.java  # Entry point
│   ├── MainController.java                 # Main view controller
│   ├── *Controller.java                    # Module controllers
│   ├── *Service.java                       # Service layer
│   ├── *Model.java                         # Data models
│   ├── *Repository.java                    # Code repositories
│   └── AlertHelper.java                    # Unified dialogs
├── src/main/resources/
│   ├── fxml/                               # View definitions
│   ├── css/                                # Stylesheets
│   ├── config/                             # Configuration files
│   └── logback.xml                         # Logging config
├── logs/                                   # Application logs
└── pom.xml                                 # Maven configuration
```

---

## 🌟 Key Features Summary

| Category | Features |
|----------|----------|
| **Algorithms** | 6 domains, 20+ specific algorithms |
| **Visualizations** | Real-time animations, step control |
| **Code Examples** | Complete implementations with syntax highlighting |
| **UI/UX** | Professional themes, smooth animations, responsive |
| **Architecture** | Enterprise patterns, modular design |
| **Quality** | SLF4J logging, input validation, error handling |
| **Security** | No hardcoded credentials, environment-based config |

---

## 📄 License

This project is for educational purposes. See [LICENSE](LICENSE) for details.

---

## 🤝 Contributing

Contributions welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

<p align="center">
  <strong>Algorithm Simulator Professional Edition v2.0</strong><br>
  <em>Bridging the gap between theory and understanding</em>
</p>
