# Todo CLI

A simple, cross-platform Java command-line todo application with persistent storage.  
Supports flexible commands, task management, and automatic saving.

---

## Features
- Add tasks (`add` or `a`)
- Remove tasks by number (`remove` or `rm`)
- Mark tasks as done (`done` or `d`)
- List all tasks (`list` or `ls`)
- Wipe all tasks (`wipe` or `w`)
- Persistent storage via `tasks.txt`
- Flexible command parsing and error handling

---

## Installation
Clone the repository:
```bash
git clone https://github.com/digitalCanine/todo-cli.git
cd todo-cli/src
```

To launch, simply use the command ```java Main.java''' in the '''src''' folder.

---
## Note
It is reccomended that you assign an alias via your prefered shell for easier access to the todo list.

If you don't know how to add an alias, simply go into the config file of your prefered shell and add a line like this ```alias (your alias)="java (repo location)/src/Main.java"```


Feel free to also change the commands/shortcuts for your productivity!