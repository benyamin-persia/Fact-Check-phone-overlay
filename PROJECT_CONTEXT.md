# Project Context

This document explains the full context behind this repository: what was requested, what was built, what was proven, and what could not honestly be provided.

## User goal

The target product was an Android app that could:

- watch what the user is currently reading on the phone screen
- detect tweets or short social claims automatically
- fact-check them against live internet information
- show a floating overlay result without forcing a manual copy-paste flow

The user also wanted:

- the APK to be developed directly on the phone
- the APK to be built directly on the phone
- the toolchain to stay mobile-first rather than desktop-first
- the backend to ideally avoid paid API usage
- the app to somehow connect directly to the Codex CLI session if possible

## What was discussed during development

The development conversation covered several layers:

- phone and LAN security
- Android build feasibility on-device
- overlay vs share-target app architecture
- accessibility-based screen reading
- OCR fallback
- backend design for current fact-checking
- whether ChatGPT Plus or Codex CLI usage could be reused as an app backend
- whether on-device Gemini, Gemma, or Google AI Edge Gallery could solve the backend problem
- what Android would and would not allow in practice

## What was built

### Android side

The Android prototype now includes:

- Kotlin Android app
- floating overlay card
- accessibility service for visible text extraction
- screenshot OCR fallback using ML Kit
- X/Twitter-only trigger filtering
- settings screen for backend URL/token
- local network security config for localhost/LAN HTTP testing

### Backend side

The repository includes a lightweight Python backend with:

- `GET /health`
- `POST /factcheck`

The current backend version is structured around a live web-backed fact-check request flow and uses the OpenAI Responses API with web search.

## What was proven successfully

### 1. On-phone Android development is possible

The full app scaffold and build process were handled on the phone using Termux and Debian proot.

### 2. On-phone APK build is possible

The APK build path was made to work by combining:

- Termux packages
- Debian proot
- Java inside Debian
- Gradle inside Debian
- Android SDK pieces on the phone
- AAPT2 override to use the phone-compatible ARM binary

### 3. The overlay approach is technically real

The app is not just a mockup. It has working code for:

- overlay rendering
- accessibility event processing
- visible text extraction
- OCR fallback
- backend request dispatch

### 4. X/Twitter-only behavior was implemented

The service was narrowed to `com.twitter.android` to reduce noisy triggers from unrelated apps.

### 5. Local backend connectivity was enabled

The client was configured so local development backends like `127.0.0.1:8080` could be used.

## What could not be honestly provided

### 1. Direct app-to-Codex-CLI integration

This was requested explicitly, but it could not be implemented honestly.

Why:

- the Codex CLI session is interactive, not a supported app backend
- it does not expose a stable local API contract for Android apps
- it is not a persistent programmable inference service
- it cannot simply be treated as a background mobile backend

So the request to make the APK automatically connect to this exact CLI session could not be fulfilled.

### 2. Free automatic live fact-checking without a real backend

The user wanted an up-to-date fact-check experience that did not require API cost.

The engineering reality is:

- current fact-checking requires fresh data from the internet
- fresh data retrieval is separate from local UI capture
- high-quality reasoning still requires either a model service or a local callable runtime

So a truly current fact-check system still needs a retrieval-and-reasoning backend of some kind.

### 3. Reusing ChatGPT Plus as if it were an app API

This also could not be provided.

Interactive ChatGPT/Codex use and programmable app integration are different products and different interfaces.

### 4. Consumer on-device AI apps as drop-in backends

Gemini on the phone, Gemma apps, and Google AI Edge Gallery were discussed.

What they may offer:

- local inference
- experimentation
- offline/on-device reasoning in some configurations

What they do not automatically provide:

- a supported backend API for this app
- live web retrieval on their own
- guaranteed APK integration simply because they are installed

### 5. Fully silent install on a non-rooted device

The APK could be built automatically and the installer could be launched, but Android still controls the final install path and permission gates.

## Why this repository still matters

This codebase is useful because it isolates the problem into the parts that are actually solvable today:

- mobile UI and overlay behavior
- screen text capture
- OCR fallback
- backend integration path

The biggest unsolved problem is no longer Android interaction. It is the backend strategy for live retrieval and reasoning.

## Honest summary

This repository is both a prototype and a reality check.

It proves that the Android side of the product is viable and that the app can be built on the phone.

It also documents the boundaries clearly:

- what Android allows
- what this Codex CLI session is not
- why live fact-checking still needs live retrieval
- why on-device AI apps do not automatically become your app backend
