-- Initialize databases for user identity service
-- This script runs when the PostgreSQL container starts for the first time

-- Create the main database if it doesn't exist
SELECT 'CREATE DATABASE octopus_user_identity_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'octopus_user_identity_db')\gexec

-- Create the development database if it doesn't exist
SELECT 'CREATE DATABASE octopus_user_identity_db_dev'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'octopus_user_identity_db_dev')\gexec

-- Create extensions in both databases
\c octopus_user_identity_db;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

\c octopus_user_identity_db_dev;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE octopus_user_identity_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE octopus_user_identity_db_dev TO postgres;
