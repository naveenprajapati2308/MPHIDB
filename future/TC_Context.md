# MPHIDB Test Case Context — for Automation Scripting (Validations & Assertions)

**Purpose:** Single reference for all documented manual test cases (module + integration) so automation scripts can reuse the same validation/assertion logic. This is the **minimum** TC set — automation may cover more.
**Generated:** 2026-07-14 · **Prepared with:** Arvind Kurmi
**Machine-readable data:** `testData/tc_context/*.json` (+ raw module TC CSVs in `testData/tc_context/module_tc/`)

**Source documents (master copies, keep in sync):**
| File | Content |
|---|---|
| `g:\MPHB\integration_tc.xlsx` | Cross-Module Integration TCs — INT_001–056 + INT_LG_001–017 merged inline (Legacy = Part 2 of Land Module), RTM with 9 flows |
| `g:\MPHB\MPHIDB TC  RTM.xlsx` | Module TCs: Land Management (LM_001–199), Architect Empanelment (TC_AR_001–…), Competition, **Legacy Land (LG_001–021)**, RTM |
| `g:\MPHB\E2E  flow across the module.xlsx` | Flow diagrams: Empanelment, Architect Workflow (In-House + Competition), Land Module (Part 1 + Part 2 Legacy), Integration E2E swimlane |

---

## 1. Application map (roles & modules)

- **Authority Login** (one login, role-based behavior): Executive Engineer (EE), Chief Architect (CA), Assistant Architect (AA), In-House Architect, Draftsman, DHC.
- **CMS Login** (separate auth): citizen/architect users — Empanelment + Competition participation.
- **Modules:** 1) Land Module → 2) Architect Workflow (In-House / Competition) → 3) CMS Empanelment. Business-dependent: output of one module is input of the next; **same Land ID must flow through everything**.

## 2. TC inventory (minimum automation targets)

| Suite / Sheet | ID range | Count | Focus |
|---|---|---|---|
| Cross-Module Integration (integration_tc.xlsx) | INT_001–INT_056 | 56 | E2E data/ownership/document/status transfer between modules |
| Legacy Land Integration (same sheet, Part 2, after INT_007) | INT_LG_001–INT_LG_017 | 17 | Migrated-land flow EE→CA |
| Land Management (module) | LM_001–LM_199 | ~199 | 8 land types × create/khasra/update/docs (Redevelopment LM_196–199 = future scope, currently skipped) |
| Architect Empanelment (module) | TC_AR_001–… | ~100+ | Registration/OTP/login + Individual & 5 firm types (Proprietorship, Partnership, Pvt. Ltd., Public Ltd., LLP) + CA review + payment |
| Competition (module) | story-based | ~40 | Publish, apply windows, winner selection, LoA→WO→Agreement chain |
| Legacy Land (module, functional) | LG_001–LG_021 | 21 | Advanced Filter, NA defaults, validations, doc rule, forward |

**Integration RTM flows:** INT-FLOW-1 Land→Architect (INT_001–007) · 2 In-House (008–020) · 3 Reverse+Final (021–026) · 4 Competition create/publish (027–031) · 5 CMS Empanelment (032–038) · 6 Competition applications (039–044) · 7 Competition workflow CA→DHC→EE→winner (045–050) · 8 Reverse+Final both modules (051–056) · 9 **Legacy Land (INT_LG_001–017)**.

## 3. Assertion cheat-sheet (the rules automation must verify)

### Land Module — Part 1 (New Land)
- Land ID generated with confirmation popup; status badge Draft → **"Land Id Created"** after forward; Land Id column gets real value (e.g. `0801/NARSINGHPUR/C/0093`).
- Mandatory document validation **APPLIED** (PDF, max 10 MB; error on invalid type/size).
- After forward to CA: EE loses edit (read-only/history), ownership = CA, audit log has from-user/to-user/timestamp/remarks.

### Land Module — Part 2 (Legacy Land) ⚠ different rules
- Entry: Land list → **Advanced Filter → Is Migrated Data = Yes** → only migrated records (each already has a Land ID).
- Defaults on unprocessed migrated record: **Land Type = NA**, **Last Modified Date = NA** (Last Modified flips to real date after first save).
- **NO new Land ID is ever created** — assert same ID before/after forward and across modules.
- Forward gate = exactly 2 validations: **Land Type not blank** AND **Khasra Area > 0 hectare**. Both fail → blocked; one fixed → still blocked; both pass → allowed.
- **Mandatory document validation NOT applied** (forward allowed without docs) — but Part 1 must still enforce docs (regression check both ways).
- At CA: uploaded docs visible AND missing docs listed too, with clear uploaded/non-uploaded distinction.

### Architect Workflow (In-House)
- Sub Land linked to correct parent; sub-land area validated against parent's available area (parent available area reduces).
- Chain CA → In-House Architect → AA → Draftsman; docs from every prior role remain accessible at each hop.
- **Draftsman can return ONLY to the assigning user (AA)** — no other targets offered.
- Reverse: AA → In-House Architect → CA, or AA → CA direct **only if permission configured**.
- After CA final approval: workflow completed, no further edit/forward, all docs still available.

### Competition + CMS
- Publish does **not** start workflow; competition becomes visible in CMS with synchronized dates.
- Apply window: blocked before start date, allowed within window, blocked after end date. CA can act on applications **only after end date**.
- Eligibility: only **Active** empanelled architects (approved + paid, valid COA at apply date); pending/reverted/expired blocked.
- Winner: exactly **one** (radio selection); only winner sees Assigned Competition / the post-selection workflow in CMS.
- Doc chain: CA LoA → DHC Work Order (status "Work Order Issued") → EE Agreement → winner uploads (allowed only after agreement attached) → returns **only to EE**; reverse EE→DHC→CA.
- Final: completion status reflected in **both** Authority and CMS modules; documents/history/ownership consistent E2E.

### Empanelment (CMS)
- Register button enabled only after mobile + email OTP both verified; credentials via SMS/email; forced password change on first login.
- Type: Individual or Firm (5 firm types); Firm Reg. No / GST / PAN must be **unique**; type changeable before submission / on revert (TC_AR_092).
- Revert loop: CA revert with remarks → architect edits → resubmits. Approve → payment (select years) → **Active** + validity assigned.

## 4. Data files for scripting

- `testData/tc_context/integration_tc_context.json` — all 73 integration TCs: `{id, suite, section, sub_section, description, steps[], expected[]}` + RTM flow map. Use `expected[]` lines as assertion text sources.
- `testData/tc_context/legacy_land_functional_tc.json` — LG_001–021 functional legacy TCs, same shape.
- `testData/tc_context/module_tc/*.csv` — raw module TC exports (Land, Empanelment, Competition, RTM) from the senior's workbook.

## 5. Suite mapping hints

| TestNG XML | Covers | Relevant TC context |
|---|---|---|
| `land.xml` / `land-v2.xml` | Land Module Part 1 (currently Government Land type) | LM_046–068, INT_001–007 |
| *(future)* legacy suite | Land Module Part 2 | LG_001–021, INT_LG_001–017 |
| `Emp_Arch.xml` / `Emp_Arch-v2.xml` | CMS Empanelment | TC_AR_*, INT_032–038 |
| `Arch_workflow-v2.xml` | Architect Workflow (In-House/Competition) | INT_008–031, INT_039–056 |

**Open items:** Redevelopment land type (LM_196–199) is documentation-only for now — user will instruct when to activate. Legacy Land automation suite not yet created; this context is the spec for it.
