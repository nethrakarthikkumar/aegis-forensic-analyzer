# Aegis Forensic Analyzer

A JavaFX desktop application for **file integrity analysis**, **hash calculations**, and **threat indicator detection** across Linux filesystems.

---

## Features

- **File Integrity Checks** — Scans directories to identify hidden, duplicate, clean, or suspicious files.
- **Cryptographic Hashing** — Computes real-time SHA-256 hashes for target files.
- **Report Export** — Generates structured forensic audit reports.
- **Native Desktop Integration** — Packaged with an embedded JRE using OpenJDK's `jpackage`, so users don't need Java installed separately.

---

##  Option 1: Install the App (Easiest — No Coding Needed)

This is for people who just want to **run the app**, not build it from source.

**Works on:** Linux, Kali Linux, Debian (and other Debian-based distros)

### Steps:

1. Go to the [Releases page](https://github.com/nethrakarthikkumar/aegis-forensic-analyzer/releases) and download the latest `.deb` file (e.g. `forensic-analyzer_1.0_amd64.deb`).
2. Open a terminal in the folder where you downloaded the file.
3. Run this command to install it:
   ```bash
   sudo dpkg -i forensic-analyzer_1.0_amd64.deb
   ```
4. Done! Open your applications menu and search for **"Forensic Analyzer"** to launch it — just like any other app.

---

## Option 2: Build From Source (For Developers)

Use this if you want to modify the code or build the app yourself instead of using the pre-built installer.

### Prerequisites

Make sure you have these installed first:

| Requirement | Version | Check with |
|---|---|---|
| OpenJDK | 21 or later | `java -version` |
| Apache Maven | Any recent version | `mvn -version` |

### Step 1 — Clone the Repository

```bash
git clone https://github.com/nethrakarthikkumar/aegis-forensic-analyzer.git
cd aegis-forensic-analyzer
```

### Step 2 — Build the Application

```bash
mvn clean package -DskipTests
cp target/forensic-analyzer-1.0-SNAPSHOT.jar target/app/
```

### Step 3 — Generate a `.deb` Installer

```bash
jpackage \
  --type deb \
  --name "forensic-analyzer" \
  --input target/app/ \
  --main-jar forensic-analyzer-1.0-SNAPSHOT.jar \
  --main-class com.forensics.Launcher \
  --dest dist \
  --linux-shortcut \
  --linux-menu-group "Utility"
```

This creates a ready-to-install `.deb` package inside the `dist/` folder. Install it the same way as in Option 1.

---

## Tech Stack

- **Java 21**
- **JavaFX** — desktop GUI
- **MySQL** — data storage
- **Apache Commons CSV** — report/export handling
- **Maven** — build tool
- **jpackage** — native Linux packaging

---

## License

This project is licensed under the [MIT License](LICENSE).
