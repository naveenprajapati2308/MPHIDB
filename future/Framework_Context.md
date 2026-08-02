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

## 9. Session 2026-07-10: Government flow closed out, khasra stale-element bug found & fixed, 7 land-type scaffolds added

**Source of truth for test cases this session:** `src/test/resources/resource/MPHIDB TC  RTM.xlsx`, sheet "Land Management" (rows ~1119-1719, test IDs LM_001-LM_199+, one User Story per land type). It's a binary xlsx - read it by unzipping it (it's a zip) and parsing `xl/worksheets/sheetN.xml` + `xl/sharedStrings.xml` directly (no Python/pandas available in this environment); a throwaway Node script for this is the fastest path.

**Government flow (section 5 above) - resolved:**
- The `ForwordToCA.TC_010` assertions (status flips to "Land Id Created", Land Id column stops showing "Draft") described as "in progress, not yet re-verified" in section 5 were already correctly implemented going into this session - matches excel LM_041 exactly ("Verify allow to create Land id by clicking 'Forward to Chief Architect' button... Create LAND ID after fwd to Chief Architect"). No new assertion code was needed for Government.
- What section 5 mis-diagnosed as "test-data exhaustion" causing TC_006/TC_008/TC_010 failures was actually a **real bug**: `Add_khasra.realSelectOptionTexts()` and `selectAvailableKhasraNumber()` read a dropdown's options right after `DropdownUtils.waitForOptionsToLoad()`/`waitForRealOptionText()` first sees them populate - but the site re-renders the `<select>` a beat later, throwing `StaleElementReferenceException` mid-iteration. Fixed by wrapping both option-reading loops in a stale-retry (up to 3 attempts, re-fetching fresh `Select(dropdown).getOptions()` each time since the underlying `@FindBy` element re-locates on each access).
- After that fix, a *different*, non-reproducible failure showed up once (`TC_002` timing out 10s on `//a[normalize-space()='Create Land ID']` right after `Dashboard.clickLandmanagement()`), which looks like live-site slowness/network flakiness rather than a code bug - user's call was to leave it alone rather than chase it further. If it recurs consistently, the fix is likely a longer/explicit wait after `clickLandmanagement()` before the land list page's controls are queried, not a locator change.

**7 remaining land-type scaffolds (new this session, explicitly NOT added to `land.xml`):**

