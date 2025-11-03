# SwagLabs E-Commerce Automation Framework

This project contains a comprehensive, Maven-based TestNG automation framework utilizing the Page Object Model (POM) and WebDriverManager for cross-platform execution.

### Prerequisites

To successfully run and/or view this project, the following must be installed:

1.  **Java Development Kit (JDK):** Version 17 or higher.
2.  **Apache Maven:** Installed and configured in your system environment path.
3.  **Internet Connection:** Required for Maven to download dependencies and for WebDriverManager to download the necessary browser driver.
4.  **IDE (Optional):** An Integrated Development Environment like Eclipse IDE or IntelliJ IDEA for viewing source code.

---

### Execution Instructions (Command Line)

The project is delivered as a clean source tree. All execution-specific files (like `/target` and `/test-output`) have been excluded.

To run the full test suite, follow these steps:

1.  **Navigate to the project root** (the folder containing `pom.xml`):

    ```bash
    # Example (replace 'SwagLabs' with your actual directory name)
    cd C:\path\to\SwagLabs
    ```


2.  **Execute and Build the Project (Recommended Single Command):**

    This single command performs the entire workflow: dependency download, compilation, and test execution based on `testng.xml`.

    ```bash 
    mvn clean install -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
    ```

    **This command performs the following actions sequentially:**
    * **Clean:** Removes any old build artifacts.
    * **Compile & Download:** Downloads all required dependencies, compiles the source code, and builds the project.
    * **Execute Tests:** Runs the test cases defined in `src/test/resources/testng.xml` using the Surefire plugin.

---

### Execution Output

Upon successful completion, the detailed Extent Reports and TestNG XML reports will be available in the newly generated `test-output` folder.

* **HTML Report Path:** `SwagLabs/test-output/reports/` (or similar path depending on your listener)
* **Screenshots Path:** `SwagLabs/test-output/screenshots/` (or similar path)

---

### IDE Setup (For Reviewing and Running Code)

To import the source code into an IDE:

1.  Open IDE.
2.  Import the project using the **Existing Maven Projects** option.
3.  If any dependencies appear missing, force a Maven update on the project to resolve the classpath (Right-click project -> Maven -> Update Project).

**To Execute Tests from IDE (After Setup):**

1.  Navigate to the `src/test/resources` folder in the Package Explorer.
2.  Right-click on **`testng.xml`**.
3.  Select **Run As** -> **TestNG Suite**.