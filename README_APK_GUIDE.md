# HisabFit BD — Local-First Android APK & Google Play Store Project

This package contains the complete Android APK project for **HisabFit BD**, pre-configured so that **100% of user tracking data, custom recipes, multi-day weight history, BMI goals, and personal cookies are stored locally on the user's phone**.

---

## 🔒 How Local Data & Cookie Persistence Works in This APK

1. **`LocalStorage` (`hisabfit_bd_v1_state`)**: Immediately saves every logged food item, custom recipe, and BMI calculation on every tap.
2. **`IndexedDB` (`HisabFitBD_LocalVault`)**: Automatically mirrors your entire database locally so even large multi-year food histories never hit browser quota limits.
3. **Persistent On-Device Cookies (`document.cookie` + `CookieManager.flush()`)**: Stores user preferences (`hf_wake_lock`, `hf_admob_consent`, `hf_asian_bmi`, `hf_idb_mirror`, `hf_active_date`) with a 365-day expiration and flushes them directly to Android flash memory via `CookieManager.getInstance().flush()`.
4. **Native Android File Vault (`window.AndroidLocalVault`)**: Inside `MainActivity.java`, a `@JavascriptInterface` bridge writes a full JSON copy to Android private internal storage (`context.openFileOutput("hisabfit_bd_vault.json", Context.MODE_PRIVATE)`) and `SharedPreferences` (`HisabFitBD_LocalPrefs`), ensuring Android OS cache cleanups never erase user data.

---

## 🚀 3 Ways to Install / Build Your `.apk` File

### Option 1: Build the `.apk` in the Cloud via GitHub Actions (No Android Studio Needed!)
1. Create a free repository on GitHub and upload the contents of this ZIP folder.
2. Click the **Actions** tab in your GitHub repository and run the **"Build HisabFit BD Android APK"** workflow.
3. In ~90 seconds, download **`HisabFit-BD-Android-APK.zip`** containing your ready-to-install `app-debug.apk`.

### Option 2: Build Signed APK / Play Store `.aab` in Android Studio
1. Open **Android Studio** and select **Open** -> choose the `android/` folder inside this ZIP.
2. Wait for Gradle sync to complete, then click **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**.
3. For Google Play Store upload, click **Build -> Generate Signed Bundle / APK...** and choose **Android App Bundle (`.aab`)**.

### Option 3: Instant Offline Install via Chrome on Android (PWA Mode)
1. Host `www/index.html` and `www/manifest.json` on any static host (such as GitHub Pages, Netlify, or Cloudflare Pages).
2. Open the link in Chrome on your Android phone and tap **"Add to Home Screen / Install App"**.
