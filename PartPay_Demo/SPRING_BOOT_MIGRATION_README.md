# PartPay Spring Boot Migration Guide

## Project Structure

```
PartPay_Demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── partpay/
│   │   │           ├── PartPayApplication.java
│   │   │           ├── config/
│   │   │           │   ├── DatabaseConfig.java
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   └── SQLiteDialectConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── UserController.java
│   │   │           │   ├── ScheduleController.java
│   │   │           │   ├── TimesheetController.java
│   │   │           │   ├── RequestsController.java
│   │   │           │   ├── TaxController.java
│   │   │           │   └── PayslipController.java
│   │   │           ├── model/
│   │   │           │   └── entity/
│   │   │           │       ├── User.java
│   │   │           │       ├── Organization.java
│   │   │           │       ├── UserOrganization.java
│   │   │           │       ├── PartTimeEmployee.java
│   │   │           │       ├── EmployeeSchedule.java
│   │   │           │       ├── Timesheet.java
│   │   │           │       ├── OvertimeRequest.java
│   │   │           │       ├── SwapRequest.java
│   │   │           │       ├── LeaveRequest.java
│   │   │           │       ├── TaxType.java
│   │   │           │       ├── TaxInformation.java
│   │   │           │       ├── TaxTaxType.java
│   │   │           │       └── Payslip.java
│   │   │           ├── repository/
│   │   │           │   └── [All Repository Interfaces]
│   │   │           ├── security/
│   │   │           │   └── JwtAuthenticationFilter.java
│   │   │           └── service/
│   │   │               ├── JwtService.java
│   │   │               ├── AuthService.java
│   │   │               ├── UserService.java
│   │   │               ├── ScheduleService.java
│   │   │               ├── TimesheetService.java
│   │   │               ├── SwapLeaveOvertimeService.java
│   │   │               ├── TaxService.java
│   │   │               ├── PayslipService.java
│   │   │               └── OrgDatabaseService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application.yml (alternative)
│   └── test/
│       └── java/
├── serverFiles/
│   └── database/
│       └── Group4_PartPay.sqlite
├── pom.xml
└── build.gradle (alternative)
```

## Prerequisites

- **Java 17 or higher**
- **Maven 3.8+** or **Gradle 8.0+**
- **SQLite** (already handled by JDBC driver)

## Installation & Setup

### Option 1: Using Maven

1. **Install dependencies:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

### Option 2: Using Gradle

1. **Install dependencies:**
   ```bash
   ./gradlew build
   ```

2. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

### Option 3: Using IDE

1. Import the project as a Maven/Gradle project
2. Run `PartPayApplication.java` as a Java Application

## Database Configuration

The application uses SQLite with two types of databases:

1. **Main Database**: `./serverFiles/database/Group4_PartPay.sqlite`
   - Stores users, organizations, and user-organization relationships

2. **Organization Databases**: `./serverFiles/database/{orgId}_{orgName}.sqlite`
   - Dynamically created for each organization
   - Stores organization-specific data (employees, schedules, payroll, etc.)

### Database Auto-Creation

- Main database tables are auto-created by Hibernate (JPA)
- Organization-specific databases are created programmatically during signup

## Configuration Files

### application.properties
Located at `src/main/resources/application.properties`

Key configurations:
- Server port: `3000`
- Database URL: `jdbc:sqlite:./serverFiles/database/Group4_PartPay.sqlite`
- JWT secret and expiration
- Logging levels

### application.yml (Alternative)
More readable YAML format with the same configurations

## API Endpoints

### Authentication
- `POST /signup` - Register new organization and admin
- `POST /login` - User login

### User Management
- `POST /adduser/new` - Add new user to organization
- `POST /adduser/existing` - Add existing user to organization
- `GET /profile` - Get current user profile
- `PUT /profile` - Update user profile
- `GET /employees` - Get all employees in organization
- `GET /profile/employees` - Get employee list