Per user instruction, wrote Page Objects + TestNG test-case classes for all 7 non-Government land types, but deliberately kept them **out of `land.xml`** since their field locators are best-effort (label-anchored xpaths built from the excel's literal field text, not live-DOM-verified) and could break the working suite if included. Files:

| Land type (excel User Story) | Page Object | Test Case |
|---|---|---|
| Deposit Work (US 8, LM_001-045) | `AddKhasraDepositWork.java` | `DepositWorkLandCreation.java` |
| Housing for Redensification (US 5, LM_069-091) | `AddKhasraHousingRedensification.java` | `HousingRedensificationLandCreation.java` |
| Land Acquisition Act (US 3, LM_092-120) | `AddKhasraLandAcquisitionAct.java` | `LandAcquisitionActLandCreation.java` |
| Land Pulling (US 4, LM_121-144) | `AddKhasraLandPulling.java` | `LandPullingLandCreation.java` |
| Private Land (US 2, LM_145-166) | `AddKhasraPrivateLand.java` | `PrivateLandLandCreation.java` |
| Redensification for Government (US 6, LM_167-195) | `AddKhasraRedensificationGovt.java` | `RedensificationGovtLandCreation.java` |
| Redevelopment (US 7, LM_196-199) | `Redevelopment.java` | `RedevelopmentLandCreation.java` |

Design decisions baked into these files:
- **Shared fields reused, not duplicated.** `Create_land.java` (Land Details: district/circle/division/subdivision/town/location/land name/purpose/use/payment) and `Add_khasra.java` (Khasra identity: type New/Old, tehsil, village, khasra-no duplicate-escalation, area-in-hectare, lat/lng) are the SAME across all 8 land types per the excel and the live site's single dynamic "Create Land" form - every new test class instantiates and calls these existing page objects directly rather than re-implementing them. Only each type's genuinely divergent fields (dates, type-specific dropdowns, document sets, owner/land-source repeaters) got new page objects.
- **Per-type unique land names.** `testData.java` gained an overload `landName(String suffix)` (e.g. `landName("DepositWork")`) so these 7 types can eventually run in the same suite/JVM as the Government flow without tripping the app's own "Land name already taken" duplicate-name check (excel LM_042).
- **Locators are explicitly flagged as unverified.** Every new page object's class-level Javadoc says so and points back to this file's section 2 methodology (throwaway investigation scripts against the live DOM) as the required next step before enabling. Do not assume these locators are correct - they were written from the excel's field *labels*, not from live inspection.
- **Redevelopment is the outlier.** Mechanically different (pick an existing Land-Id-created land, select its khasra(s) via checkbox, extract area into a brand-new land) rather than "create new khasras". The excel itself flags this flow as currently broken on the live site ("Redevelopment Id create faile- throwing Integer error", LM_199) - a failure here needs to be triaged against that known defect before assuming it's a framework/locator problem.

**Next steps (not started):** Live-DOM verification pass per type (same investigation-script method as section 2) before any of these 7 get added to `land.xml`, starting wherever the user wants to script next. — Superseded by section 10 below; that verification pass started the same day.

## 10. Session 2026-07-10 (cont'd): Government flow merged into one class, live-verification pass started on the 7 land types

**Government flow restructured.** Per user request, merged the previously separate `landCreation.java` + `AddKhasra.java` + `SurveyDetails.java` + `ForwordToCA.java` into a single `GovernmentLandCreation.java` (TC_002-TC_010, same bodies, same order), mirroring the one-class-per-type shape already used for the other 7. The 4 old files were deleted and `land.xml` now just lists `login` + `GovernmentLandCreation`. One deliberate behavior change during the merge: `TC_005` used to declare `dependsOnMethods = {"TC_001"}` only and silently relied on cross-class XML ordering for TC_002-004 to run first - inside a single class that's not safe (TestNG doesn't guarantee declaration order without explicit deps), so the merged file chains each method to the previous one (004→005→006→...→010) explicitly. Verified via a full `land.xml` run: 9/10 passed cleanly, only the Forward-to-CA step hit the live-site timeout described below - the merge itself is confirmed structurally sound.

**Two real, generically-impactful bugs found and fixed** while live-verifying the 7 new types (not locator issues - actual logic bugs that would have bitten every non-Government type):
1. `Create_land.enterLandName()` unconditionally waited for `purpose_of_land_id`/`land_use_id`/`payment_type` to load - hung/failed for Deposit Work, which doesn't render a Purpose-of-Land or Payment Type field at all. Fixed by wrapping each wait in try/catch (`waitIfPresent`) so a field that never appears for a given land type is simply skipped, not fatal.
2. `Add_khasra.clickOkBtn()` only waited for the OK button to be clickable, not for the resulting save to actually finish. Confirmed live (via screenshots) that saving a khasra with several document uploads can show a loading spinner for **up to ~30 seconds**; any caller that proceeded immediately (e.g. clicking the page-level Update button) could hit a still-open modal and fail. Fixed by waiting up to 45s for the khasra modal (`#khasraModalLabel`) to become invisible after clicking OK, inside `clickOkBtn()` itself - every land type gets this for free.

**Dead code removed per user instruction ("reuse as much as possible, don't write unnecessary code").** Every one of the 7 new `AddKhasraXxx.java` page objects originally had its own copy-pasted Submit/OK/Update/Other-Details methods, identical to `Add_khasra.java`'s. These were deleted from all 7, and every test case now calls the shared `Add_khasra` instance for those 4 actions - which also means every type automatically gets bug-fix #2 above.

**Verification status per type (method: live investigation script → fix real locators/logic → run the actual TestNG class via a temporary `land_verify.xml`, never `land.xml`):**

