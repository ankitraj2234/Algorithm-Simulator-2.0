# 🎉 Algorithm Simulator v2.0 - Release Notes

**Release Date**: January 2026  
**Developer**: Ankit Raj (ar443203@gmail.com)

---

## ✨ What's New in v2.0

### 🔐 Security Improvements

#### Secure Credential Management
Previously, email credentials were hardcoded in source code - a critical security vulnerability.

**Now**:
- Credentials load from **environment variables** (recommended)
- Fallback to **properties file** for local development
- `email.properties` excluded from version control via `.gitignore`
- Graceful handling when email service is not configured

```bash
# Configure via environment variables
EMAIL_SENDER=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_RECIPIENT=feedback@example.com
```

---

### 📊 Professional Logging System

#### SLF4J + Logback Integration
Replaced all `e.printStackTrace()` calls with professional logging.

| Feature | Details |
|---------|---------|
| **Framework** | SLF4J API 2.0.9 + Logback 1.4.14 |
| **Console** | Colored output with timestamps |
| **File Logs** | Daily rolling, 30-day retention |
| **Size Limit** | 100MB total cap |
| **Log Location** | `logs/algorithm-simulator.log` |

**Example Log Output**:
```
14:30:45.123 [JavaFX-Launcher] ERROR c.s.StackController - Could not open code examples
   at com.simulator.StackController.openCodesPopup(StackController.java:135)
```

---

### 📦 New Algorithm Modules

#### Queue Operations
Full FIFO data structure visualization:
- **Enqueue** - Add to rear with slide-in animation
- **Dequeue** - Remove from front with rotation effect
- **Front/Rear** - Peek operations with highlight
- **Clear** - Staggered removal animation
- Capacity tracking with color-coded progress bar

#### Hash Table
Key-value storage visualization:
- **Insert/Search/Delete** operations
- Hash function calculation display
- Collision handling (chaining) visualization
- Load factor monitoring

---

### 🎨 UI/UX Improvements

#### Jitter-Free Hover Effects
Fixed a critical hover loop bug on algorithm cards.

**Problem**: Cards would vibrate/jitter when hovering due to CSS `scale` transform causing mouse exit/enter loop.

**Solution**: Removed all transforms from hover state. Now using:
- Enhanced shadow with primary color tint
- Border color change
- Subtle background darkening

```css
/* Before (caused jitter) */
.category-card:hover {
    -fx-scale-x: 1.025;  /* PROBLEMATIC */
}

/* After (smooth) */
.category-card:hover {
    -fx-effect: dropshadow(...);
    -fx-border-color: -fx-primary;
    -fx-background-color: derive(-fx-surface, -3%);
}
```

---

### 🧹 Code Quality Enhancements

#### AlertHelper Utility
Centralized alert dialogs across all controllers:
```java
AlertHelper.showWarning("Title", "Message");
AlertHelper.showInfo("Title", "Message");
AlertHelper.showError("Title", "Message");
```

#### InputValidator Utility
Consistent input validation logic:
```java
InputValidator.isValidInteger(input, 1, 999);
InputValidator.isValidPosition(pos, listSize);
```

#### Cleanable Interface
Resource cleanup contract for controllers:
```java
public interface Cleanable {
    void cleanup();
}
```

#### NavigationService Updates
- Automatic cleanup on window close
- Support for `Cleanable` interface
- Improved window lifecycle management

---

### 🛠️ Technical Updates

| Component | Change |
|-----------|--------|
| **pom.xml** | Added SLF4J, Logback dependencies |
| **logback.xml** | NEW - Logging configuration |
| **email.properties.template** | NEW - Secure credential template |
| **.gitignore** | Added email.properties exclusion |

---

## 📁 Files Changed

### New Files
- `src/main/resources/logback.xml`
- `src/main/resources/config/email.properties.template`
- `src/main/java/com/simulator/InputValidator.java`
- `src/main/java/com/simulator/Cleanable.java`

### Modified Files
| File | Changes |
|------|---------|
| `EmailService.java` | Secure credential loading |
| `AnalysisController.java` | Fixed empty catch block |
| `MainController.java` | Simplified hover handlers |
| `StackController.java` | SLF4J logging, AlertHelper |
| `QueueController.java` | SLF4J logging, AlertHelper |
| `HashTableController.java` | SLF4J logging, AlertHelper |
| `LinkedListController.java` | SLF4J logging |
| `TooltipService.java` | SLF4J logging |
| `NavigationService.java` | Cleanable support |
| `main.css` | Jitter-free hover styles |
| `pom.xml` | Logging dependencies |
| `.gitignore` | Security exclusions |

---

## ⬆️ Upgrade Notes

### From v1.x to v2.0

1. **Email Configuration Required** (if using feedback feature)
   - Set environment variables OR create `email.properties`
   - See Configuration section in README

2. **Log Directory**
   - Application now creates `logs/` directory
   - Add to `.gitignore` if not already present

3. **No Breaking Changes**
   - All existing functionality preserved
   - UI behavior improved (no jitter)

---

## 🐛 Bug Fixes

| Issue | Status |
|-------|--------|
| Hover jitter on Performance Analysis card | ✅ Fixed |
| Empty catch block in AnalysisController | ✅ Fixed |
| Inconsistent card-to-card hover transitions | ✅ Fixed |
| Hardcoded credentials in source | ✅ Fixed |

---

**Full Changelog**: See [walkthrough.md](walkthrough.md) for detailed implementation notes.
