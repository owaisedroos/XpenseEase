# XpenseEase 💸  
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blueviolet)
![Android](https://img.shields.io/badge/Android-App-green)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange)
![Room](https://img.shields.io/badge/Database-Room-red)

A Modern Android Expense Tracker Application

XpenseEase is a feature-rich Android expense tracking application designed to simplify personal finance management. It enables users to record daily expenses, categorize transactions, visualize spending patterns, and manage shared expenses through a clean and intuitive interface.

Built using modern Android development practices, XpenseEase focuses on usability, performance, and maintainability.

---

##  Problem Statement
Managing personal finances is challenging due to:
- Lack of a systematic way to track expenses and income
- Difficulty analyzing spending patterns
- Manual and error-prone tracking methods
- Complex and cluttered existing expense tracker apps
- Existing System do not have categories catering to Students

XpenseEase addresses these issues by providing a simple, visual, and user-friendly solution for everyday expense management.

---

##  Features
- Expense & Income Tracking  
- Expense Categorization  
- Interactive Pie Chart Visualizations  
- Expense Summary Dashboard  
- Split Expense Management (Splitwise-style)
- Event Management Tab
- Secure User Authentication (PIN / Password)  
- Data Export & Backup  
- Clean and Intuitive UI  

---

##  Tech Stack
- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** MVVM  
- **Database:** Room (SQLite)  
- **Dependency Injection:** Dagger Hilt  
- **IDE:** Android Studio Hedgehog  

---

##  Architecture
The application follows the **MVVM (Model–View–ViewModel)** architecture:
- **UI Layer:** Jetpack Compose screens  
- **ViewModel Layer:** Business logic & state management  
- **Data Layer:** Room database & repositories  

This ensures scalability, testability, and clean separation of concerns.

---

##  Screenshots

> Click on any section below to view screenshots.

---

<details>
<summary><strong>🏠 Home & Dashboard</strong></summary>

<br>

| Home Dashboard |
|---------------|
| <img src="screenshots/home.jpeg" width="300"/> |
| **Home Screen** – Displays total balance, income, expenses, and recent transactions |

</details>

---

<details>
<summary><strong>➕ Add Expense</strong></summary>

<br>

| Add Expense Screen |
|-------------------|
| <img src="screenshots/addexpense.jpeg" width="300"/> |
| **Add Expense** – Add category, amount, and date using a clean input form |

</details>

---

<details>
<summary><strong>📊 Expense Visualization (Pie Chart)</strong></summary>

<br>

| Expense Distribution |
|----------------------|
| <img src="screenshots/pie.jpeg" width="300"/> |
| **Expense Visualization** – Category-wise expense breakdown using a donut chart |

</details>

---

<details>
<summary><strong>📈 Statistics & Top Spending</strong></summary>

<br>

| Statistics & Trends |
|---------------------|
| <img src="screenshots/stats.jpeg" width="300"/> |
| **Statistics Screen** – Expense trends over time with top spending categories |

</details>

---

<details>
<summary><strong>🤝 Splitwise (Shared Expenses)</strong></summary>

<br>

| Shared Expenses |
|----------------|
| <img src="screenshots/splitwise.jpeg" width="300"/> |
| **Splitwise Feature** – Track shared expenses and calculate per-person splits |

</details>

---

<details>
<summary><strong>📅 Event Management</strong></summary>

<br>

| Event Expenses |
|---------------|
| <img src="screenshots/event.jpeg" width="300"/> |
| **Event Manager** – Track event-wise expenses with status and breakdown |

</details>

---


##  Future Enhancements
- Firebase cloud sync  
- AI-based expense insights  
- Bill reminders & notifications  
- Smart budget recommendations  

---


