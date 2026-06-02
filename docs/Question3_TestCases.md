# Question 3 — DailyFinance End-to-End Test Cases

**Application Under Test:** https://dailyfinance.roadtocareer.net/
**Test Framework:** Selenium WebDriver 4 + JUnit 5 (POM architecture)
**Author:** Nusrat Rita
**Date:** 2026-05-31

---

## Test Environment

| Item                | Value                                                |
|---------------------|------------------------------------------------------|
| Browser             | Google Chrome (latest)                               |
| OS                  | Windows 11                                           |
| Java                | JDK 11+                                              |
| Build tool          | Gradle (Kotlin DSL)                                  |
| Test runner         | `./gradlew test --tests "tests.Question3Test"`       |
| Admin credentials   | Passed at runtime via `-Dadmin.email` / `-Dadmin.password` |
| Reset link delivery | Pasted into an always-on-top dialog during step 4   |

---

## Test Case Summary

| ID            | Title                                                     | Type     | Status      |
|---------------|-----------------------------------------------------------|----------|-------------|
| TC-01         | Register a new user and assert congratulations message    | Positive | ✅ Pass     |
| TC-02-NEG-A   | Forgot password with empty email is rejected              | Negative | ✅ Pass     |
| TC-02-NEG-B   | Forgot password with invalid email format is rejected     | Negative | ✅ Pass     |
| TC-03         | Send reset link to the registered email                   | Positive | ✅ Pass     |
| TC-04         | Open reset link from Gmail and set a new password         | Positive | ✅ Pass     |
| TC-05         | Login with the new password succeeds                      | Positive | ✅ Pass     |
| TC-06         | Add 2 items (mandatory-only + all-fields) and verify list | Positive | ✅ Pass     |
| TC-07         | Update profile email to a new Gmail address               | Positive | ✅ Pass     |
| TC-08         | Login with the updated email succeeds                     | Positive | ✅ Pass     |
| TC-08-NEG     | Login with the previous email fails                       | Negative | ✅ Pass     |
| TC-09         | Admin can log in using terminal-supplied credentials      | Positive | ✅ Pass     |
| TC-10         | Admin can search and see the updated user email           | Positive | ✅ Pass     |

---

## Implementation Note (SPA quirk)

The profile is a client-rendered SPA. After navigating to the profile view, the page object
performs a **one-time hard refresh** before editing. Without it, an Update submitted against
the not-yet-fully-bound React form is silently dropped (no error shown, change not saved);
refreshing so the form is fully loaded makes the update save reliably. This was discovered
during testing and is handled inside `DashboardPage.openProfile()`.

---

## TC-01 — Register a new user

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| User is not already registered                                 |
| **Test data**    | name=`QA Tester {rand}`, email=`gmailuser+{rand}@gmail.com`, password=`Pass@{rand}1` |
| **Steps**        | 1. Navigate to `/register`. 2. Fill name, email, password, confirm password. 3. Click Register. |
| **Expected**     | Success/congratulations message appears (e.g. "Check your email to verify"). |
| **Priority**     | High                                                           |

---

## TC-02-NEG-A — Forgot password with empty email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| Forgot-password page is reachable from the login page          |
| **Test data**    | email=`""` (empty)                                             |
| **Steps**        | 1. From `/login` click "Forgot Password". 2. Leave email empty. 3. Click "Send Reset Link". |
| **Expected**     | Form is rejected — error message shown OR submit button disabled OR success message NOT shown. |
| **Priority**     | Medium                                                         |

---

## TC-02-NEG-B — Forgot password with invalid email format

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| Forgot-password page reachable                                 |
| **Test data**    | email=`not-an-email`                                           |
| **Steps**        | 1. Navigate to `/forgot-password`. 2. Enter `not-an-email`. 3. Click "Send Reset Link". |
| **Expected**     | Error message shown OR success message NOT shown.              |
| **Priority**     | Medium                                                         |

---

