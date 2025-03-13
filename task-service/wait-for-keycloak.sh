#!/usr/bin/env bash
set -e

host="$1"
shift
cmd="$@"

echo "Waiting for Keycloak at $host..."

until curl -v --fail "$host" >/dev/null 2>&1; do
  echo "Keycloak not up yet..."
  sleep 5
done

echo "Keycloak is up! Starting task-service..."
exec $cmd
