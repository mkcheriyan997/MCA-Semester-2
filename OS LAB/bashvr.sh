#!/bin/bash
# Script to demonstrate bash variables

# Declaring and assigning variables
NAME="John Doe"
AGE=30
CITY="New York"

# Accessing variables
echo "Name: $NAME"
echo "Age: $AGE"
echo "City: $CITY"

# Reassigning a variable
NAME="Jane Smith"
echo "New Name: $NAME"

# Using command substitution
CURRENT_DIR=$(pwd)
echo "Current Directory: $CURRENT_DIR"
