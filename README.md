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
