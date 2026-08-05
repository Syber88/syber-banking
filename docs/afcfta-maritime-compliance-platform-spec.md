# AfCFTA Maritime Compliance Pre-check Platform

**Status:** Product and technical specification (draft 0.1)  
**Jurisdiction for MVP:** South Africa; commercial sea-freight imports and exports  
**Research checked:** 31 July 2026  
**Working name:** TradeReady

## 1. Product decision

Build a document *pre-check* service for traders, freight forwarders and clearing agents. A customer uploads a proposed clearance pack before lodging it with SARS or another authority. The service extracts data, checks it against a versioned rules library, compares documents for inconsistencies, and produces an evidence-linked findings report.

The service must not submit declarations, sign or authenticate certificates, amend customer documents, calculate a final duty liability, represent a customer to SARS, or promise clearance. It is a decision-support and document-quality product, not a customs-clearing or legal-advice service.

This is a sensible starting point because SARS requires declarations to be accurate and treats them as binding legal statements. SARS itself remains the release authority and may request documents, inspect goods, or detain a consignment. [SARS Goods Declaration policy](https://www.sars.gov.za/sc-cf-55-goods-declaration-external-policy/)

## 2. Problem and opportunity

Sea-freight clearance packs contain repeated, interdependent information: parties, goods descriptions, quantities, values, HS codes, origin, transport references, procedure codes and permits. A missing field, a conflict between an invoice and bill of lading, an expired proof of origin, or an unrecognised permit can create avoidable rework before clearance.

For imports, SARS describes the clearance check as comparing a declaration with the invoice, bill of lading, certificate of origin and permits; it may ask for more information, samples, or other-agency compliance. [SARS import process](https://www.sars.gov.za/customs-and-excise/import-export-and-transit/imports/)

AfCFTA makes origin evidence especially important. South Africa states that trade under approved tariff offers began on 31 January 2024 with Algeria, Cameroon, Egypt, Ghana, Kenya, Rwanda and Tunisia; trade with SADC countries uses the SADC Trade Protocol. [SARS AfCFTA automation notice](https://www.sars.gov.za/latest-news/automation-of-customs-excise-african-continental-free-trade-area-afcfta-agreement/)

## 3. Regulatory baseline and product implications

| Regulatory fact | Product implication |
| --- | --- |
| Importers/exporters normally lodge a Goods Declaration; they or a registered/clearing agent submit it. | Do not lodge declarations in MVP. Use an exportable checklist and machine-readable findings only. |
| The declaration must be lodged before export, and commercial goods use designated ports. | Display deadline/port prompts as warnings, never clearance approval. |
| Declaration fields include transport-document number; a booking reference is not a substitute. | Cross-check the declared transport reference against the bill of lading and flag booking-reference patterns. |
| Trade-agreement exports require origin checks and Customs authentication where applicable. | Treat AfCFTA origin verification as an evidence checklist, not an origin determination. |
| AfCFTA proof may be a certificate, origin declaration, or supplier/producer declaration; proof is generally valid for 12 months. | Validate presence, completion, date window, serial/reference and consistency, then require a human confirmation for substantive origin claims. |
| Restricted goods may require permits, certificates or letters from an administering authority. | Rules library must map HS code + direction + procedure to required permits and label every match as “verify with issuing authority.” |

The first four facts are stated in the current [SARS Goods Declaration policy](https://www.sars.gov.za/sc-cf-55-goods-declaration-external-policy/). AfCFTA origin evidence and its 12-month validity are set out in South Africa's published AfCFTA customs rules. [Government Gazette notice R1432](https://www.sars.gov.za/wp-content/uploads/Legal/SecLegis/LSec-CE-RA-2020-19-Notice-R1432-GG-44049-AfCFTA-31-December-2020.pdf)

SAMSA separately requires pre-arrival notification information for internationally trading ships bound for South African ports. This is a distinct vessel-level workflow; do not claim that cargo-document checking fulfils vessel clearance. [SAMSA Marine Notice 25 of 2020](https://www.samsa.org.za/api/api/File/view/dZy%2FpRGTbLIp4238LYHtOg%3D%3D)

## 4. Users and jobs to be done

- **Exporter/importer:** check a small shipment pack before sending it to a clearing agent.
- **Freight forwarder:** identify documentary gaps and inconsistent data across high-volume shipment files.
- **Clearing agent:** use a client-preparation report; retain full responsibility for declaration and submission.
- **Compliance administrator:** maintain the organisation's approved templates, internal controls and evidence trail.
- **Internal regulatory analyst:** proposes and tests rule updates; cannot silently publish them.

## 5. MVP scope

### In scope

1. Organisation accounts, roles and shipment workspaces.
2. Upload PDF, image, XLSX and CSV documents, with malware scanning and OCR.
3. Document classification: commercial invoice, packing list, bill of lading, certificate/proof of origin, permit, SAD 500 draft, and “other.”
4. Structured field extraction with source-page citations and user correction.
5. Cross-document consistency rules for: importer/exporter/consignee, invoice number/date, goods description, package count, gross weight, quantity/unit, currency/value, country of origin, HS code, container/seal and transport-document number.
6. Rules-based checks for required documents, basic completion, date validity, AfCFTA evidence presence and listed restricted-goods permit prompts.
7. A downloadable, immutable findings report showing rule source, rule version, severity, rationale, source evidence and recommended next action.
8. Human review and “mark resolved / accepted risk” workflow with actor and timestamp.

### Explicitly out of scope for MVP

- SARS eFiling/EDI integration, submission, payment, customs-status polling or filing on a client’s behalf.
- Auto-completion, correction, signing, stamping or issuing of a declaration, certificate of origin or permit.
- Tariff classification, origin determination, valuation advice, duty/VAT determination or legal advice.
- Vessel clearance, crew/passenger reporting, port-call scheduling, dangerous-goods management or direct SAMSA/MRCC submission.
- Any claim that a pass predicts release or prevents inspection/detention.

## 6. Customer workflow

1. User creates a shipment: direction, transport mode, port, origin/destination, procedure intent, trade preference claimed and commodity details.
2. User uploads the document pack and confirms consent to process the documents.
3. System scans, classifies and extracts fields; user resolves low-confidence fields.
4. Rules engine evaluates the declared scenario and extracted evidence.
5. User views findings by severity and document, follows the recommendation, and re-uploads corrected originals where needed.
6. User exports the report and optionally shares a read-only link with a clearing agent.
7. The report records that it is a pre-check and does not replace an official declaration, inspection, certificate, permit or clearance decision.

## 7. Findings model

| Severity | Meaning | Example |
| --- | --- | --- |
| Blocker | Pack cannot be assessed for the selected scenario. | AfCFTA preference claimed but no proof of origin uploaded. |
| High | Material conflict or likely mandatory evidence gap. | Transport document number is absent or invoice and bill of lading disagree on consignee. |
| Medium | Information needs review or may require authority action. | HS code matches a restricted-goods rule; relevant permit was not found. |
| Low | Quality/completeness concern. | OCR cannot read the invoice date confidently. |
| Info | Guidance only. | Certificate evidence should be checked against the importing State Party's requirements. |

No severity may be worded as “approved,” “compliant,” “cleared,” or “guaranteed.” A completed check is “no findings detected under rule set X at time Y”; it is not a legal conclusion.

## 8. Rules-engine requirements

Each rule is data, not hard-coded logic, and has: `id`, jurisdiction, direction, mode, procedure, conditions, authority/source URL, effective-from/to, review date, version, test cases, owner and approval record. Rules must return `PASS`, `FINDING`, `NOT_APPLICABLE`, or `INSUFFICIENT_DATA`.

Initial rule families:

- Pack completeness by shipment type and claimed preference.
- Field format and mandatory-field validation.
- Cross-document equality/tolerance checks.
- Date and document-reference checks.
- AfCFTA proof-of-origin checklist and 12-month date check.
- Registration/status prompt for AfCFTA exporter, producer and approved exporter workflows.
- Restricted/prohibited goods prompt by tariff code and direction.
- Evidence-retention prompt.

Every automated check must expose: the extracted input, its page/box source, the rule version and official source. Ambiguous extraction or legal interpretation must become `INSUFFICIENT_DATA` and route to user review.

## 9. Architecture

```text
Browser / API
    -> Identity & organisation service
    -> Encrypted document store
    -> AV scan -> OCR/classification -> extraction + confidence
    -> Normalised shipment/document data
    -> Versioned rules engine <-> reviewed regulatory-content repository
    -> Findings, audit trail and PDF/JSON report service
```

Recommended MVP stack: Spring Boot REST API (aligned with this Java workspace), PostgreSQL for transactional data and audit events, encrypted object storage for documents, a queued OCR pipeline, and a separate rules/content module. Use an OCR provider only under a data-processing agreement and retain a manual-entry fallback.

### Core entities

- `Organisation`, `User`, `Role`, `Subscription`
- `Shipment`, `ShipmentParty`, `Commodity`, `ClaimedPreference`
- `Document`, `DocumentVersion`, `ExtractedField`, `EvidenceAnchor`
- `Rule`, `RuleVersion`, `RuleRun`, `Finding`, `FindingResolution`
- `Report`, `ShareLink`, `AuditEvent`, `RetentionPolicy`

## 10. Security, privacy and audit

Trade documents contain commercially sensitive data and personal data (names, contact details and sometimes IDs). Design for POPIA from day one: purpose limitation, minimal collection, role-based access, encryption in transit/at rest, customer-controlled access, access logs, configurable retention/deletion, breach response, and processor due diligence. Obtain specialist South African privacy counsel before launch.

The system must maintain a tamper-evident audit history of upload, extraction changes, rule version used, findings, user overrides, report generation and sharing. It must not reuse customer document content to train models unless the customer has made a separate, informed opt-in.

## 11. Commercial model

- **Free:** 10 checks/month, limited document types, 30-day retention, PDF report watermark.
- **Professional:** per-seat or per-organisation subscription; larger pack limits, team review, API/CSV intake and 12-month retention.
- **Enterprise:** volume pricing for forwarders/clearing agents; SSO, organisation rule overlays, dedicated storage region, audit export and SLA.

Pricing should be per completed shipment pack/check, with pooled allowances, rather than charging for every upload or re-check caused by OCR correction.

## 12. Success measures and guardrails

Measure time from upload to usable report, extraction accuracy after correction, findings resolved before submission, repeat checks per shipment, user adoption by forwarders, and false-positive/false-negative feedback. Do not claim reduced detention time until validated with a controlled customer study.

Launch guardrails:

- Regulatory analyst reviews every content release; lawyer/customs specialist approves substantive rule changes.
- Rules show an “effective as of” date and expire automatically without review.
- Start with a small, documented commodity and corridor set; do not represent coverage as universal.
- Place a visible non-reliance disclaimer on the upload flow and every report.

## 13. Delivery plan

**Phase 0 — 2–4 weeks: discovery and rule catalogue.** Interview 5–10 forwarders/clearing agents; select two South African sea-freight lanes and 5–10 document patterns; turn each official requirement into a traceable candidate rule and test pack.

**Phase 1 — 6–8 weeks: concierge MVP.** Secure upload, manual metadata entry, document checklist, rule runs, findings report and human analyst review. OCR may be optional. Validate whether the reports catch real rework before automation.

**Phase 2 — 8–12 weeks: assisted automation.** Add OCR/extraction, field comparison, confidence review, rule versioning, audit log and subscription enforcement.

**Phase 3: controlled integrations.** Only after legal/commercial review, consider read-only intake from transport-management systems or a clearing-agent workflow. Do not integrate with SARS for filing without explicit authority, security review and regulator-compatible terms.

## 14. MVP acceptance criteria

1. A user can upload a commercial invoice, packing list and bill of lading and correct extracted fields before a rule run.
2. The system flags a missing document required by the selected scenario and shows the supporting rule/source.
3. The system flags mismatched invoice and bill-of-lading values/parties with page-level evidence.
4. An AfCFTA preference claim without uploaded origin evidence produces a Blocker, not a pass/fail legal conclusion.
5. A suspected restricted tariff code creates a Medium finding and points to authority verification; the system does not determine permit validity.
6. A generated report names the ruleset version, timestamps the run, contains all unresolved findings and carries the disclaimer.
7. Every user override is attributed, timestamped and retained in the audit trail.

## 15. Open decisions before build

1. Which two sea-freight corridors and commodities should define the pilot?
2. Is the initial buyer a trader, freight forwarder or clearing agent? Their workflow and willingness to pay differ materially.
3. Which exact documents and declarations are included in the first ruleset?
4. Who is the named legal/customs content owner, and what is the weekly update/review process?
5. What document-residency, retention and deletion commitments will customers require?

## Sources

- African Union, [Agreement Establishing the African Continental Free Trade Area](https://au.int/en/treaty/agreement-establishing-african-continental-free-trade-area).
- SARS, [Automation of Customs & Excise AfCFTA agreement](https://www.sars.gov.za/latest-news/automation-of-customs-excise-african-continental-free-trade-area-afcfta-agreement/) (18 November 2025).
- SARS, [Goods Declaration policy SC-CF-55](https://www.sars.gov.za/sc-cf-55-goods-declaration-external-policy/) (effective 30 March 2026).
- SARS, [Imports: clearance process](https://www.sars.gov.za/customs-and-excise/import-export-and-transit/imports/).
- SARS, [Rules of Origin](https://www.sars.gov.za/customs-and-excise/rules-of-origin/).
- SARS, [Prohibited, restricted and counterfeit goods](https://www.sars.gov.za/customs-and-excise/prohibited-restricted-and-counterfeit-goods/).
- SARS, [AfCFTA rules / Government Gazette R1432](https://www.sars.gov.za/wp-content/uploads/Legal/SecLegis/LSec-CE-RA-2020-19-Notice-R1432-GG-44049-AfCFTA-31-December-2020.pdf).
- SAMSA, [Marine Notice 25 of 2020](https://www.samsa.org.za/api/api/File/view/dZy%2FpRGTbLIp4238LYHtOg%3D%3D).

This document is a product specification informed by public sources, not legal advice. Validate the ruleset and launch terms with a South African customs and maritime-law practitioner.
