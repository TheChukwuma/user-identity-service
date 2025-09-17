# PostgreSQL Setup Guide

This guide will help you set up PostgreSQL database for the User Identity Service.

## Prerequisites

- Docker and Docker Compose installed
- Java 21+
- Maven 3.6+

## Quick Start with Docker

### 1. Start PostgreSQL Database

```bash
# Start PostgreSQL container
docker-compose up -d postgres

# For development environment
docker-compose up -d postgres-dev
```

### 2. Verify Database Connection

```bash
# Check if container is running
docker ps

# Connect to database
docker exec -it user-identity-postgres psql -U postgres -d user_identity_db

# List databases
\l
```

### 3. Run the Application

```bash
# For development
mvn spring-boot:run -Dspring.profiles.active=dev

# For production
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Manual PostgreSQL Setup

### 1. Install PostgreSQL

#### On macOS (using Homebrew):
```bash
brew install postgresql
brew services start postgresql
```

#### On Ubuntu/Debian:
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### On Windows:
Download and install from [PostgreSQL Official Website](https://www.postgresql.org/download/windows/)

### 2. Create Database and User

```bash
# Switch to postgres user
sudo -u postgres psql

# Create database
CREATE DATABASE user_identity_db;
CREATE DATABASE user_identity_db_dev;

# Create user (optional)
CREATE USER user_identity_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE user_identity_db TO user_identity_user;
GRANT ALL PRIVILEGES ON DATABASE user_identity_db_dev TO user_identity_user;

# Exit psql
\q
```

### 3. Update Application Configuration

Update `application.properties` or create environment-specific properties:

```properties
# For development
spring.datasource.url=jdbc:postgresql://localhost:5432/user_identity_db_dev
spring.datasource.username=postgres
spring.datasource.password=your_password

# For production
spring.datasource.url=jdbc:postgresql://localhost:5432/user_identity_db
spring.datasource.username=user_identity_user
spring.datasource.password=your_password
```

## Environment-Specific Configuration

### Development Environment

Use `application-dev.properties`:

```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Production Environment

Use `application-prod.properties` with environment variables:

```bash
export DB_HOST=your-db-host
export DB_PORT=5432
export DB_NAME=user_identity_db
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
export JWT_SECRET=your-secure-jwt-secret

mvn spring-boot:run -Dspring.profiles.active=prod
```

## Database Migration

Flyway will automatically run migrations on application startup:

1. **V1__Create_baseline_tables.sql** - Creates all database tables
2. **V2__Insert_default_data.sql** - Inserts default roles and permissions

### Manual Migration

```bash
# Check migration status
docker exec -it user-identity-postgres psql -U postgres -d user_identity_db -c "SELECT * FROM flyway_schema_history;"

# Force migration (if needed)
mvn flyway:migrate
```

## Database Schema

The application creates the following tables:

- `permissions` - System permissions
- `roles` - User roles
- `role_permissions` - Role-permission mapping
- `users` - User information
- `user_roles` - User-role mapping
- `addresses` - User addresses
- `devices` - User devices
- `accounts` - User accounts
- `security_questions` - Security questions

## Connection Pool Configuration

For production, consider adding connection pool settings:

```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

## SSL Configuration (Production)

For production environments with SSL:

```properties
spring.datasource.url=jdbc:postgresql://your-host:5432/user_identity_db?sslmode=require
spring.datasource.hikari.connection-test-query=SELECT 1
```

## Backup and Restore

### Backup

```bash
# Backup database
pg_dump -h localhost -U postgres -d user_identity_db > backup.sql

# Backup with Docker
docker exec user-identity-postgres pg_dump -U postgres user_identity_db > backup.sql
```

### Restore

```bash
# Restore database
psql -h localhost -U postgres -d user_identity_db < backup.sql

# Restore with Docker
docker exec -i user-identity-postgres psql -U postgres user_identity_db < backup.sql
```

## Monitoring and Maintenance

### Check Database Size

```sql
SELECT pg_size_pretty(pg_database_size('user_identity_db'));
```

### Check Table Sizes

```sql
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Performance Monitoring

```sql
-- Check active connections
SELECT count(*) FROM pg_stat_activity WHERE datname = 'user_identity_db';

-- Check slow queries
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Check if PostgreSQL is running
   - Verify port 5432 is accessible
   - Check firewall settings

2. **Authentication Failed**
   - Verify username and password
   - Check pg_hba.conf configuration
   - Ensure user has proper permissions

3. **Database Not Found**
   - Create the database manually
   - Check database name in connection URL
   - Verify user has CREATE DATABASE privilege

4. **Migration Failures**
   - Check Flyway schema history table
   - Verify migration scripts syntax
   - Check database permissions

### Useful Commands

```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# View PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-*.log

# Connect to database
psql -h localhost -U postgres -d user_identity_db

# List all databases
\l

# List all tables
\dt

# Describe table structure
\d table_name
```

## Security Best Practices

1. **Use Strong Passwords**
2. **Enable SSL in Production**
3. **Limit Database User Privileges**
4. **Regular Security Updates**
5. **Monitor Database Access**
6. **Backup Regularly**
7. **Use Connection Pooling**
8. **Implement Database Firewall**

## Performance Optimization

1. **Create Appropriate Indexes**
2. **Use Connection Pooling**
3. **Optimize Queries**
4. **Monitor Performance Metrics**
5. **Regular Database Maintenance**
6. **Archive Old Data**
7. **Use Read Replicas for Scaling**
