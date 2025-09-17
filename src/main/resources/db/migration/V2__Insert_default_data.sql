-- Insert default permissions
INSERT INTO permissions (name, description, resource, action, is_active) VALUES
('USER_CREATE', 'Create users', 'USER', 'CREATE', TRUE),
('USER_READ', 'View users', 'USER', 'READ', TRUE),
('USER_UPDATE', 'Update users', 'USER', 'UPDATE', TRUE),
('USER_DELETE', 'Delete users', 'USER', 'DELETE', TRUE),
('ROLE_CREATE', 'Create roles', 'ROLE', 'CREATE', TRUE),
('ROLE_READ', 'View roles', 'ROLE', 'READ', TRUE),
('ROLE_UPDATE', 'Update roles', 'ROLE', 'UPDATE', TRUE),
('ROLE_DELETE', 'Delete roles', 'ROLE', 'DELETE', TRUE),
('PERMISSION_CREATE', 'Create permissions', 'PERMISSION', 'CREATE', TRUE),
('PERMISSION_READ', 'View permissions', 'PERMISSION', 'READ', TRUE),
('PERMISSION_UPDATE', 'Update permissions', 'PERMISSION', 'UPDATE', TRUE),
('PERMISSION_DELETE', 'Delete permissions', 'PERMISSION', 'DELETE', TRUE),
('DEVICE_MANAGE', 'Manage user devices', 'DEVICE', 'MANAGE', TRUE),
('DEVICE_VIEW', 'View user devices', 'DEVICE', 'VIEW', TRUE),
('ACCOUNT_MANAGE', 'Manage user accounts', 'ACCOUNT', 'MANAGE', TRUE),
('ACCOUNT_VIEW', 'View user accounts', 'ACCOUNT', 'VIEW', TRUE),
('SECURITY_QUESTION_MANAGE', 'Manage security questions', 'SECURITY_QUESTION', 'MANAGE', TRUE),
('SECURITY_QUESTION_VIEW', 'View security questions', 'SECURITY_QUESTION', 'VIEW', TRUE);

-- Insert default roles
INSERT INTO roles (name, description, is_active) VALUES
('ADMIN', 'Administrator with full system access', TRUE),
('USER', 'Regular user with basic access', TRUE),
('MODERATOR', 'User with moderation capabilities', TRUE);

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- Assign basic permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER' 
AND p.name IN ('USER_READ', 'DEVICE_VIEW', 'ACCOUNT_VIEW', 'SECURITY_QUESTION_VIEW');

-- Assign moderation permissions to MODERATOR role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MODERATOR' 
AND p.name IN ('USER_READ', 'USER_UPDATE', 'DEVICE_MANAGE', 'DEVICE_VIEW');
