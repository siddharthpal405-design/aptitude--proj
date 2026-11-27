# AptiMaster - Aptitude & Mock Test Platform (Java Full Stack)

A comprehensive, production-grade aptitude preparation and examination platform built with **Spring Boot 3 (Java 24)** and **React 18 + Vite + TypeScript + Tailwind CSS**.

---

## 🌟 Full Feature Overview

### 1. Authentication & Security
- User registration and login with stateless JWT authentication (`Spring Security 6`).
- BCrypt password encryption.
- Token auto-attachment via Axios request interceptors and auto-logout on token expiration.

### 2. Chapter-Wise Syllabus Practice
- Full hierarchical organization:
  - **Quantitative Aptitude**: *Percentages*, *Profit & Loss*, *Time & Work*, *Number Systems*, *Ratio & Proportion*.
  - **Logical Reasoning**: *Blood Relations*, *Syllogisms*, *Coding-Decoding*.
  - **Verbal Ability**: *Spotting Errors*, *Sentence Improvement*.
- **MCQ Practice Arena**:
  - 4 options per question (A, B, C, D).
  - Instant visual feedback on submission (Green for correct, Red for selected incorrect, reveals true answer).
  - Complete step-by-step mathematical explanation box.

### 3. Timed Mock Test Arena (`/mock-tests`)
- Real-time countdown timer (`HH:MM:SS`) with warning colors when time runs low.
- Hard automatic submission when the timer reaches `00:00:00`.
- **NTA / GATE Style Question Palette Grid**:
  - ⚪ **Unvisited**
  - 🔴 **Not Answered**
  - 🟢 **Answered**
  - 🟣 **Marked for Review**
  - 🔵 **Answered & Marked for Review**
- Asynchronous option auto-saving on each question jump so no progress is lost.

### 4. Scorecard & Performance Analytics (`/mock-tests/result/:sessionId`)
- Detailed scorecard: Total score, Max marks, Accuracy %, Percentile estimate, Time taken.
- Question breakdown: Attempted, Correct (+1.00), Incorrect (-0.25), Skipped (0.00).
- Filterable question review ("All", "Correct", "Incorrect", "Skipped") with step-by-step explanations for each question.

### 5. Daily Streak Engine & 365-Day Heatmap
- Automatic consecutive day tracking and longest streak calculation.
- Live animated streak flame widget in the top navigation bar (`🔥 X Days`).
- GitHub-style 52-week contribution heatmap on the Dashboard displaying daily question solve intensity.

### 6. Question Bank Scaling & Admin Ingestion (`/admin`)
- Bulk CSV upload endpoint (`POST /api/admin/questions/bulk-upload-raw`) to ingest hundreds of MCQs into chapters in batches.
- Mock test generator to create simulated exam sprints across syllabus chapters.

---

## 🏃 Quick Start Guide

### 1. Run Backend
```powershell
cd C:\Users\siddh\.gemini\antigravity\scratch\aptitude-platform\backend
..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
```
- API Base: `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/aptitudedb`)

### 2. Run Frontend
```powershell
cd C:\Users\siddh\.gemini\antigravity\scratch\aptitude-platform\frontend
npm.cmd run dev
```
- Web Application: `http://localhost:5173`

### 3. Demo Credentials
- **Student**: `student@test.com` / `Student@123`
- **Admin**: `admin@aptitude.com` / `Admin@123`
