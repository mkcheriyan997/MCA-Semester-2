## Shell Scripting Lab Record Notes

### Bash Syntax

The basic syntax of a Bash script is a series of commands. Each command is on a new line.

```bash
#!/bin/bash
# This is a comment
echo "Hello, World!"
```

- `#!/bin/bash`: This is called a shebang. It specifies the interpreter to be used to execute the script.
- `#`: Lines starting with `#` are comments.

### Bash Variables

Variables are used to store data. In Bash, you can define a variable by simply assigning a value to it.

```bash
#!/bin/bash

# A simple variable
GREETING="Hello, World!"

# Using the variable
echo $GREETING

# Reading input into a variable
echo "What is your name?"
read NAME
echo "Hello, $NAME"
```

- To get the value of a variable, you use the `$` prefix.
- It's a good practice to enclose variable names in double quotes to prevent word splitting and globbing issues.

### Control Constructs

#### `if` statements

The `if` statement allows for conditional execution of commands.

```bash
#!/bin/bash

read -p "Enter a number: " NUM

if [ $NUM -gt 10 ]; then
  echo "The number is greater than 10."
elif [ $NUM -eq 10 ]; then
  echo "The number is equal to 10."
else
  echo "The number is less than 10."
fi
```

- `[ ]` is an alias for the `test` command.
- `-gt` is for greater than, `-eq` is for equal to, and `-lt` is for less than.

#### `for` loops

The `for` loop is used to iterate over a list of items.

```bash
#!/bin/bash

# Looping over a list of strings
for fruit in "apple" "banana" "cherry"; do
  echo "I like $fruit"
done

# C-style for loop
for (( i=0; i<5; i++ )); do
  echo "Number: $i"
done
```

#### `while` loops

The `while` loop executes as long as a condition is true.

```bash
#!/bin'bash

COUNT=0
while [ $COUNT -lt 5 ]; do
  echo "Count: $COUNT"
  let COUNT=COUNT+1
done
```