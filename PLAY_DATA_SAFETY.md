# Google Play Data Safety Responses

**App:** Lift Log (`tracker.lift_log`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

This document provides responses for the Google Play Console Data Safety section. Filling this form is required to republish (the listing was previously removed for a missing Data safety form).

---

## Overview

Lift Log is a local workout logger. Workout data stays in an on-device SQLite database. The app uses Google AdMob for banners and Google User Messaging Platform (UMP) for GDPR consent. There is no account system and no Firebase in this app.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| App interactions | Yes | Yes (AdMob) | Advertising |
| Advertising ID | Yes | Yes (AdMob) | Advertising |
| Device or other IDs | Yes | Yes (AdMob) | Advertising |
| Workout / exercise logs | Yes (on device only) | No | App functionality |

---

## Detailed Data Types

### 1. App Activity

#### App Interactions
- **Collected:** Yes (via Google AdMob / advertising measurement)
- **Shared:** Yes (with Google AdMob for ad personalization / serving)
- **Purpose:** Advertising
- **Optional:** Subject to GDPR/consent via UMP
- **Encrypted in transit:** Yes (HTTPS)

### 2. Device or Other IDs

#### Advertising ID
- **Collected:** Yes (via Google AdMob)
- **Shared:** Yes (with Google for advertising)
- **Purpose:** Advertising
- **Optional:** Users can reset or opt out via device settings; UMP consent in EEA/UK
- **Encrypted in transit:** Yes (HTTPS)

### 3. App Info and Performance

Lift Log does **not** include Crashlytics or Analytics SDKs in this modernization. Do not declare crash logs / diagnostics unless you add those SDKs later.

### 4. Personal Information

- **Email / name / phone:** Not collected by the app
- **User accounts:** None

### 5. Workout Data (on device)

Sets, reps, weights, and dates are stored in a local SQLite database. This is not transmitted to a developer backend. Still useful context for the Data safety form under “App functionality” if Play asks about data the app processes locally—follow the current Play Console wording for on-device-only data.

---

## Third-Party SDKs

### Google AdMob (`play-services-ads`)
- **Data collected:** Advertising ID, app interactions, device/approx. location signals as described by Google
- **Purpose:** Display advertisements
- **Shared:** Yes, with Google advertising partners
- **User Control:** UMP consent flow for GDPR
- **Privacy Policy:** https://policies.google.com/privacy

### Google User Messaging Platform (UMP)
- **Purpose:** GDPR consent management before ads are requested
- **Data collected:** Consent preferences
- **Privacy Policy:** https://policies.google.com/privacy

### Amazon Mobile Ads
- **Removed** in this release (HTTP / unencrypted ad traffic and deprecated SDK)

---

## Permissions (Play declarations)

| Permission | Why |
|------------|-----|
| `INTERNET` | Load ads / consent forms |
| `ACCESS_NETWORK_STATE` | AdMob connectivity checks |
| `ACCESS_WIFI_STATE` | Legacy connectivity checks used by ads stack |
| `WRITE_EXTERNAL_STORAGE` (maxSdk 28) | Legacy; not required for modern ads. Declare only if Play still surfaces it from the merged manifest for old API levels |

No `POST_NOTIFICATIONS`, camera, microphone, precise location, or contacts permissions are requested by this app.

---

## Data Security

- **In transit:** Yes (TLS/HTTPS). Cleartext traffic is disabled (`usesCleartextTraffic=false`).
- **At rest:** Local SQLite on device; no developer cloud sync.
- **Deletion:** Users can clear app data / uninstall. There is no cloud account to delete.

---

## Children's Privacy

This app is **not** directed at children under 13 (COPPA) or under 16 (GDPR).

- `setTagForUnderAgeOfConsent(false)` is configured in UMP
- Do **not** enroll in Designed for Families unless you change ad treatment to child-directed

---

## Regional Compliance

### GDPR (EU/EEA/UK)
- UMP consent dialog is shown before personalized ads when required
- Ads are only requested when `ConsentInformation.canRequestAds()` is true

### CCPA (California)
- Users can opt out of personalized ads via device advertising settings / Google account controls

---

## Play Console Form Responses (suggested)

### Does your app collect or share any of the required user data types?
**Yes** (Advertising ID / app interactions via AdMob)

### Is all of the user data collected by your app encrypted in transit?
**Yes**

### Do you provide a way for users to request that their data is deleted?
**Yes** (uninstall / clear app storage; no cloud account)

### Data Types to Declare

1. **Device or other IDs (Advertising ID)**
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Purpose: Advertising
2. **App interactions**
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Purpose: Advertising

---

## Contact

For privacy inquiries, contact the developer via the email address listed on the Google Play Store listing.
