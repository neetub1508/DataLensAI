# Product Requirements Document (PRD) – Data Lens AI
**Version:** 0.2 (Delta from v0.1)
**Date:** 3 August 2025

---

## Overview
This document lists only the new or changed requirements and features introduced in version 0.2 of Data Lens AI. For all other requirements, refer to PRD v0.1.

---

## 1. New Functional Requirements

### 1.1 Public Pages and Navigation
- The home page (before login) will display navigation links: Home, Pricing, About Us, Blog, Sign In, and Get Data Lens AI Free.
- Each link will route to a separate page: Home, Pricing, About Us, Blog, Sign In, and Get Data Lens AI Free.
- The application logo will be displayed on the left side of the navigation bar on all public and authenticated pages.

### 1.2 Version Management and Database Upgrades
- Each product version will have a separate SQL file for schema changes and upgrades.
- The database will include a table (e.g., `app_version`) to track the current version of the product.
- After login, the current version number will be displayed to the user in the application UI.

---

## 2. Updated Milestones (v0.2)
- Add public pages: Home, Pricing, About Us, Blog, Sign In, Get Data Lens AI Free
- Add logo placement in navigation
- Implement version management and display after login
- Maintain separate SQL files for each version upgrade

---

**End of PRD v0.2 (Delta)**
