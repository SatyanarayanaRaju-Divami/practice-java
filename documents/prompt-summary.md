# AI Prompt Summary — Family League

**AI Tool Used:** Claude Code (claude.ai/code) — Anthropic Claude Sonnet 4.6

This document lists the prompts submitted to the AI assistant during the development of this project. Each prompt maps to one or more architectural decisions recorded in [decision-log.md](./decision-log.md). Prompts are listed in approximate chronological order.

---

## 1. Project Bootstrapping

**Prompt:**
> I need to build a Spring Boot Java backend application called "Family League". It is a platform where family and friends can predict outcomes of an actual sports league, earn points for correct predictions, and compete on a leaderboard. There will be two user roles: Admin and User. I have attached the requirements document. Help me set up the project structure, choose the right Java and Spring Boot versions, and scaffold the initial Maven project with PostgreSQL, Flyway, Spring Security, and Spring Data JPA.


---

## 2. Database Choice

**Prompt:**
> The requirements say we can use either PostgreSQL or MySQL. Which one should we use and why? If we go with PostgreSQL, what specific advantages does it give us for this project?


---

## 3. Primary Key Strategy

**Prompt:**
> What should we use as primary keys for our entities — auto-increment longs or UUIDs? The API will expose these IDs to users, and we want to avoid leaking record counts. Walk me through the trade-offs and help me set it up in JPA.


---

## 4. Audit Fields and Soft Delete

**Prompt:**
> The requirements say all data changes must be captured as standard audit data and no records should be permanently deleted — only soft deleted. How should I implement this? I want it to be consistent across all entities without repeating the same fields everywhere.

---

## 5. Data Model — League vs Season

**Prompt:**
> The requirements say "Teams are independent of League season" and "Each team could play many leagues of the same name." Help me model the League and Season entities correctly. What is the right relationship between League, Season, Team, and Match?

---

## 6. Leaderboard Design

**Prompt:**
> The requirements mention two different kinds of leaderboards — one for real-world team positions in the league, and one for user prediction rankings. Should these be the same table or separate? How should I model them in the database?

---

## 7. Scoring Rules

**Prompt:**
> The requirements say "One Prediction adds one point to the user score in the league." Does this mean any submitted prediction earns a point, or only correct ones? Also, how do ties work — who gets the point? And how do we make sure points are never accepted via the API?

---

## 8. Prediction Lock Enforcement

**Prompt:**
> The requirements state that prediction lock must be enforced at the database level, not just in application code. Match predictions close 1 hour before match time and league predictions close 4 hours before the first match. How do I enforce this at the DB level in PostgreSQL? Can I use a CHECK constraint, or do I need something else?

---

## 9. Async Scoring Engine

**Prompt:**
> After an admin publishes a match result, the system needs to recalculate scores and update the leaderboard, then notify the admin by email. The requirements say this must be done asynchronously. How should I implement this in Spring Boot? What happens if the async job fails?
---

## 10. Package Structure — Player

**Prompt:**
> Where should the Player entity live? Should it be nested inside the team package since a player belongs to a team, or should it have its own top-level package? Players can transfer between teams across seasons.

---

## 11. Package Structure — Season

**Prompt:**
> Should the Season entity have its own package or live inside the league package? It seems tightly coupled to League since a season cannot exist without one.

---

## 12. Audit FK vs Raw UUID

**Prompt:**
> For audit fields like `published_by`, `deleted_by`, and `created_by` that reference users — should these be actual foreign keys to the users table, or just plain UUID columns? What are the trade-offs?

---

## 13. First Admin Bootstrap

**Prompt:**
> There is no public "register as admin" endpoint — that would be a security risk. How does the first admin account get created? Should I seed it in a Flyway migration? How do I handle the credentials without hardcoding them?

---

## 14. App Config Design

**Prompt:**
> I need a configuration table to store runtime settings like prediction cutoff durations (e.g., 1 hour before match for match predictions, 4 hours for league predictions). These should be configurable from the database without a deployment. Should this table extend BaseEntity with full audit fields, or is it a special case?

---

## 15. API Versioning

**Prompt:**
> Should I version the API from the start? What base path should I use for all endpoints? I want to be able to introduce breaking changes in future without disrupting existing clients.

---

## 16. Response Envelope

**Prompt:**
> The requirements say we need consistent API agreements for request and response data structures. How should I structure API responses so that both success and error cases are handled uniformly by any client? I want the error response to include field-level validation errors as well.

---

## 17. Dependency Injection Style

**Prompt:**
> Should I use field injection with `@Autowired` or constructor injection for Spring beans? What does Spring itself recommend and why?

---

## 18. Transaction Management

**Prompt:**
> Where should I put `@Transactional` annotations — on controllers, services, or repositories? What is the correct layer to own transaction boundaries in a Spring Boot application?

---

## 19. Enum vs Lookup Tables

**Prompt:**
> For status fields like `SeasonStatus`, `MatchStatus`, `UserRole`, and `EmailStatus` — should I use Java enums stored as strings with CHECK constraints in PostgreSQL, or should I create separate lookup/reference tables for each? What are the trade-offs?

---

## 20. JWT Authentication Setup

**Prompt:**
> I need to implement JWT-based authentication with Spring Security 6. There should be two roles: ADMIN and USER. How do I set up the JWT filter chain, token generation, and role-based endpoint protection? The admin creation endpoint should only be accessible by an existing admin.

---

## 21. Folder Structure and Layering

**Prompt:**
> Review the current package structure. I want each feature domain (league, match, player, prediction, scoring, notification) to have its own sub-packages for controller, service, repository, and dto. Refactor the structure to follow this layer-based sub-directory pattern consistently.

---

## 22. Password Validation

**Prompt:**
> I want to add a custom password validation annotation that enforces minimum length, uppercase, lowercase, digit, and special character requirements. How do I implement a custom `ConstraintValidator` in Spring Boot?

---

*End of prompt summary.*
