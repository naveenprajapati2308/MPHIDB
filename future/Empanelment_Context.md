# MPHIDB Architect Empanelment — Working Context
**Purpose:** Continuity doc for the Architect Empanelment automation rework (same pattern as `Framework_Context.md` for Land). Land module work is paused (QA down); this module runs against **UAT** (`mphb_uat`, config me URL).
**Status as of:** 2026-07-15

---

## 1. What was rebuilt this session (user instructions)

- **NO DataProviders going forward** (explicit user instruction). Old `TC_002_SignUp` (DataProvider matrix) + `TC_003_SignIn` are superseded; left in repo but removed from `Emp_Arch.xml`.
- **Target: complete flow first** — registration → login → all 5 tabs → ack checkboxes → FINAL SUBMIT (application number). Detailed TC-by-TC expansion comes after flows are green.
- **Every registered user is appended to `testData/registered_users.txt`** (`RegisteredUsersLog`) — user needs this to delete automation users from the DB later. Never remove this logging.
- Positive + negative scenarios with assertions and validation-message checks.

## 2. New structure

| File | Role |
|---|---|
| `testBase/CmsFlowBase.java` | Browser-per-class base for flow suites (NO per-method home reload — that's `BaseTest`, which would wipe form state mid-flow) |
| `testCases/ArchitectEmpanelment/EmpanelmentFlowBase.java` | Abstract 13-step chain TC_E01–TC_E13; type hooks: `orgType()`, `selectFirmType()`, `hasShareholders()`, `typeName()` |
| `...IndividualEmpanelmentFlow` etc. | 6 thin subclasses: Individual, Proprietorship, Partnership, PvtLtd, PublicLtd, LLP |
| `...SignupNegativeTests.java` | 9 explicit signup/login negatives (replaces the DataProvider matrix) |
| `utilities/RandomUtils.java` | Counter-based unique generators: `generatePan/Gst/FirmRegNo/CoaNo/FirmName` (duplicacy rules TC_AR_026/093/094) |
| `utilities/ValidationUtils.java` | Fast error scan for flow assertions; success Toastify/swal ≠ error (keyword filter). Do NOT confuse with `FailureDiagnostics` (listener-only, parked) |
| `utilities/RegisteredUsersLog.java` | Appends every created user (+password changes, +application no) to `testData/registered_users.txt` |
| `pageObjects/ArchitectEmpanelment/ChangePasswordPage.java` | First-login forced password change (live-verified) |
| `Emp_Arch.xml` | Full suite: LaunchUrl + SignupNegativeTests + 6 flows (order matters — see §4) |
| `emp_verify.xml` | Scratch: one class at a time (like `land_verify.xml`) |

## 3. Live-verified UAT findings (2026-07-15) — DO NOT re-guess these

- **First login force-redirects to `/user/change-password`** (TC_AR_007/008 is now LIVE on UAT, excel me "not developed" likha tha). Fields: `current_password`, `new_password`, `new_password_confirmation`, submit "Change" → dashboard. Flow sets new password `Test@<username>` (logged to registered_users.txt).
- Registration email input: `name='email'`, placeholder `Enter Email ID` (capital E — old lowercase wait was dead code). OTPs are shown on-page (`span.fw-bold`), captcha static `9895`, initial password `12345678` from config.
- **Manage Empanelment opens the form DIRECTLY for a fresh user** — no "Add New" button (`clickAddNewIfPresent()` handles both cases).
- Tab-pane ids: `home1`=Application Details, `education`=Qualification, `certificate`=Certificate+Docs+Projects, `partner`=Stakeholders/Other Details, `profile1`=Acknowledgement. Each pane has its OWN submit — never use the old global `btn-outline-warning` xpath (only home1 has it).
- Individual: address = single `textarea[name='primary_address']`; NO state/district/pincode; tab label "Other Details"; NO partner section.
- Firm: firm_type radios id 1–5 (1=Proprietorship, 2=Partnership, 3=PvtLtd, 4=PublicLtd, 5=LLP — confirmed labels); Registered State / State / District are **react-select** comboboxes (label-anchored input + type + Enter; District disabled until State picked); pincode/address/fax/landline plain; TWO `name='email'` inputs (fill both); tab label "Stakeholders/Other Details". Proprietorship has NO partner section (tab-switch twist), other 4 have it.
- Qualification tab = table; each row has its own small save (`button[@title='Save new record']`, disabled until row valid); graduation upload `id='degree-upload-graduation'` (hidden input, sendKeys works).
- Certificate: COA upload = unnamed `accept='.pdf'` input in `#certificate`; `certificate_issued_date` is `type=date` (max = today) → `DateUtils.DatePicker` (yyyy-MM-dd); lifetime radios `lifeYes`/`lifeNo`.
- **Project year is a SELECT capped at the current year** — future years excluded by design, so TC_E10 asserts the dropdown offers no future option (instead of typing 2030).
- Success feedback is Toastify toasts / swal ("Submission Successful...") — `ValidationUtils` ignores success messages when scanning for errors.

## 4. Verification status (final for this session)

Solo runs (emp_verify.xml): Individual ✅ 13/13 · Proprietorship ✅ 13/13 · Partnership ✅ 13/13 — all through FINAL SUBMIT with application number.

**Full `Emp_Arch.xml` run (2026-07-15, end of session): Tests run 88, Failures 9, Skipped 30 (mostly cascades).** What the failures actually are:

1. **TC_E05 duplicate-identifier negative FAILED on all 4 firms (Partnership/PvtLtd/PublicLtd/LLP)** — saving Application Details with Proprietorship's Firm Reg No + GST + PAN produced NO error: **UAT accepted the duplicates**. This looks like a real app defect (excel TC_AR_026/040/068/082 originally Fail, "PAN unique after confirmation by BA", later marked Pass 1/27-28 — regression?) OR duplicate-check fires at a later step. The subsequent TC_E06 failures ("Registered State ... input not visible") are pure cascade: the duplicate save unexpectedly SUCCEEDED, form state moved on, so re-filling failed. **Triage against the app/BA before touching the test code — the assertion itself is per spec.**
2. **TC_SN09 wrong-password login** — no error text captured; page only showed "Forgot Password?". Needs one live look at what the site actually shows on bad credentials (probably a toast/text ValidationUtils' keyword filter missed).

Everything else green in the full run too: SignupNegativeTests 8/9 with REAL captured messages ("Please enter valid 10-digit mobile number", "Enter valid email address", "This mobile number is already registered.", "This email is already registered."), Individual + Proprietorship 13/13 in-suite.

**Full-suite ordering matters:** SignupNegativeTests (creates dup-source user) → Individual → Proprietorship (stores firstFirm* in TestContext) → Partnership/PvtLtd/PublicLtd/LLP (duplicate negatives). TC_E05 skips in solo runs (TestContext empty — by design).

## 5. Open items / next steps (resume here)

1. **UAT went down at session end** — user will say when to resume.
2. Triage TC_E05: is duplicate GST/PAN/RegNo acceptance a real UAT defect? (Ask BA / check manually once env is up.) If the app validates duplicates elsewhere (e.g. on final submit), move the negative there.
3. TC_SN09: capture the real wrong-password error (one investigation run: bad login → dump visible text/toasts).
4. LLP/PvtLtd/PublicLtd positive path: never completed a clean TC_E06→TC_E13 leg (cascade after TC_E05) — expected to pass once item 2 is resolved since Partnership's identical shape passed solo; verify with one full-suite rerun.
5. Application-number capture is loose (`\D` strip gave "1"/"123") — tighten once the real popup format is known.
6. Post-final-submit flow (CA review/approve/revert, payment) — **user will instruct later**; do not start on your own.
7. Detailed per-TC expansion (edit-before-submit TC_AR_020, doc edit/delete TC_AR_016, COA re-upload bug TC_AR_028, etc.) — after flows stay green.
8. Old DataProvider files (`TC_002_SignUp`, `TC_003_SignIn`, `SignupDataProvider`, `SignUpDataArch`) — ask user before deleting.

## 6.5 Session 2026-07-16 (evening): TC-count expansion STARTED, then HELD on user instruction — resume here

User's larger instruction (this drives the next sessions): **empanelment ~200 TCs, land ~200 TCs, Legacy Land Part 2 ~50 TCs** (spec: `future/TC_Context.md` + `testData/tc_context/*`), assertion on every TC, no DataProviders, unusual files/code removed, clean naming. Emp part was in progress when user said "emp ko hold karo, context save karo, next session me start karenge".

**DONE this session (compile-verified `mvn test-compile` OK — NOT yet run live):**
1. **DataProvider purge complete:** deleted `TC_002_SignUp.java`, `TC_003_SignIn.java`, `testDataProviders/` (SignupDataProvider), `testData/` java pkg (SignUpDataArch), root `emp_*.html` DOM dumps. All suite XMLs fixed to the current classes (`Emp_Arch.xml`, `Emp_Arch-v2.xml`, `MPHIDB.xml`, `MPHIDB-v2.xml`, `Arch_workflow-v2.xml` — the 4 deleted-Land-class refs in MPHIDB*/land-v2/Arch_workflow were also fixed, plain `mvn test` no longer aborts).
2. **`EmpanelmentFlowBase` 13 → 21 chained steps** (per type; 21×6=126 executed). New steps: TC_E03 dashboard GUI (TC_AR_010), TC_E04 all-5-tabs assert (TC_AR_011), TC_E06 invalid-PAN negative (TC_AR_013), TC_E09 data-persists-after-tab-revisit (TC_AR_020) via new `Application_Details.getArchitectNameValue()`, TC_E11 post-grad-optional assert, TC_E14 document-visible-in-list, TC_E17 project-visible-in-table, TC_E18/E19 = old E12 split (shareholder presence vs other-details save), TC_E20 final-Submit-disabled-without-consent negative (new `Acknowledgement.isFinalSubmitEnabled()`), TC_E21 submit-enabled assert + appNo fallback tightened to `\d{4,}` regex. **Positive saves now disambiguate success-vs-error popups via `ValidationUtils.looksLikeError(popupText)`** — fixes the masking bug where an error swal was dismissed and the step still went green.
3. **`SignupNegativeTests` 9 → 22** (SN10-22: alphabetic/9-digit mobile, no-domain/space email, wrong mobile/email OTP (known past app defect - may fail legitimately), empty username/password, wrong captcha, unregistered user, login/register page GUI positives, correct-login positive). `ErrorMszUtil` usage replaced with AlertUtils+ValidationUtils.
4. **Utilities/page objects:** `RandomUtils.generateMobile()` fixed (old code produced invalid 9-digit mobiles in recurring windows, next ~2026-09-22); `ValidationUtils.looksLikeError()` public; `Manage_Empanelment` tab-visibility getters added, dead `addNewButton`/`clickAddNew` removed; `Application_Details` dead state/district/registered-state fields removed.

**Current executed count:** 126 (flows) + 22 (negatives) + 1 (LaunchUrl) ≈ **149** vs target ~200.

**NEXT SESSION start here:**
1. `TC_001_LaunchUrl` expansion to 4 GUI TCs was drafted but user held it — re-do if wanted (welcome msg, title, login entry, register entry).
2. Live-verify the new 21-step chain: `emp_verify.xml` → IndividualEmpanelmentFlow solo first, then full `Emp_Arch.xml`. Watch: TC_E06 (app may ACCEPT bad PAN → app defect or move the negative), TC_E20 (submit may not be disabled → per-spec failure), TC_SN14/15 (wrong-OTP known defect), TC_E09 (architect-name persistence assumes field reloads saved value).
3. To reach ~200: more granular negatives (GST format, turnover numeric, qualification percentage bounds), or CA-review/payment flows (user will instruct - §5 item 6).
4. Then: land TC expansion ~200 (LandFlowBase design in session notes) + Legacy Land ~50 (`legacy_land_functional_tc.json`).
5. Old items still open: TC_E05 duplicate-acceptance triage (§5.2), TC_SN09 wrong-password message (§5.3).

## 6. How to run

```
# one class:  edit emp_verify.xml's <class>, then
mvn -f pom.xml test -DsuiteXmlFile=emp_verify.xml -DopenReport=false
# full suite (~35-45 min, 8 browsers serially):
mvn -f pom.xml test -DsuiteXmlFile=Emp_Arch.xml -DopenReport=false
```
Investigation-script pattern (same as Land §2): `mvn dependency:build-classpath` → `javac/java -cp` from project root; scripts in session scratchpad (not persisted) — recreate from this doc.
