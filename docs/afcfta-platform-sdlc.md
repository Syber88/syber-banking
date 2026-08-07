# Software Development Life Cycle: AfCFTA Maritime Compliance Platform

**Applies to:** TradeReady pre-submission compliance platform  
**Lifecycle model:** Iterative delivery with regulated-release gates  
**Scope:** South African sea-freight MVP; document pre-checks only

## 1. Purpose and operating principles

The platform helps customers find documentary inconsistencies before they submit to official authorities. It does not lodge declarations, issue certificates, make legal determinations, or guarantee customs clearance.

The team delivers in short iterations, but no release may bypass the regulatory-content, privacy, security, quality, or operational-readiness gates below. Product functionality and regulatory rules are released independently so that a rule change can be reviewed, tested, traced and rolled back without redeploying the application.

## 2. Roles and accountability

| Role | Accountable for |
| --- | --- |
| Product owner | Customer problem, scope, priorities, acceptance and commercial decisions |
| Regulatory content owner | Rules catalogue, source traceability, review dates and change proposals |
| Customs/maritime legal reviewer | Approval of substantive legal/regulatory interpretations and customer wording |
| Engineering lead | Architecture, delivery quality, technical risk and release recommendation |
| Developers | Secure implementation, unit tests, peer review and documentation |
| QA lead | Test strategy, evidence, regression suite and release quality report |
| Security/privacy lead | Threat modelling, POPIA controls, risk treatment and incident readiness |
| Operations/support owner | Monitoring, backup/restore, support runbook and customer communication |

No individual may approve their own regulatory rule change for production.

## 3. Lifecycle

```text
Discover -> Define -> Design -> Build -> Verify -> Release -> Operate -> Learn
     ^                                                              |
     +-------------------- feedback and regulatory change ----------+
```

### Phase 1 — Discover

**Objective:** prove there is a focused customer problem worth solving.

- Interview importers/exporters, freight forwarders and clearing agents.
- Map the current document-pack workflow and its rework points.
- Select a narrow pilot: two sea-freight corridors, 5–10 document patterns and a limited commodity set.
- Identify authoritative sources and validate who owns each compliance decision.
- Record assumptions, risks, desired outcomes and baseline metrics.

**Exit criteria:** product owner approves a problem statement, pilot cohort, measurable hypothesis and explicit out-of-scope list.

### Phase 2 — Define

**Objective:** turn the selected workflow into implementable, testable requirements.

- Create user stories with acceptance criteria and error states.
- Specify data fields, document types, severity definitions and report wording.
- Produce a rule catalogue: source, jurisdiction, applicability, effective date, owner and tests for every rule.
- Create a data inventory and retention schedule; identify POPIA risks and processors.
- Define the non-reliance disclaimer: no “approved,” “cleared,” “compliant,” or guarantee language.

**Exit criteria:** approved product requirements document, prioritised backlog, data/privacy assessment and traceable rule catalogue.

### Phase 3 — Design

**Objective:** choose a secure, maintainable solution before implementation.

- Design the architecture, API contracts, database model and document storage model.
- Model threats: unauthorised document access, tenant isolation failure, malicious uploads, OCR prompt/data leakage, rule tampering, audit-log alteration and report-link sharing.
- Define access roles, encryption, audit events, backup/restore, deletion and incident response.
- Prototype the customer flow: upload, extraction review, findings, resolution and report export.
- Design rule versioning, approval workflow, test fixtures and rollback.

**Exit criteria:** architecture decision record, approved threat model, UX prototype, test strategy and operations plan.

### Phase 4 — Build

**Objective:** implement small, independently testable increments.

- Work in short iterations (normally two weeks) from a prioritised backlog.
- Use peer review for every production change.
- Keep secrets out of source control; use environment-based secret management.
- Add unit, API/integration and end-to-end tests with each feature.
- Store rules as versioned content/data, never as untraceable free text or hard-coded legal assertions.
- Generate a software bill of materials and scan dependencies in CI.

**Minimum definition of done:** code reviewed; tests added and passing; logs/audit events included; documentation updated; accessibility and error paths checked; no unresolved critical/high security issue.

### Phase 5 — Verify

**Objective:** demonstrate that the product behaves safely and accurately for the supported scope.

| Test area | Required evidence |
| --- | --- |
| Unit tests | Field parsing, tolerances, rule conditions and severity selection |
| Integration tests | Upload, malware scanning, storage, OCR queue, extraction and report generation |
| Rules tests | Positive, negative, boundary and obsolete-rule fixtures for every rule version |
| End-to-end tests | Upload-to-report workflow and reviewer resolution workflow |
| Security tests | Dependency/vulnerability scans, authorisation/tenant-isolation tests and upload-abuse tests |
| Privacy tests | Access logging, deletion, retention and export requests |
| Performance tests | Target pack volume, OCR timeouts and degraded-provider behaviour |
| User acceptance testing | Pilot users validate findings, wording and workflow usefulness |