## TC-03 — Send reset link to registered email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-01 completed; user exists                                   |
| **Test data**    | email=`{email from TC-01}`                                     |
| **Steps**        | 1. Navigate to `/forgot-password`. 2. Enter registered email. 3. Click "Send Reset Link". |
| **Expected**     | Confirmation message "Reset link sent / Check your inbox" appears. |
| **Priority**     | High                                                           |

---

## TC-04 — Reset password via emailed link

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-03 completed; reset email received                          |
| **Test data**    | newPassword=`Pass@{rand}1` (regenerated)                       |
| **Steps**        | 1. Tester opens Gmail, copies the reset URL, pastes it into the console when prompted. 2. Test navigates to that URL. 3. Enter new password + confirm. 4. Submit. |
| **Expected**     | "Password reset successfully" or redirected to `/login`.       |
| **Priority**     | High                                                           |

---

## TC-05 — Login with new password

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-04 completed                                                |
| **Test data**    | email=`{registered}`, password=`{new from TC-04}`              |
| **Steps**        | 1. Navigate to `/login`. 2. Enter credentials. 3. Click Login. |
| **Expected**     | Dashboard loads.                                               |
| **Priority**     | High                                                           |

---

## TC-06 — Add 2 items and verify list

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| User logged in (TC-05)                                         |
| **Test data**    | item1=all fields (`FullItem_{rand}`), item2=mandatory only (`MinItem_{rand}`) |
| **Steps**        | 1. From dashboard click "Add Item". 2. Fill all fields, save. 3. Click "Add Item" again. 4. Fill only mandatory fields, save. |
| **Expected**     | Both item names appear in the item list.                       |
| **Priority**     | High                                                           |

---

## TC-07 — Update profile email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| User logged in                                                 |
| **Test data**    | newEmail=`nusratrita00+{rand}@gmail.com` (different from registered) |
| **Steps**        | 1. Open account menu → Profile (page is hard-refreshed). 2. Click EDIT. 3. Change the email field. 4. Click UPDATE. |
| **Expected**     | The new email is saved; reopening the profile shows the updated address. |
| **Automated assertion** | The test reopens the profile and asserts the email now equals the new address (update persisted). |
| **Priority**     | High                                                           |

---

## TC-08 — Login with updated email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-07 completed (email updated and saved)                      |
| **Test data**    | email=`{updatedEmail}`, password=`{newPassword}`               |
| **Steps**        | 1. Logout. 2. Navigate to `/login`. 3. Enter updated email + new password. 4. Click Login. |
| **Expected**     | Dashboard loads.                                               |
| **Priority**     | High                                                           |

---

## TC-08-NEG — Login with previous email fails

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-07 completed (email already changed)                        |
| **Test data**    | email=`{old registered email}`, password=`{newPassword}`       |
| **Steps**        | 1. Logout. 2. Try logging in with the OLD email. 3. Click Login. |
| **Expected**     | Login fails — error shown or still on `/login`.                |
| **Priority**     | High                                                           |

---

## TC-09 — Admin login with terminal-supplied credentials

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| Tester runs Gradle with `-Dadmin.email=... -Dadmin.password=...` |
| **Test data**    | adminEmail, adminPassword (never hard-coded in repo)           |
| **Steps**        | 1. Logout. 2. Navigate to `/login`. 3. Submit admin credentials read from system properties. |
| **Expected**     | Admin dashboard loads.                                         |
| **Priority**     | High                                                           |

---

## TC-10 — Admin can find updated user by email

| Field            | Value                                                          |
|------------------|----------------------------------------------------------------|
| **Pre-condition**| TC-09 completed; admin sees user list                          |
| **Test data**    | searchTerm=`{updatedEmail from TC-07}`                         |
| **Steps**        | 1. Open admin Users page. 2. Type updated email in search. 3. Wait for filtered rows. |
| **Expected**     | A row containing the updated email is shown in the admin table.|
| **Priority**     | High                                                           |
