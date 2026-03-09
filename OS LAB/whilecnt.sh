#!/bin/bash
# Script to demonstrate while loop

COUNT=1

echo "Counting up to 3 with while loop:"
while [ $COUNT -le 3 ]; do
  echo "Current count: $COUNT"
  ((COUNT++)) # Increment COUNT
done
