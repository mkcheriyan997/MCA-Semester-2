#!/bin/bash
# Script to demonstrate for loop (list iteration)

FRUITS=("Apple" "Banana" "Cherry" "Date")

echo "Listing fruits:"
for FRUIT in "${FRUITS[@]}"; do
  echo "$FRUIT"
done
