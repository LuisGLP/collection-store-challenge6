# 🏪 Collectibles Store (Challenge 6)

---

## 📘 Project Description

This project implements a **RESTful API** built with **Java (Spark Framework)** and connected to a **PostgreSQL** database to manage a **Collectibles Store**.  

It allows users to perform full **CRUD operations** on **Users** 👥 and **Collectibles** 🧸, returning **JSON responses** and supporting **dynamic HTML pages** using Mustache templates.  
The system also includes **error handling**, **logging**, and **connection pooling** for optimized performance.

---

## 🧱 Project Structure

src/
 └── main/
     ├── java/
     │   └── org.challenge6.javaspark/
     │       ├── config/
     │       │   ├── DatabaseConfig.java
     │       │   └── LocalDateTimeAdapter.java
     │       ├── Controllers/
     │       │   ├── ItemController.java
     │       │   ├── OfferController.java
     │       │   ├── UserController.java
     │       │   └── ViewController.java
     │       ├── entity/
     │       │   ├── Item.java
     │       │   ├── Offer.java
     │       │   └── User.java
     │       ├── exceptions/
     |       |   ├── BadRequestException.java
     |       |   ├── ConflicException.java
     |       |   ├── CustomException.java
     |       |   ├── DatabaseException.java
     |       |   ├─  ExceptionHandler.java
     |       |   ├── NotFoundException.java
     │       │   └── ValidationException.java    
     │       ├── services/
     │       │   ├── ItemService.java
     │       │   ├── OfferService.java
     │       │   └── UserService.java
     │       ├── websocket/
     │       │   ├── AuctionWebSocketCreator.java
     │       │   └── AuctionWebSocketHandler.java    
     │       └── Main.java
     └── resources/
     └── pom.xml
     └── postman_collection.json
     └── README.md

---

## ⚙️ Main Dependencies

All dependencies are defined in `pom.xml`:

| Dependency | Purpose |
|-------------|----------|
| `com.sparkjava:spark-core` | Web framework for REST APIs |
| `com.sparkjava:spark-template-mustache` | Template engine for HTML rendering |
| `org.postgresql:postgresql` | PostgreSQL JDBC driver |
| `com.zaxxer:HikariCP` | High-performance connection pool |
| `com.google.code.gson:gson` | JSON serialization and deserialization |
| `org.slf4j` + `logback-classic` | Logging and monitoring |

---

## 🗄️ Database Setup

Run the following SQL commands in your PostgreSQL environment:

```sql
-- =====================================
-- Users table
-- =====================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================
--  Items table 
-- =====================================
CREATE TABLE items (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(200) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'closed'))
) ENGINE=InnoDB;

-- =====================================
-- Oferts table
-- =====================================
CREATE TABLE offers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
) ENGINE=InnoDB;
```

---
## 🚀 How to Run the Project

1. Clone the repository:
```
git clone https://github.com/LuisGLP/collection-store-challenge6.git
cd collectibles-store
```

2. Ensure Java 17+ and Maven are installed.

3. Install dependencies:

```
mvn clean install
```

4. Run the application:

```
mvn exec:java -Dexec.mainClass="org.challenge6.javaspark.Main"
```

5. Open your browser or Postman and navigate to:

```
👉 http://localhost:4567
```


---
## 🧠 API Endpoints

**👥 Users** ```(/api/users)```

| Method  | Route            | Description             |
| ------- | ---------------- | ----------------------- |
| GET     | `/api/users`     | Retrieve all users      |
| GET     | `/api/users/:id` | Retrieve a user by ID   |
| POST    | `/api/users`     | Create a new user       |
| PUT     | `/api/users/:id` | Update an existing user |
| DELETE  | `/api/users/:id` | Delete a user           |
| OPTIONS | `/api/users/:id` | Check if a user exists  |


---

## 🛍️ Collectibles (/api/collectibles)

