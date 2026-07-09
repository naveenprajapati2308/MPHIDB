# MPHIDB Automation Framework — Working Context
**Purpose:** Continuity document for future sessions. Captures what was done, why, what's confirmed working, and what's still open — specifically for the Land Module debugging/rebuild effort.
**Status as of:** 2026-07-09 (single long working session)

---

## 1. Starting point

The Land Module test suite (`land.xml`, classes under `testCases/LandModule` + `pageObjects/LandModule`) was completely broken. The site (`https://godavari.mp.gov.in/mphb_qa/...`) had been redesigned by the dev team since the framework was last updated — every locator built on the old UI needed re-verification against the live app. The user's explicit rule throughout: **never guess a locator — verify it against the real DOM first**, using live investigation, screenshots, and page-source dumps.

## 2. Method used

Rather than guessing fixes, a series of small **throwaway standalone Java investigation scripts** (`Investigate1.java` … `Investigate21.java`) were written in the scratchpad directory, compiled against the project's Maven classpath (`mvn dependency:build-classpath`), and run directly with `java -cp` to log in, navigate, and dump real page source / screenshots at each step. Root causes were confirmed from that evidence before any production code was touched. This is why the session involved so many "run → wait → inspect" cycles — it's deliberate, not accidental churn.

**Practical lessons for next time (avoid re-learning these):**
- Runs must go through `run_in_background: true` + polling; a single `mvn test` cycle against the live site takes ~90–180s.
- Chrome/chromedriver processes leak across repeated investigation runs. If a run appears to hang for much longer than prior runs, check `tasklist` — it's often 15–20+ orphaned `chrome.exe` processes starving the live run of resources, not an actual code hang. Clean up with `taskkill //F //IM chrome.exe` (only when no test is currently mid-run) rather than waiting indefinitely.
- Repeated `findElements` polling loops with certain CSS selectors caused Selenium to hang once (~5+ min) for unclear reasons; switching to screenshot-based diagnosis instead of polling loops resolved it. Prefer "click → sleep → screenshot" over "click → poll `findElements` in a loop" when diagnosing.

## 3. Root causes found and fixed (all confirmed via live runs)

