# MPHIDB Automation Framework — Error / Outage Context
**Purpose:** Separate from `Framework_Context.md` on purpose (explicit user instruction: don't mix land-type build-out progress with site-outage/error-diagnostics tracking). This file is where "why did a live run fail" and "how do we surface the real reason" live.

---

## 1. Live-site outages observed this session (2026-07-10)

Two **different** failure modes were seen on the same QA site (`https://godavari.mp.gov.in/mphb_qa/...`) - do not conflate them in future debugging:

| # | Symptom | Where documented |
|---|---|---|
| 1 | Login succeeds ("Welcome!" banner renders), but the "Land Management" sidebar link never becomes clickable (20s timeout) | `Framework_Context.md` section 10/11, end of session |
| 2 | Browser never leaves the login page at all - a red **"Request failed with status code 404"** renders directly under the Login button, meaning the login API call itself isn't resolving | This session; screenshot at `screenShots/login only check/TC_001_*.png`; confirmed reproducible on 2 separate isolated runs (full Land Pulling suite, then login-only in isolation) |

Both are treated as live-site/backend issues, not framework bugs, until shown otherwise.

## 1b. Outage on 2026-07-16: QA came up, then went down again mid-session (HTTP 500)

QA was confirmed **up** at ~11:22 IST (login-only smoke 1/1 pass, 29s) and stayed up through a full Land Pulling verification run (finished 11:27, real test results — see `Framework_Context.md` §10). Then from ~11:29 onward, login started failing exactly like mode #2 above but with a **new status code: "Request failed with status code 500"** (was 404 on 2026-07-10) — red `text-danger` under the Login button, browser never leaves the login page. Confirmed via investigation-script DOM dump at the failure moment (`lp_0_login_FAILED.html`, URL still `.../manage/login`), reproduced 3 times in a row. User confirmed site down ("roko yrr fir se down he") and stopped work. Treat 500 as a third distinct symptom (backend erroring, not missing route) but the same triage rule applies: live-site issue, not a framework bug. The window between up and down was ~7 minutes of usable time — when QA comes back, get the high-value runs in early.

## 2. The real ask: surface the actual root cause, not just a Selenium exception

User's framing: when a live run hits something like a 404, some tests will simply error out and others will cascade-skip - that's expected and not interesting on its own. What's actually useful is whether the framework can catch and *show* the true underlying reason (e.g. "Request failed with status code 404") instead of a bare `TimeoutException: element not clickable`, which is technically true but tells you nothing about why.

## 3. What was built: `FailureDiagnostics`

`src/test/java/utilities/FailureDiagnostics.java` (new). `FailureDiagnostics.captureRootCause(WebDriver driver)` scans the live DOM at the moment of failure for visible on-page error text, in priority order:
1. SweetAlert2 popups (`.swal2-html-container`, `.swal2-title`)
2. Bootstrap alerts (`.alert-danger`, `.alert-warning`)
3. Toasts (`.toast-body`, `.Toastify__toast-body`)
4. `.invalid-feedback.d-block`
5. Anything classed `text-danger`
6. Fallback: a loose XPath substring scan for keywords (`failed`, `error`, `invalid`, `already`, `not found`, `denied`, `exception`, `unable`), with each candidate then re-checked against whole-word regex (`\berror\b` etc.) before being accepted.

The substring-then-regex two-step in the fallback exists because an early version had a real false positive: a benign line reading "no errors here" got flagged just because it contains the substring "error". Caught and fixed via a local, non-live-site test before this ever touched real data (see section 4).

Runs with no `WebDriverWait` (the calling test has already failed; driver state may already be shaky) and never throws - a failed diagnostic must never mask or replace the original test failure.

**Perf + accuracy fixes (2026-07-16):** (a) `captureRootCause` now zeroes the implicit wait during its scan and restores it in `finally` - previously each non-matching locator blocked the full 10s implicit wait, adding ~131s to EVERY failing test (live-measured: an 8-failure land suite dropped from 1082s to 117s after the fix); (b) texts of ≤2 chars are ignored - a bare `*` mandatory-field marker (text-danger span) was being reported as the root cause.

**KNOWN OPEN GAP (found in 2026-07-16 code review, NOT yet fixed):** v1 (`Master_extent_report.java`) resolves the failing test's `driver` via `getRealClass().getSuperclass().getDeclaredField("driver")` - ONE level only. The empanelment flow classes are `XxxEmpanelmentFlow → EmpanelmentFlowBase → CmsFlowBase` and `driver` lives two levels up in `CmsFlowBase`, so for every empanelment failure under v1 (`Emp_Arch.xml`, `emp_verify.xml`) the reflection silently returns null → NO screenshot and NO `captureRootCause` in the report. v2 walks the whole superclass chain and is fine. Fix when resuming emp work: copy v2's chain-walk into v1 (or extract a shared `resolveDriver(ITestResult)` helper both listeners call).

**Wiring:** there are exactly two `ITestListener` implementations in the project (`ReportManager/Master_extent_report.java` and `ReportManager/Master_extent_report_v2.java` - confirmed via `grep "implements ITestListener"`, nothing else exists). User's explicit instruction: apply this in both, not just one - done. Both `onTestFailure(ITestResult result)` methods (previously only logged `result.getThrowable()` + a screenshot) now also call `FailureDiagnostics.captureRootCause(driver)` right after the screenshot capture. If it finds something, they add `test.log(Status.FAIL, "🔎 Actual reason shown on page: " + rootCause)` to the Extent HTML report and print `[FailureDiagnostics] <testName> -> <cause>` to console. v2's version reuses the same `activeDriver` it already resolves for its screenshot call (a superclass-chain walk, slightly more robust than v1's single-level reflection lookup). This is in the shared suite-level listeners, so it applies to **every** test case in every module automatically - not just login, and not just whichever suite XML happens to be running.

## 4. Verification history

**First pass (local mock, no live site needed):** a throwaway `InvestigateFailureDiagnostics.java` launched headless Chrome against a local HTML file mimicking the real screenshot (a `text-danger` div reading "Request failed with status code 404") and separately against a clean page reading "Login page, no errors here." Initial version correctly caught the real error but also false-positived on the clean page. Fixed with the regex word-boundary confirmation step (section 3); re-run confirmed both cases correct: `CAPTURED_ROOT_CAUSE=[Request failed with status code 404]`, `CLEAN_PAGE_RESULT=[null]`.

**Second pass (live site, real failure) - 2026-07-10, same session, user's explicit push-back on waiting:** user's point was that since the site is down *right now* and any run will fail anyway, this is the ideal moment to prove the feature against a genuine live failure rather than waiting for the site to recover - correct call, acted on immediately instead of deferring. Ran `login.java` alone via `land_verify.xml` against the live (down) site. Confirmed working on the real failure, not just the mock:

```
[FailureDiagnostics] TC_001 -> Request failed with status code 404
```

Same result also landed in the Extent report (`reports/MasterReport2.html`) as `🔎 Actual reason shown on page: Request failed with status code 404` under `TC_001`. This is a real, live-site-confirmed result - the feature is proven working end-to-end, not just against the local mock in the first pass.

**Third pass (live site, the 500 outage) - 2026-07-16:** during the §1b outage, user asked to make sure an unknowing run would still surface the real reason. Ran login-only via `land_verify.xml` against the down site: console printed `[FailureDiagnostics] TC_001 -> Request failed with status code 500` and `reports/MasterReport2.html` logged `🔎 Actual reason shown on page: Request failed with status code 500`. So the feature is now live-proven for both outage symptoms (404 and 500) with zero code changes needed - the `.text-danger` scan covers both.

## 5. Portal integration also gets the real reason now (was missing it entirely)

User's follow-up question, and it was a real gap: when a run is triggered *from the portal* (not directly via `mvn`), does it just get a generic pass/fail/partial status with the raw Selenium exception, or does it also get the real root cause? Checked `utilities/PortalApiClient.java` - confirmed the gap was real:
- `pushTestFailed(ITestResult result, String screenshotPath)` was sending `exceptionType`/`exceptionMessage`/`stackTrace`/`screenshotPath` only - the same generic Selenium exception info, nothing from `FailureDiagnostics`.
- `pushSuiteCompleted` only sends a suite-level `exitStatus` of `"PASSED"` or `"PARTIAL"` - no per-test detail at all.
- Only `Master_extent_report_v2.java` calls into `PortalApiClient` (confirmed via `grep pushTestFailed` - one call site). v1 (`Master_extent_report.java`) has no portal integration at all, so this gap only applied to v2-driven runs.

**Fix:** added an overload `PortalApiClient.pushTestFailed(ITestResult result, String screenshotPath, String rootCause)` (old 2-arg version kept, just delegates with `null`) that includes a new `"actualReason"` JSON field in the `TEST_FAILED` event payload whenever `rootCause` is non-empty - omitted entirely otherwise, so the portal isn't sent a misleading "no root cause" signal for a genuinely unread failure. `Master_extent_report_v2.onTestFailure` now passes the `rootCause` it already computed from `FailureDiagnostics.captureRootCause(activeDriver)` straight through. Compiles clean (`mvn test-compile`). Not yet re-verified against a real portal endpoint (no `portal.url`/`execution.id` configured in this environment as far as investigated this session - `PortalApiClient.pushEvent` silently no-ops and returns early if those aren't set, so this can't accidentally spam a real portal by mistake) - next time a real portal-triggered run happens, confirm the `TEST_FAILED` event's JSON body actually contains `"actualReason"` on a failure that has one.

## 6. How to re-verify this feature later

Run any live suite while the site is unhealthy (or force a failure): `mvn -f pom.xml test -DsuiteXmlFile=land_verify.xml -DopenReport=false`, then check the console tail for a `[FailureDiagnostics] ... -> ...` line, or open `reports/MasterReport2.html` and look for the `🔎 Actual reason shown on page:` log line under the failed test. If a real failure's on-page error text doesn't get caught, extend the selector list or fallback keywords in `FailureDiagnostics.java` - it's intentionally centralized there rather than duplicated per page object.

## 7. Scope confirmed, work paused here (2026-07-10, same session)

User asked to confirm the whole `FailureDiagnostics`/`actualReason` mechanism (sections 3-5) only fires on the FAIL path - confirmed yes: it's wired solely into `onTestFailure()` in both `Master_extent_report.java` and `Master_extent_report_v2.java`, and `rootCause` is only ever passed into `PortalApiClient.pushTestFailed()`. `onTestSuccess`/`onTestSkipped` are untouched in both listeners - nothing to explain on a pass or a dependency-skip, so no root-cause logic runs there.

**User's explicit instruction: no further code changes until told to proceed.** This session's error-diagnostics work (sections 3-5) is considered done-for-now and parked here; live-site verification work (Land Pulling / Private Land / Redensification for Government, tracked in `Framework_Context.md` section 10) is separately blocked on the QA site being down (section 1). Next session: don't restart coding on this feature on your own initiative - wait for the user to say "karte hain" / explicitly resume it.