### Schedule Management
- `POST /schedule/new` - Create new schedule
- `GET /schedule` - Get all schedules
- `GET /schedule/{id}` - Get schedule by ID
- `PUT /schedule/{id}` - Update schedule
- `DELETE /schedule/{id}` - Delete schedule

### Timesheet Management
- `POST /timesheet/new` - Create timesheet entry
- `GET /timesheet` - Get all timesheets
- `GET /timesheet/{id}` - Get timesheet by ID
- `PUT /timesheet/{id}` - Update timesheet
- `DELETE /timesheet/{id}` - Delete timesheet

### Request Management
- **Swap Requests**: `/swap/*`
- **Leave Requests**: `/leave/*`
- **Overtime Requests**: `/overtime/*`

### Tax Management
- **Tax Types**: `/taxtypes/*`
- **Tax Information**: `/tax/*`

### Payroll
- `POST /payslips/generate` - Generate payslips
- `GET /payslips` - Get all payslips
- `GET /payslips/{employee_id}` - Get payslips by employee

## Authentication

All endpoints except `/signup` and `/login` require JWT authentication.

**Header Required:**
```
jwt-access-token: <your-jwt-token>
```

**JWT Token Contains:**
- user_id
- org_id
- org_name
- role
- employee_id (if role is ptemployee)

## Key Changes from Node.js

### 1. **Dependencies**
- Express → Spring Boot Web
- bcrypt → BCryptPasswordEncoder (Spring Security)
- jsonwebtoken → io.jsonwebtoken (JJWT)
- sqlite3 → xerial sqlite-jdbc + Hibernate

### 2. **Architecture**
- **Node.js**: Routes → Controllers
- **Spring Boot**: Controllers → Services → Repositories

### 3. **Database Access**
- **Node.js**: Direct SQL queries with sqlite3
- **Spring Boot**: JPA/Hibernate with entity mapping

### 4. **Authentication**
- **Node.js**: Custom middleware function
- **Spring Boot**: JwtAuthenticationFilter + Spring Security

### 5. **Dependency Injection**
- **Node.js**: Manual require()
- **Spring Boot**: @Autowired / Constructor injection

### 6. **Error Handling**
- **Node.js**: try-catch with res.status()
- **Spring Boot**: try-catch with ResponseEntity<?>

## Running Tests

```bash
# Maven
mvn test

# Gradle
./gradlew test
```

## Building for Production

### Maven
```bash
mvn clean package
java -jar target/partpay-api-1.0.0.jar
```

### Gradle
```bash
./gradlew clean build
java -jar build/libs/partpay-api-1.0.0.jar
```

## Troubleshooting

### Issue: Database locked
**Solution**: Ensure only one instance of the application is running

### Issue: JWT authentication fails
**Solution**: Check if `jwt.secret` matches between token generation and validation

### Issue: CORS errors
**Solution**: Update allowed origins in `SecurityConfig.java`

### Issue: SQLite foreign keys not enforced
**Solution**: Foreign keys are enabled via PRAGMA in database configuration

## Development Tips

1. **Hot Reload**: Use Spring Boot DevTools for automatic restarts
2. **Debugging**: Set logging level to DEBUG in application.properties
3. **Database Inspection**: Use SQLite DB Browser to view database contents
4. **API Testing**: Use Postman or curl for endpoint testing

## Migration Checklist

- [x] Entity models created
- [x] Repository interfaces implemented
- [x] Service layer business logic migrated
- [x] Controllers (REST APIs) implemented
- [x] JWT authentication configured
- [x] Database configuration completed
- [x] CORS configuration added
- [x] Error handling implemented
- [x] Organization database creation logic
- [x] Password encryption configured

## Next Steps

1. Test all API endpoints
2. Update frontend to point to Spring Boot server (port 3000)
3. Run integration tests
4. Deploy to production server

## Support

For issues or questions, refer to:
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Hibernate ORM: https://hibernate.org/orm/documentation/