| Type | Status | Notes |
|---|---|---|
| Government | ✅ 9/10 (merge verified) | Only the known Forward-to-CA flakiness (see below) |
| Deposit Work | ✅ 10/10 | Fully verified, all locators confirmed live |
| Housing for Redensification | ✅ 10/10 | Confirmed live: unlike Deposit Work, this type DOES render `purpose_of_land_id`/`land_use_id`/`payment_type` (same as Government) plus the allotment-letter/payment-receipt uploads. Also confirmed it needs 7 required khasra documents (`Khasra Copy, Map Layout, Demarcation, Possession of Land, Field Book, Allotment Letter, Empowered Committee MOM` via `doc-input-0..6`, same set as Redensification-for-Govt) even though the excel doesn't mention a Documents section for it at all - found via the server's own "Required fields missing" error |
| Land Acquisition Act | ⚠️ 9/10 | All real logic (land creation, khasra, 5 documents, survey) verified live and passing. Two extra conditionally-rendered sub-sections were found (`section_4_date`/`section_4_notification_file`, `section_6_date`/`section_6_notification_file`) that only appear once the "Right for fair Compensation..." Act is selected - not visible in the initial khasra-modal dump. Only the final Forward-to-CA click hit the known live-site timeout (see below) - not a code issue |
| Land Pulling | ✅ 10/10 (2026-07-16, full clean run) | Fixed via live DOM dumps + 3 iterative runs. Real facts (all live-confirmed): date fields are `date_of_notification` / `final_notification_date` / `final_agreement_date` (old `notification_date` guess didn't exist); land-source option text is "Land Pulling" (capital P); owner details live in their own "Add Land Owner" modal (`land_owner`, `land_area` type=number with `max` bound to the khasra's `area_hectare` value, `land_owner_mutation_date`, visible pdf input `land-owner-agreement-<n>`, confirm button exact text "Add"); docs are `doc-input-0..4` = Khasra Copy, Scheme Layout, Demarcation, Possession of Land, Field Book (order as already coded). **Two general lessons that will bite other types too:** (1) these khasra-modal forms are React-controlled — plain JS `.value` assignment leaves React state empty ("field is required" with the value visibly present); (2) some fields only COMMIT to form state on blur/focusout. Both handled centrally by new `DateUtils.setReactValue`/`setReactDate` (native value setter + input/change/blur/focusout events) — use those for any React-form field instead of raw JS or bare sendKeys when a "required" error appears despite a filled field. `AddKhasraLandPulling.addLandOwner()` also waits for the owner modal to actually close so an in-modal validation failure surfaces at the right step. |
| Private Land | ✅ 10/10 (2026-07-16, full clean run) | Fixed via live DOM dumps. Live-confirmed facts: (1) CREATE form has two REQUIRED uploads - Allotment Letter (`file_allotment_letter`) + Payment Receipt (`file_payment_receipt`) - via the existing `Create_land.uploadAllotmentLetter/uploadPaymentReceipt`; (2) land-source option text "Direct Purchase" (capitalized); (3) owner details = "Add Land Owner Details" modal ("+ Add Owner" button): ids `current_owner_of_land`, `owner_name`, `purchase_date`, `registry_date`, `possession_date`, and `date_of_purchased` (= the MUTATION date field - odd dev naming), confirm button "Add", no file upload; By.ID needed because a hidden form-level `name='purchase_date'` (defaults to today) shadows By.name; (4) docs `doc-input-0..4` = Demarcation, Possession of Land, Field Book, Map Layout, Khasra Copy (this type's own order); (5) the "Submit Khasra Details?" confirm dialog can render >10s after Submit - `Add_khasra.clickOkBtn()` now budgets 45s (shared fix, all types benefit). |
| Redensification for Government | ✅ 10/10 (2026-07-16, full clean run) | Fixed via live DOM dump. Field names live-confirmed: `collector_order_date` / `collector_order_file` / `development_date` / `possession_date` / `possession_details_file` (the "Possession Document" upload - old label-anchored guess was wrong) / `possession_details` (textarea) / `land_details` (textarea - old `//label['Land Details']/following::textarea` guess didn't match). Dates + textareas now via `DateUtils.setReactDate/setReactValue` (React blur-commit forms; setReactValue also handles TEXTAREA via HTMLTextAreaElement prototype). Docs `doc-input-0..6` = Khasra Copy, Map Layout, Demarcation, Possession of Land, Field Book, Allotment Letter, Empowered Committee MOM - the guessed order was correct. |
| Redevelopment | Explicitly out of scope | User: "you can't handle it, it has different flow" - skip entirely for now |

**Known, already-diagnosed flakiness (do not re-investigate as a bug):** the "Forward to Chief Architect" button intermittently isn't found within the 20s wait right after Survey save, across multiple otherwise-fully-passing runs (Government, Land Acquisition Act). User's explicit call: this is very likely live-site/network slowness, not a locator or logic problem - leave it alone rather than chasing it further.

**Site-availability issue that ended this session:** After ~20 live browser sessions today (investigation scripts + verification runs), even the untouched, previously 100%-reliable `login.java` (`TC_001`) started failing 5 times in a row at the exact same step - waiting for the "Land Management" sidebar link to become clickable after login, 20s timeout, "Welcome!" banner present so login itself succeeded. Cleared ~8 accumulated orphaned `chrome.exe` processes (known issue, see section 2) and retried - still failed identically. This strongly suggests the live QA site itself was slow/degraded at that point, not a code or environment problem. **User's explicit decision: stop here, this was the right call, resume from this document next session.**

**How to resume:**
1. `land_verify.xml` (repo root, gitignored-worthy scratch file, currently pointed at whichever type was last tested - check/update its `<class>` entry) is the pattern for testing one type in isolation without touching the real `land.xml`: `<class name="testCases.LandModule.login" />` + `<class name="testCases.LandModule.XxxLandCreation" />`, then `mvn -f pom.xml test -DsuiteXmlFile=land_verify.xml -DopenReport=false`.
2. Before any run: check `tasklist //FI "IMAGENAME eq chrome.exe"` and `taskkill //F //IM chrome.exe` / `chromedriver.exe` if orphans are piled up - this alone has caused false failures twice this session.
3. The investigation-script technique that worked well: a standalone Java class (compiled via `mvn dependency:build-classpath` + `javac`/`java -cp`, run from the project root so relative `testData/config.properties` paths resolve) that reuses the real page objects (`LoginPage`, `Dashboard`, `LandManagement`, `Create_land`, `Add_khasra`) to log in, create a real land of the target type, edit it, open Add Khasra, and dump `document.body.outerHTML` to a file at each stage - then `grep`/`node -e` the dump for `name=`/`id=`/`<option`/`is-invalid` to get real field names instead of guessing. These scratch investigation `.java` files live in the session's temp scratchpad directory, not the repo, so they don't persist between sessions - recreate the pattern from this description rather than looking for the old files.
4. Immediate next steps in priority order: (a) get a clean run on Land Pulling once the site is responsive again, (b) audit Private Land's document uploads for the same "reused Add_khasra positional methods on a differently-shaped form" bug already found and fixed in Land Acquisition Act, (c) do a first live-verification pass on Redensification for Government (never run once yet), (d) once all 6 are green, revisit the user's still-open ask from earlier this session: cap each type's test class at roughly 15 `@Test` methods with at least one assertion each (some can validate toast/popup messages), which hasn't been addressed yet - current classes sit at 9-10 methods per type.

## 10b. Session 2026-07-16 (afternoon): ALL 7 LAND TYPES VERIFIED — land.xml now runs the full set

QA came back up mid-afternoon and stayed up. In one session: Land Pulling ✅ 10/10, Private Land ✅ 10/10, Redensification for Govt ✅ 10/10 (details in the §10 table rows, all via the DOM-dump investigation method — 6 throwaway scripts, ~8 live runs). That completes every land type except Redevelopment (explicitly out of scope).

**`land.xml` now lists login + all 7 type classes** (Government, Deposit Work, Housing Redensification, Land Acquisition Act, Land Pulling, Private Land, Redensification Govt). `land-v2.xml` mirrors it (v2/portal listener); `land_verify.xml` DELETED per user — land.xml + land-v2.xml are the only land suite files now. For one-class isolation runs, create a scratch xml in the session scratchpad and pass its absolute path to `-DsuiteXmlFile`.

**Combined-run status (2026-07-16 evening, session ended here):** two attempts both failed AT LOGIN — not locators. Evidence: TC_001 failure screenshot shows only a full-page loading spinner (dashboard still loading when the 20s `waitForElementClickable(Landmanagement)` expired); a login-only smoke passed minutes before AND after. QA gets slow under evening load. **Identified fix, NOT yet applied (user held all changes):** longer explicit wait (~90s) on the Land Management link in `login.java` TC_001 — this is the suite bootstrap, everything else cascades from it (7× TC_x02 "Create Land ID not found" failures + 56 skips are pure downstream). Also note TC_x02s FAILED rather than SKIPPED on TC_001 failure — the cross-class `dependsOnMethods={"TC_001"}` doesn't actually gate them; worth checking later. Applied this session: `FailureDiagnostics.captureRootCause` now zeroes the implicit wait during its scan (each failing test was burning ~131s in diagnostics — measured drop: suite fail-fast 1082s → 117s).

**Cross-cutting infrastructure fixes this session (benefit every type):**
- `DateUtils.setReactValue/setReactDate` — native value setter + input/change/blur/focusout for the React blur-commit modal forms (works for input AND textarea). Use these whenever a filled field still says "required".
- `Add_khasra.clickOkBtn()` — confirm-dialog wait 10s→45s (dialog can render slowly after многodoc submits).
- `FailureDiagnostics` — ignores bare "*" / <3-char texts (mandatory-marker false positive).

## 11. Same-day follow-up: parallel execution requested, then deliberately deferred - nothing changed, do not assume otherwise

Immediately after section 10 was written the user tried checking back in (still same day), asked to parallelize land-type execution to cut down total run time, then **explicitly deferred that entire idea** once the tradeoffs became clear. Record this precisely so the next session doesn't accidentally redo or half-apply it:

**What was discussed (design only, nothing implemented):**
- Root blocker for parallelism: every test class currently shares ONE `static WebDriver driver` in `AuthorityLogin`, set up once via `@BeforeSuite`. Multiple classes on multiple threads would fight over that single browser tab - not safe.
- Proposed fix (design only): make `driver` a per-instance (non-static) field, move setup/teardown from `@BeforeSuite`/`@AfterSuite` to `@BeforeClass`/`@AfterClass` so each class gets its own browser + own login, enabling `parallel="classes"` in the suite XML. Confirmed (by reading `ReportManager/Master_extent_report.java`) that this is safe for reporting: `Master_extent_report` is a separate `ISuiteListener` whose `onStart`/`onFinish` fire on TestNG's true suite boundary regardless of what's in `AuthorityLogin`, and its per-test screenshot-on-failure code reads `driver` via reflection off the failing test's own instance - which becomes *more* correct with a per-instance field, not less.
- User's separate, real requirement surfaced during this discussion (also not yet built): once a land reaches "Land Id Created" via the Executive Engineer flow, it needs to be picked up by a **Chief Architect** login (`AuthorityLoginManager.loginAs(driver, "CA")` - that utility already exists and already takes `driver` as a parameter, so it's compatible with either a static or per-instance driver) and continue into the existing **"architect work flow" module** (see commit `c4581fb "architect work flow module added"` - not investigated yet this session). Same site/URL, different credentials. Two shapes were discussed for this, still undecided: (a) same class, `logout()` then `loginAs(driver, "CA")` mid-test to continue in the same browser session, or (b) a separate class with its own CA login that re-finds the land by searching (`searchLand(testData.landName(...))`), closer to how a real Chief Architect would actually use the system on a different day. **Not decided - revisit when this work actually starts.**
- User's explicit final call: shelve all of this (both the parallel-execution refactor and the multi-role/architect-workflow login work) until the land-type verification (section 10's remaining items) is done. Go back to land-type coverage first.

**What was actually changed in code: nothing.** Three separate attempts to edit `testBase/AuthorityLogin.java` were all rejected/interrupted by the user before landing - confirmed via `git status`/`git diff` showing `AuthorityLogin.java` absent from the changed-files list. There is nothing to revert. Do not re-attempt this refactor until the user explicitly asks for it again.

**Session ended here, site still down.** A retry of Land Pulling (section 10, item 4a) was attempted but the user stopped it - the live QA site was confirmed still unresponsive at this point too, hours after the section-10 outage was first noticed. Next session: check site responsiveness first (a quick login via `land_verify.xml` running just `login.java`, or ask the user) before spending time on anything live-site-dependent.

## 12. Site outages and the failure-diagnostics feature: moved out

Session 2026-07-10's site-down investigation and the new `FailureDiagnostics` root-cause-reporting feature are tracked separately in **`future/Error_Context.md`**, not here - keep error/outage tracking out of this file going forward; this file stays about the land-type build-out itself. Land-type verification progress/status table is still section 10 above; check `Error_Context.md` for anything related to *why a live run failed* or to the diagnostics feature that surfaces real root causes on test failure.
