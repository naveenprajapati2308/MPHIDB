# MPHIDB Remote Runner Setup

This project still runs through TestNG XML files. The runner dashboard only gives browser buttons on top of those XML files.

## Module Buttons

- Run All Modules: `MPHIDB.xml`
- Run Land Module: `land.xml`
- Run Architect Module: `Emp_Arch.xml`

## Start On Local Or Remote Machine

From the project folder:

```bat
run-dashboard.bat
```

Or run directly with Maven:

```bat
mvn -DskipTests compile exec:java
```

Then open:

```text
http://localhost:9090
```

From another machine on the same network, open:

```text
http://REMOTE_MACHINE_IP:9090
```

## Required On Remote Machine

- Java installed
- Maven installed and available in PATH
- Chrome installed
- Remote machine must have access to the MPHIDB QA URLs
- Windows firewall must allow inbound access to port `9090` if opening from another system

## Useful Commands

Run a suite manually:

```bat
mvn test -DsuiteXmlFile=MPHIDB.xml
mvn test -DsuiteXmlFile=land.xml
mvn test -DsuiteXmlFile=Emp_Arch.xml
```

Start dashboard on another port:

```bat
mvn -DskipTests compile exec:java -Drunner.port=8085
```

If Maven is not in PATH, pass its full command:

```bat
mvn -DskipTests compile exec:java -Dmaven.command="C:\apache-maven\bin\mvn.cmd"
```

## Reports

- All/Land report: `reports/MasterReport2.html`
- Architect report: `reports/MasterReport.html`

When the dashboard runs tests, it passes `-DopenReport=false`, so the report manager will not try to open a desktop browser automatically. This is important for remote/headless environments.
