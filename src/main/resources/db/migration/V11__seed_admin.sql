INSERT INTO users (id, email, password_hash, display_name, role, is_active, is_deleted, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin@gmail.com',
    '$2a$10$lQeTPjKbhc/3yfsfx0zlH.tZxmDcli.Kn.ZoRLXUsdrFvMnq8pZKS',
    'Admin',
    'ADMIN',
    true,
    false,
    now(),
    now()
)
ON CONFLICT (email) DO NOTHING;