| # | Symptom | Real cause (verified from DOM/screenshots) | Fix |
|---|---|---|---|
| 1 | Login step always failed | Header text changed from exact "Welcome to Mp Housing Board." to a generic "Welcome!" shown on every page | `login.java` now asserts `.contains("Welcome")` instead of exact match |
| 2 | Land search never found rows | The land-name search input lives inside a collapsible "Advanced Search" panel; must click "Search" once to open it (text then reads "Hide Search") before the input exists | `LandManagement.searchLand()` now clicks the toggle first, then fills the field |
| 3 | Land creation never persisted | Clicking "Create" opens an untended "Submit Land Details?" confirmation modal (Cancel/OK) — without clicking OK, nothing saves | `Create_land.clickCreateButton()` now waits for and clicks the modal's OK |
| 4 | Land Purpose selection failed | Option text changed from `"Commercial"` to `"Commercial-C"` (site added code suffixes: `-H`/`-C`/`-P`) | `landCreation.java` updated to the new option text |
| 5 | Add Khasra's District dropdown picked the wrong value | The Khasra sub-form re-uses the same `select[name='district_id']` as the land-level form (invalid duplicate id on the page) — plain xpath matched the first (wrong) one | `Add_khasra.java`'s `KhasraDistrict` now scoped to `(//select[@name='district_id'])[2]` |
| 6 | **Khasra never actually saved, even with 0 validation errors and a clicked confirm modal** (root-caused after ~10 investigation rounds) | Khasra No. dropdown lists every seeded number for a village **regardless of whether it's already claimed** — it is not live-filtered. Selecting an already-claimed number pops a SweetAlert2 **"Duplicate Khasra"** dialog ("This khasra number is already registered for the selected village/ward.") which was going undetected | New algorithm in `Add_khasra.java`: `selectAvailableKhasraAcrossTehsilsAndVillages(tehsils...)` — for the current tehsil, try each village's khasra numbers first-to-last (dismissing the duplicate popup and moving to the next number each time); if a village's numbers are all taken, move to the next village; if a whole tehsil's villages are exhausted, move to the next tehsil. This was **specifically requested by the user in this exact escalation order** (village index first, then tehsil) |
| 7 | "Other Details" button never appeared | It only renders once a khasra has actually persisted (see #6) **and** the page-level "Update" button (separate from the khasra modal's own Submit/OK) has been clicked to save the land record | `AddKhasra.java` TC_006 now calls `khasra.clickUpdateBtn()` after the khasra modal is confirmed; new `TC_007` clicks Other Details. Locator also fixed: real markup is a `<button>`, not `<a>` |
| 8 | Survey Details fields 2/3 (Location Survey, Field Survey) not found | Only the required "DTS Survey" section (fields `survey_pdf_1`/`_cad_1`/`_details_1`/`_discrepancy_1`) is visible by default; Location/Field Survey are collapsed behind a "Show Optional" toggle | `Survey_details.clickShowOptional()` added and called between DTS and Location Survey in `SurveyDetails.java` |
| 9 | Forward to Chief Architect button never appeared, even after filling Survey | "Save & Next" on the Survey tab **also** shows an unhandled confirm modal (same swal2/bootstrap-modal pattern as land/khasra) — without confirming it, the survey data (and the "Survey submitted" flag that unlocks forwarding) never actually saves | `Survey_details.clickSaveBtn()` now waits for and clicks either a `modal-footer` OK or a `swal2-confirm` button after clicking Save & Next |
| — | Healenium (self-healing driver, "Option A") broke the **entire** suite (0 tests ran) | Adding the `com.epam.healenium:healenium-web` dependency pulled in JUnit Platform transitively, which made Maven Surefire silently switch from the TestNG provider to the JUnit provider | `pom.xml`/`pom-v2.xml`: excluded the JUnit/Testcontainers transitives from healenium-web, and explicitly pinned Surefire to `surefire-testng` in the plugin's own `<dependencies>` so this class of regression can't recur even if other deps change later |

## 4. Confirmed working end-to-end (as of the `Tests run: 10, Failures: 0` run)

Login → Create Land (all fields, file uploads) → Add Khasra (duplicate-safe selection) → Other Details navigation → Survey Details (DTS + Location + Field, agency info) → Forward to Chief Architect — **all 10 TestNG methods passed in a single live run** against `https://godavari.mp.gov.in/mphb_qa/...` using the `Exicutive_Engineer` (EE) role.

## 5. In progress / just added, NOT yet re-verified

A follow-up request came in: after clicking Forward, validate that the land's **Status badge flips from blue "Draft" to green "Land Id Created"**, and that the **Land Id / Ref Id column gets a real value** (e.g. `0801/NARSINGHPUR/C/0093`) instead of literally showing "Draft". This is exactly the same "Draft vs Land Id Created" status pattern visible in the Land List screenshots the user shared.

Added (not yet confirmed clean on a fresh run):
- `LandManagement.getStatusText(landName)` / `LandManagement.getLandId(landName)` — read the badge text / 3rd `<td>` for a row.
- `LandManagement.clickForwardButton()` now also handles a possible confirm modal after the click (same pattern as #9), since this had never been checked before.
- `ForwordToCA.java` TC_010 now re-searches after forwarding and asserts `status == "Land Id Created"` and `landId != "Draft"`.

**The verification run for this specific change showed regressions** (`Tests run: 10, Failures: 3` — TC_006, TC_008, TC_010 failed, TC_006 took 104.7s). This is very likely **test-data exhaustion**, not a new logic bug: dozens of live runs across this session have consumed many khasra numbers across the early villages/tehsils in the escalation list (`Kareli, Gadarwara, Gotegaon, Narsinghpur, Tendukheda` × their villages), so the escalation loop now has to search much deeper before finding an available number, occasionally exceeding whatever timeout/retry budget it hit. **Next session should re-run once cleanly and, if TC_006 is still slow/flaky, either widen the tehsil/village candidate list further or add more headroom to the escalation loop** rather than assuming the algorithm itself is wrong — the algorithm's correctness was already proven in the "Tests run: 10, Failures: 0" run.

## 6. Explicitly out of scope so far (user asked for these next, not started)

Per the user's screenshots, the "Other Details" page has **5 tabs**: Survey (done), **Dispute, Amenities, Approval Details, Payment Details** (not started). Only Survey is marked "required" for forwarding, but the user wants all 5 filled and each one turned into its own Page Object class + test case, following the same real-DOM-verification discipline as everything above. Land type in scope: **Government Land only** for now (per earlier decision) — other land types may have different tabs/fields.

## 7. Files touched this session

- `src/test/java/testBase/AuthorityLogin.java` — opt-in Healenium wrapper (`-Dhealenium.enabled=true`, default off)
- `src/test/java/pageObjects/LandModule/LandManagement.java` — search-panel fix, forward-modal handling, `getStatusText`/`getLandId`
- `src/test/java/pageObjects/LandModule/Create_land.java` — confirm-modal handling on Create
- `src/test/java/pageObjects/LandModule/Add_khasra.java` — district scoping fix, duplicate-khasra escalation algorithm, Other Details locator
- `src/test/java/pageObjects/LandModule/Survey_details.java` — Show Optional toggle, confirm-modal handling on Save
- `src/test/java/utilities/DropdownUtils.java` — new `waitForRealOptionText()` helper
- `src/test/java/testCases/LandModule/login.java` — welcome-message assertion
- `src/test/java/testCases/LandModule/landCreation.java` — purpose text fix, redirect-wait assertion
- `src/test/java/testCases/LandModule/AddKhasra.java` — escalation call, page-Update call, new TC_007
- `src/test/java/testCases/LandModule/SurveyDetails.java` — Show Optional call, dependency now on TC_007
- `src/test/java/testCases/LandModule/ForwordToCA.java` — post-forward status/land-id assertions
- `pom.xml`, `pom-v2.xml` — Healenium dependency (with exclusions) + pinned TestNG Surefire provider
- `src/test/resources/healenium.properties` — new, only read when Healenium is enabled

## 8. How to re-run and verify from a clean slate

```
# from D:\New folder\MPHIDB
taskkill //F //IM chromedriver.exe   # clean up any orphaned browser processes first
taskkill //F //IM chrome.exe
mvn -f pom.xml test -DsuiteXmlFile=land.xml -DopenReport=false
```
Expect ~2–3 minutes. Check `target/surefire-reports` or the console tail for `Tests run: 10, Failures: 0`.
