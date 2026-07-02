#!/usr/bin/env bash
set -euo pipefail

# Runs Maven tests using the official Maven Docker image (no local Maven required)
# Usage: ./run-tests.sh

docker run --rm \
  -v "$PWD":/workspace \
  -w /workspace \
  maven:3.9.4-jdk-17 \
  mvn -B test