| Method | Route                   | Description                                                          |
| ------ | ----------------------- | -------------------------------------------------------------------- |
| GET    | `/api/collectibles`     | Retrieve all collectibles                                            |
| GET    | `/api/collectibles/:id` | Retrieve a collectible by ID                                         |
| POST   | `/api/collectibles`     | Create a new collectible                                             |
| PUT    | `/api/collectibles/:id` | Update collectible information (price changes trigger notifications) |
| DELETE | `/api/collectibles/:id` | Delete a collectible                                                 |


---

## 🧪 Postman Testing

You can import the provided _postman_collection.json_ into Postman.

It includes predefined requests organized into sections:

- 👤 Users API → Full CRUD testing
- 🧸 Collectibles API → Full CRUD testing
- ⚠️ Error Handling → Validation for 400 and 404 errors
- 💻 Health & Info → HTML routes and service status checks

---

## 🧰 Technologies Used

| Technology        | Description                    |
| ----------------- | ------------------------------ |
|  **Java 17**     | Main programming language      |
|  **Spark Java**  | Lightweight web framework      |
|  **PostgreSQL** | Relational database            |
|  **HikariCP**   | Connection pooling             |
|  **Gson**       | JSON parsing                   |
|  **Maven**      | Build & dependency management  |
|  **Mustache**   | Template engine for HTML pages |
|  **Postman**    | API testing tool               |




---

# collection-store-challenge6
Project built-with Spark framework and mustache template
| **Method**  | **Endpoint**   | **Description**                          |
| ----------- | -------------- | ---------------------------------------- |
| **GET**     | /api/users     | Retrieve all users                       |
| **GET**     | /api/users/:id | Retrieve a user by ID                    |
| **POST**    | /api/users/:id | Add a user                               |
| **PUT**     | /api/users/:id | Edit a specific user                     |
| **OPTIONS** | /api/users/:id | Check if a user with the given ID exists |
| **DELETE**  | /api/users/:id | Delete a specific user                   |

| **ID**   | **User Story**                                                                                                                                 | **Priority** | **Acceptance Criteria**                                                                              |
| :------- | :--------------------------------------------------------------------------------------------------------------------------------------------- | :----------- | :--------------------------------------------------------------------------------------------------- |
| **US01** | As a **developer**, I want to **define project dependencies (Spark, Logback, Gson)** so that I can structure the base for service development. | High         | The project builds successfully with Maven using POM packaging and includes all dependencies.        |
| **US02** | As a **user**, I want to **manage my account (create, update, delete)** so that I can control my profile data.                                 | High         | CRUD endpoints for `/api/users` must function correctly, returning appropriate HTTP status codes.    |
| **US03** | As an **administrator**, I want to **handle exceptions gracefully** so that the system displays meaningful error messages.                     | Medium       | Exception handling module must catch and respond with structured JSON error responses.               |
| **US04** | As a **customer**, I want to **create and manage offers through a web form** so that I can list my items for sale.                             | High         | Web form must allow item creation, modification, and deletion integrated with the backend API.       |
| **US05** | As a **buyer**, I want to **view filtered and real-time updated items** so I can easily find the best offers.                                  | High         | Items can be filtered by category, price, and the system shows live price updates through WebSocket. |

| **ID**    | **Requirement Description**                                                                          | **Type**   | **Associated User Story** |
| :-------- | :--------------------------------------------------------------------------------------------------- | :--------- | :------------------------ |
| **REQ01** | Define Maven project structure with packaging type (POM/JAR) and dependencies: Spark, Logback, Gson. | Functional | US01                      |
| **REQ02** | Implement CRUD operations for `/api/users` (GET, POST, PUT, OPTIONS, DELETE).                        | Functional | US02                      |
| **REQ03** | Create a module for exception handling that returns consistent JSON responses.                       | Functional | US03                      |
| **REQ04** | Develop views and templates to display items and manage offers.                                      | Functional | US04                      |
| **REQ05** | Implement filters for items by price and category.                                                   | Functional | US05                      |
| **REQ06** | Add WebSocket functionality for real-time price updates on items.                                    | Functional | US05                      |
| **REQ07** | Web form must be able to send data to the backend API to create or edit offers.                      | Functional | US04                      |
