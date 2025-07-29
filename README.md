
# 💻 Congestion Tax Calculator

A Spring Boot application that calculates daily, monthly, and yearly congestion tax for vehicles based on timestamped passages. <br>
Comes with a PostgreSQL database and Docker Compose setup for one‐command startup.

---

## 🛠️ Tech Stack

- **Backend:** Java • Spring Boot MVC • Spring Data JPA  
- **Database:** PostgreSQL  
- **Containerization:** Docker • Docker Compose  
- **Build:** Maven

---

## ⚙️ Features

- ✅ Calculate daily, monthly and yearly congestion tax
- ✅ Load tax calculation rules from configuration file
- ✅ Exempt vehicles & exempt dates (weekends, public holidays, ...)  
- ✅ Loads public holiday dates from CSV file.
- ✅ CRUD endpoints for `Vehicle` and `Passage`
- ✅ Load data from `data.sql` on every startup
- ✅ Fully containerized (app + DB)

---

## 🔧 Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21) (for local build)  
- [Docker & Docker Compose](https://docs.docker.com/get-started/get-docker/)
- [Git](https://github.com/git-guides/install-git)

---

## 🚀 Quick Start with Docker Compose

1. **Clone the repo**  
   ```bash
   git clone https://github.com/MihaelMajetic141/CongestionTax
   cd CongestionTax
   ```

2. **Build & run**

   ```bash
   docker-compose up --build
   ```

3. **Browse the API**

    * Service: `http://localhost:8080`
    * Health:  `http://localhost:8080/actuator/health`
   

4. **Reset & rebuild** (fresh DB)

   ```bash
   docker-compose down -v
   docker-compose up --build
   ```

---

## 🔍 API Reference

| Method | Endpoint                                                                  | Description                                                          |
| :----: |:--------------------------------------------------------------------------|:---------------------------------------------------------------------|
|  POST  | `/api/congestion/addVehicle`                                              | Save new vehicle (expects JSON object in request body)               |
|  POST  | `/api/congestion/addPassage`                                              | Save new passage (expects JSON object in request body)               |
|   GET  | `/api/congestion/calculateDailyTax?registration={r}&date={YYYY-MM-DD}`    | Get daily tax for registration `{r}` on `{date}`                     |
|   GET  | `/api/congestion/calculateMonthlyTax?registration={r}&month={m}&year={y}` | Get monthly tax for registration `{r}` on month `{m}` and year `{y}` |
|   GET  | `/api/congestion/calculateYearlyTax?registration={r}&year={y}`            | Get yearly tax for registration `{r}` on year `{y}`                  |
|   GET  | `/api/congestion/calculateDailyTaxForAll?date={YYYY-MM-DD}`               | Get daily tax on all vehicles for `{date}`                           |
|   GET  | `/api/congestion/calculateMonthlyTaxForAll?month={m}&year={y}`            | Get monthly tax for all vehicles on month `{m}` and year `{y}`       |
|   GET  | `/api/congestion/calculateYearlyTaxForAll?year={y}`                       | Get yearly tax for all vehicles on year `{y}`                        |

---

## ⚙️ Configuration

All settings can be found in `src/main/resources` directory and can be overridden via:
* Project properties `application.properties` 
* Tax calculation rules YAML `gothenburg2013.yml`
* Public holiday data `publicholiday.SE.2013.csv`

---

## 🔬 Testing

Run tests with:

```bash
./mvnw test
```

---