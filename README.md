## Running the Application

Follow any one of the methods below to compile and run the project.

---

### Option 1: Using Batch Files (Windows)

```bash
cd onboarding-offboarding
compile.bat
run-main.bat
```

---

### Option 2: Manual Compilation & Execution

```bash
cd onboarding-offboarding

# Compile all Java files
javac -d bin -cp ".;hrms-database-1.0-SNAPSHOT.jar" @sources.txt

# Run Main class (with database)
java -cp "bin;hrms-database-1.0-SNAPSHOT.jar" Main

# OR Run Demo class (sample data, no database)
java -cp "bin;hrms-database-1.0-SNAPSHOT.jar" DemoMain
```

---

### Option 3: Using Maven (If Installed)

```bash
cd onboarding-offboarding

# Compile
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="Main"
```

---

### Notes

* Ensure Java is installed and added to your system PATH
* Ensure Maven is installed (for Option 3)
* Keep `hrms-database-1.0-SNAPSHOT.jar` in the project root
