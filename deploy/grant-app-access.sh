#!/bin/sh
# Run manually after importing the catalog. Grants read-only catalog access.
set -eu
: "${APP_DB_PASSWORD:?APP_DB_PASSWORD is required}"
psql -U postgres -d food_to_eat -v ON_ERROR_STOP=1 \
    --set=app_password="$APP_DB_PASSWORD" <<'SQL'
SELECT 'CREATE ROLE food_to_eat_app LOGIN'
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'food_to_eat_app')
\gexec
ALTER ROLE food_to_eat_app WITH PASSWORD :'app_password';
GRANT CONNECT ON DATABASE food_to_eat TO food_to_eat_app;
GRANT USAGE ON SCHEMA public TO food_to_eat_app;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO food_to_eat_app;
ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public
    GRANT SELECT ON TABLES TO food_to_eat_app;
SQL
