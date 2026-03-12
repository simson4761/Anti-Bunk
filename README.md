# Anti-Bunk 📍

**Anti-Bunk** is a high-integrity attendance enforcement solution designed to solve the chronic issue of "proxy" attendance and students leaving campus after morning roll call. Unlike traditional systems that only record entry, Anti-Bunk maintains a persistent virtual watch on the student's presence throughout the academic day.

---

## 🛡️ The Anti-Bunk Enforcement Logic

Colleges today face a major challenge: students register their attendance and immediately slip out of the gates. Anti-Bunk is specifically engineered to combat this:

* **Continuous Geofence Monitoring:** The app uses the Google **Fence API** to create a persistent "Stay" condition.
* **Automatic Revocation:** If a student exits the defined coordinates of the college campus before the scheduled time, the app triggers an immediate `EXIT` event.
* **Instant Backend Sync:** The exit event communicates with **Firebase**, which automatically flags the student as "Bunked" and revokes the day's attendance credits.
* **Geofence Dwell Logic:** Requires the student to stay within the perimeter for a specific duration to count as "Present," preventing students from simply driving past the gate to trigger attendance.

---

## 🛠 Tech Stack

* **Language:** [Kotlin](https://kotlinlang.org/) - For robust, modern Android development.
* **IDE:** [Android Studio](https://developer.android.com/studio).
* **Real-time Infrastructure:** [Firebase](https://firebase.google.com/) - Handles the instant revocation of attendance.
* **Location Intelligence:** Google [Fence API](https://developers.google.com/location-context/fence-api) - Provides high-accuracy perimeter monitoring with low battery drain.

---

## 📍 Manual Perimeter Configuration

To ensure high precision, the college geofence is not automated; it is defined through manual calibration:
* **Manual Mapping:** Administrators must manually extract the exact **Latitude** and **Longitude** coordinates from mapping services (like Google Maps) for the college's boundaries.
* **Custom Radius:** The perimeter is manually set and updated in the code/backend to match the specific physical layout of the campus.
* **Static Bounds:** This manual approach ensures that only the official college grounds are included, preventing "false presence" from nearby public areas.

---

## ⚠️ Critical Limitations & Drawbacks

While the system is highly effective, it relies entirely on the integrity of the hardware state and the assumption of "One Student, One Device." The following scenarios represent "Dark Zones" where the app cannot effectively enforce attendance:

### 1. The "Single Device" Assumption
The system assumes a 1:1 ratio between the student and their smartphone. 
* **Multiple Devices:** A student could potentially leave a "logged-in" device inside the college perimeter (e.g., in a locker or with a friend) while they physically exit the campus with a second device.
* **Device Sharing:** The app cannot verify if the person carrying the phone is actually the student registered to that account.

### 2. Digital Divide (No Smartphone)
The system is inherently exclusionary toward students who do not own a compatible Android smartphone or those whose devices do not support the required Google Play Services for the Fence API.

### 3. GPS & Hardware Deactivation
* **GPS Manipulation:** If a student manually disables GPS/Location services or uses "Mock Location" apps, the Fence API cannot verify their position. 
* **Device Power-Off:** If the phone is switched off, all background monitoring ceases. No `EXIT` or `STAY` events can be recorded or transmitted.

### 4. Connectivity Issues
* **Data Dead-Zones:** A lack of active internet (Wi-Fi or Mobile Data) prevents the app from syncing "Bunk" alerts to the Firebase server in real-time. The event may only sync once the student regains connectivity, potentially after the bunking period has ended.

---

> **💡 Note to Administrators:** > To maximize effectiveness, it is recommended that college policy treats **"Location Signal Loss"** or **"Device Inactivity"** during mandatory college hours as a "Bunk" event by default. Furthermore, manual spot-checks are encouraged to ensure students are physically in possession of the device registered to their ID.

---

## ⚙️ Setup & Configuration

1.  **Clone the Repo:** `git clone https://github.com/yourusername/anti-bunk.git`
2.  **Define Coordinates:** Set your campus latitude, longitude, and radius (in meters) within the Fence API configuration.
3.  **Firebase Setup:** Add your `google-services.json` to the `/app` folder.
4.  **Permissions:** Ensure the app requests `ACCESS_FINE_LOCATION` and `ACCESS_BACKGROUND_LOCATION` (mandatory for Android 10+).

---

## 📬 Contact

For inquiries or to discuss implementing this at your institution:

**Simson** 📩 Email: [66simson99@gmail.com](mailto:66simson99@gmail.com)
