#!/bin/sh
set -e

if [ -n "$DATABASE_URL" ]; then
  # Parse Neon / PostgreSQL URI: postgres[ql]://user:pass@host[:port]/db[?params]
  clean_url=$(echo "$DATABASE_URL" | sed -E 's|^postgres(ql)?://||')
  creds="${clean_url%%@*}"
  host_and_db="${clean_url#*@}"

  # Strip libpq-only channel_binding param if present, and ensure sslmode=require for Neon
  host_and_db=$(echo "$host_and_db" | sed -E 's/([?&])channel_binding=[^&]*&?/\1/g; s/[?&]$//')
  case "$host_and_db" in
    *sslmode=*) ;;
    *\?*) host_and_db="${host_and_db}&sslmode=require" ;;
    *) host_and_db="${host_and_db}?sslmode=require" ;;
  esac

  export SPRING_DATASOURCE_USERNAME="${creds%%:*}"
  export SPRING_DATASOURCE_PASSWORD="${creds#*:}"
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${host_and_db}"
  export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
elif [ -n "$DB_HOST" ]; then
  export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
fi

exec java -Xmx384m -XX:+UseSerialGC -Dserver.port="${PORT:-8080}" -jar /app/app.jar
