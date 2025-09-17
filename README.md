# User Identity Service

A comprehensive Spring Boot application for user authentication, authorization, and identity management using JWT tokens.

## Features

- **User Management**: Complete user registration and authentication
- **JWT Security**: Token-based authentication with refresh token support
- **Role-Based Access Control**: Flexible role and permission system
- **Device Management**: Track and manage user devices with primary device designation
- **Address Management**: User address information with multiple address types
- **Account Management**: Multiple account types per user
- **Security Questions**: Security question system for account recovery
- **Audit Trail**: Automatic tracking of creation and modification timestamps

## Architecture

### Base Model
All entities inherit from `BaseModel` which provides:
- `id`: Auto-generated primary key
- `createdAt`: Automatic timestamp on creation
- `updatedAt`: Automatic timestamp on updates

### Core Entities

#### User
- Implements Spring Security's `UserDetails`
- Supports multiple roles and permissions
- One-to-one relationship with Address
- One-to-many relationship with Devices and Accounts
- One-to-many relationship with SecurityQuestions

#### Role & Permission System
- Many-to-many relationship between Users and Roles
- Many-to-many relationship between Roles and Permissions
- Default roles: ADMIN, USER, MODERATOR
- Comprehensive permission system for fine-grained access control

#### Device Management
- Each user can have multiple devices
- One device can be marked as primary
- Device tracking with IP, MAC address, OS information

#### Address Management
- One address per user (one-to-one relationship)
- Support for different address types (HOME, WORK, BILLING, etc.)

#### Account System
- Multiple accounts per user
- Different account types (CHECKING, SAVINGS, BUSINESS, etc.)
- Account status tracking and verification

## Security Configuration

### JWT Implementation
- Access tokens with configurable expiration
- Refresh tokens for seamless authentication
- Secure token validation and parsing
- Automatic token refresh endpoint

### Spring Security
- Stateless JWT-based authentication
- Role-based authorization
- Method-level security annotations
- Global exception handling

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh
- `POST /api/auth/logout` - User logout

### Protected Endpoints
- `/api/admin/**` - Admin-only endpoints
- `/api/user/**` - User and admin endpoints

## Configuration

### Database
- H2 in-memory database for development
- PostgreSQL support for production
- JPA with Hibernate

### JWT Settings
```properties
jwt.secret=your-secret-key-here
jwt.expiration=86400  # 24 hours
jwt.refresh.expiration=604800  # 7 days
```

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run: `mvn spring-boot:run`
4. Access H2 console at: `http://localhost:8080/h2-console`

### Default Data
The application automatically creates:
- Default roles: ADMIN, USER, MODERATOR
- Comprehensive permission system
- Admin user with full access

### Sample API Usage

#### Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'
```

## Admin vs Regular Users

The system uses a role-based approach where:
- **ADMIN role**: Full system access with all permissions
- **USER role**: Basic access with limited permissions
- **MODERATOR role**: Enhanced access for content moderation

No separate Admin class is needed as any user can be assigned the ADMIN role, providing flexible user management.

## Database Schema

The application creates the following tables:
- `users` - User information
- `roles` - Available roles
- `permissions` - Available permissions
- `user_roles` - User-role mapping
- `role_permissions` - Role-permission mapping
- `addresses` - User addresses
- `devices` - User devices
- `accounts` - User accounts
- `security_questions` - Security questions

## Security Considerations

- Passwords are encrypted using BCrypt
- JWT tokens are signed with HMAC-SHA256
- All endpoints are protected by default
- Input validation on all API endpoints
- Comprehensive exception handling
- Audit logging for security events

## Development

### Testing
Run tests with: `mvn test`

### Building
Build the application with: `mvn clean package`

### Production Deployment
1. Update database configuration for your environment
2. Set secure JWT secret key
3. Configure proper logging levels
4. Set up SSL/TLS for HTTPS
5. Configure CORS if needed for frontend integration