Regulatory validation is a separate activity from functional testing: the content owner and legal reviewer must confirm that a rule accurately reflects its cited source and that the report does not overstate its conclusion.

**Exit criteria:** all planned tests pass; no open critical/high defects; UAT sign-off; rule-validation record; privacy/security release review complete.

### Phase 6 — Release

**Objective:** deploy predictably and reversibly.

1. Confirm release notes, migration plan, monitoring dashboards, backup status and rollback plan.
2. Deploy first to staging and run smoke tests with representative, synthetic documents.
3. Obtain production sign-off from engineering, QA, security/privacy and product owner.
4. For a rules-only release, additionally require regulatory content owner and legal reviewer approval.
5. Deploy gradually: internal users, pilot customers, then wider availability.
6. Monitor errors, queue delay, rule findings rate and access anomalies; roll back if release thresholds are exceeded.

**Release artefacts:** version number, deployment log, approved change request, test report, SBOM/vulnerability result, rule version list, release notes and rollback record.

### Phase 7 — Operate and learn

**Objective:** keep the service dependable and current after launch.

- Monitor availability, document-processing latency, extraction confidence, errors, security events and backup success.
- Provide support triage with defined response targets.
- Review customer feedback and false-positive/false-negative reports weekly during pilot.
- Review regulatory sources at least monthly and immediately after a relevant official notice.
- Hold a post-incident review for security incidents, data loss, incorrect high-severity rule findings or material outages.
- Track outcomes: time to usable report, correction/rework rate, pilot retention and findings resolved before formal submission.

## 4. Regulatory rules change process

1. **Detect:** content owner monitors official SARS, SAMSA, DTIC/AU and other relevant authority publications.
2. **Assess:** record source URL, publication/effective dates, affected corridors/documents and risk.
3. **Draft:** create a new rule version with plain-language rationale and complete test fixtures.
4. **Review:** legal/customs reviewer verifies the interpretation; engineering verifies technical impact.
5. **Approve:** two-person approval by content owner and legal reviewer.
6. **Release:** publish with an effective date, customer-facing change note and automatic expiry/review date.
7. **Monitor:** inspect finding rates and customer feedback; roll back only to a previously approved version.

Emergency content changes follow the same two-person approval rule, with a retrospective review within one business day.

## 5. Environments and controls

| Environment | Purpose | Data policy |
| --- | --- | --- |
| Local development | Developer implementation and unit testing | Synthetic or irreversibly anonymised data only |
| Test/CI | Automated verification | Synthetic test packs only |
| Staging | Release rehearsal and UAT | Synthetic data by default; approved, minimised pilot data only when necessary |
| Production | Customer service | Encrypted customer data; least-privilege access and full audit trail |

Production access is time-bound, role-based and logged. Never use real customer documents in developer laptops, public issue trackers or model-training datasets.

## 6. Initial delivery roadmap

| Stage | Outcome | Indicative duration |
| --- | --- | --- |
| Discovery | Pilot scope, customer workflow and rule catalogue | 2–4 weeks |
| Concierge MVP | Upload, manual data capture, findings report and analyst review | 6–8 weeks |
| Assisted automation | OCR, cross-document comparison, versioned rules and audit trail | 8–12 weeks |
| Controlled pilot | 3–5 organisations, monitored feedback and rule tuning | 6–8 weeks |
| Scale decision | Expand coverage only if pilot metrics and controls pass | After pilot review |

Durations are planning assumptions, not commitments. Do not add SARS filing, certification or clearance integrations until legal, security and commercial authority are explicitly obtained.

## 7. Key quality gates summary

| Gate | Required approval |
| --- | --- |
| Problem/pilot scope | Product owner |
| Requirements and rule catalogue | Product owner + regulatory content owner |
| Architecture and privacy/security | Engineering lead + security/privacy lead |
| Rule interpretation | Regulatory content owner + legal/customs reviewer |
| Pilot/UAT | QA lead + product owner + pilot representative |
| Production feature release | Engineering lead + QA lead + security/privacy lead + product owner |
| Production rule release | Regulatory content owner + legal/customs reviewer + QA lead |

## 8. Artefacts to maintain

- Product requirements and prioritised backlog
- Rule catalogue, source register, change log and test fixtures
- Architecture decision records and API/data documentation
- Threat model, privacy impact/data inventory and retention schedule
- Test strategy, automated test reports and UAT evidence
- Release checklist, deployment/rollback record and incident runbook
- Support knowledge base and customer-facing disclaimers

This SDLC should be reviewed after the first pilot and at least every six months thereafter.
