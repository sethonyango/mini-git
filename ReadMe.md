# MINI-GIT
This is a lightweight distributed source control system using Kotlin. The inspiration comes from the
existing GIT system.
The system allows you to manage version control locally with features like staging files, committing changes,
viewing commit logs, and branch management.
The tool is for educational, fun, trying out things purposes and only works for local repositories.

## Features

- Initialize a repository: Create a new repository in the current directory.

- Stage files: Add files to the staging area for the next commit.

- Commit changes: Save changes with a descriptive message and author information.

- View commit history: See a list of all commits.

- Manage branches: Create, list, and switch between branches.

- Ignore files: Specify patterns of files to exclude from version control.

## Getting Started

### Prerequisites
- Java 11 or higher
- Kotlin 1.8 or higher
- Gradle

### Installation

1. Clone this repository:
```shell
git clone git@github.com:sethonyango/mini-git.git
```
2. Navigate to the project directory:
```shell
cd mini-git
```
3. Build the project:
```shell
./gradlew build
```
4. The JAR file will be available in the build/libs directory.

5. Run the tool:
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar --help
```

### Usage

Here are some example commands to get started with mini-git:

Initialize a Repository

```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar init
```

Stage Files
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar add file1.txt file2.txt
```

Commit Changes
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar commit -a "Your Name" "Initial commit"
```

View Commit History
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar log
```

#### Manage Branches

Create a new branch:
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar branch --create new-feature
```

List all branches:
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar branch --list
```

Switch to a branch:
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar branch --switch new-feature
```
Merge Branches
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar merge source-branch target-branch
```

View Repository Status
```shell
java -jar build/libs/mini-git-1.0-SNAPSHOT.jar status
```

### Configuration

You can ignore files by creating a .miniignore file in the root of your repository. Add file patterns to this file, 
similar to how you would with Git:

```gitignore
*.log
*.tmp
```

Limitations

- No network support: Cloning is local-only.

- Conflict resolution is not implemented.

- Limited diff visualization.