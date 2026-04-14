# Live Fact Check Overlay

This repository documents an Android experiment that started from a direct user need:

> I want my phone to fact-check the tweet or text I am currently looking at, automatically, while I am reading it on screen.

The project was built directly on an Android phone in Termux/proot, not on a desktop machine.

## What I wanted

The original product goal was very clear:

- read what is currently visible on the phone screen
- detect tweets or similar short social claims automatically
- check them against current information from the internet
- show the result as a floating overlay
- avoid a manual copy-paste workflow
- avoid desktop development
- build the APK entirely on the phone

The ideal end state was a phone-native assistant that feels immediate:

- user scrolls on X/Twitter
- the app detects the visible post
- the app fact-checks it
- the overlay explains the result in place

## What we discussed

This repository is not only code. It is also the record of a practical product and engineering conversation.

We discussed:

- LAN and phone security risks
- whether the Android phone could be used as a full development machine
- whether an APK could be built directly on-device
- how to create a true floating fact-check overlay
- how to read what the user is seeing on screen
- whether the app could directly connect to this Codex CLI session
- whether paid APIs were necessary
- whether Gemini, Gemma, AI Edge Gallery, or other on-device AI options could replace a cloud backend
- what was technically possible versus what only seemed possible from a user perspective

Those conversations shaped the architecture that ended up in this repo.

## What I asked the system to do

The implementation goals eventually became:

- develop the APK directly on the phone
- make the app use an overlay, not just a share target
- detect what the user is seeing on screen
- automatically fact-check visible tweet text
- use live internet information, not stale offline knowledge
- try to avoid depending on a paid API if possible
- publish the result to GitHub with a clear explanation of the real outcome

## What was actually built

### Android client

The Android side now includes:

- Kotlin Android application
- floating overlay UI
- accessibility service to inspect visible screen text
- OCR fallback using screenshot-based text recognition
- package filtering so the overlay reacts only to X/Twitter
- backend endpoint configuration from inside the app
- network security config allowing local backend access over HTTP for localhost/LAN testing

### Backend

The repository also includes:

- a small Python HTTP backend
- `GET /health`
- `POST /factcheck`
- JSON request/response shape for claim checking

This backend is structured for live internet-backed fact-checking, and in its current version it calls the OpenAI Responses API with web search enabled.

## What was proven

Several things were proven successfully in this project.

### 1. The app can be built on the phone

The APK build path was made to work entirely on the Android phone.

That required:

- installing Java and Android build tooling in Termux
- working around broken Java behavior in the native Termux environment
- creating a Debian proot environment on the phone
- running Gradle from inside that Debian environment
- overriding AAPT2 so the build used the phone-compatible ARM binary instead of the default Linux x86 binary

Result:

- the APK built successfully on-device

### 2. The overlay architecture is real

The Android client is not hypothetical. It implements:

- accessibility-driven text extraction
- OCR fallback
- floating overlay rendering
- runtime fact-check requests

### 3. The app can be limited to X/Twitter

The service was tuned so it ignores unrelated apps and focuses on `com.twitter.android`.

### 4. The app can talk to a local backend

The manifest and network security configuration were adjusted so the client can talk to local HTTP endpoints like:

- `http://127.0.0.1:8080`
- `http://localhost:8080`
- the phone's LAN IP during testing

## What could not be proven

This part is just as important as the successful part.

### 1. The app cannot directly use this Codex CLI session as its backend

This was one of the most important requests in the conversation, and it had to be answered precisely.

What was wanted:

- run this CLI locally
- let the APK send requests directly to it
- use this session itself as the fact-check engine

What is true:

- this Codex chat session is interactive, but it is not exposed as a supported local API for custom Android apps
- it does not provide a stable HTTP/Binder/WebSocket interface that the APK can rely on
- it is not a persistent app backend contract

So that bridge could not be provided.

### 2. ChatGPT Plus / Codex usage here is not the same as app API access

Another key discovery was economic and architectural:

- using ChatGPT or Codex interactively does not automatically give a custom Android app a free backend
- subscription use and programmable app integration are separate things

So the app could not simply reuse this CLI session or a ChatGPT subscription as if it were an app-facing inference service.

### 3. A live fact-check system still needs live retrieval

This was clarified multiple times during the project:

- if the goal is current fact-checking, the system must retrieve fresh data from the internet
- an offline model alone is not enough for up-to-date claims

This means the product still needs:

- live search / retrieval
- source selection
- reasoning over those sources

### 4. On-device AI apps are not automatically app backends

We considered:

- Gemini on the phone
- Gemma
- Google AI Edge Gallery

What they may provide:

- local inference
- local experimentation
- on-device reasoning in some cases

What they do not automatically provide:

- a supported backend API for this Android app
- live web retrieval on their own
- a guaranteed integration path from this APK to the installed consumer AI app

So they did not remove the backend problem by themselves.

### 5. Fully silent installation was not possible

The APK was built and the installer was launched from the phone, but Android package installation still remained subject to device security rules.

That means:

- the APK could be built automatically
- the installer intent could be launched automatically
- but final installation still depends on Android's permission and installer restrictions

## Why this matters

This repository is valuable because it separates three things that are easy to confuse:

- what the product idea is
- what the app can already do on the device
- what still requires a true backend or model runtime

The phone-side UX is achievable.

The automatic current fact-checking goal is also achievable.

But the second part depends on an actual reasoning-and-retrieval service, not just on-screen extraction.

## Current architecture

### On-device flow

- Android accessibility service observes screen changes
- visible text is extracted
- if text is incomplete, screenshot OCR is attempted
- app sends text to a backend
- overlay shows progress and result

### Backend flow

- backend receives the extracted claim/text
- backend retrieves or reasons over current information
- backend returns structured fact-check output

## Honest status

This repository represents a real prototype, not a fake mockup.

It successfully demonstrates:

- on-phone Android development
- overlay-based screen reading
- OCR fallback
- X/Twitter-focused behavior
- local backend integration path

It does not yet solve, by itself:

- a free high-quality live fact-check backend
- direct integration with this CLI session
- a zero-cost fully automatic internet-backed reasoning layer

## Perspective

From my perspective, this project is still worth building even with those limits.

Why:

- reducing the distance between reading a claim and challenging it is valuable
- the mobile UX problem is solvable
- the remaining challenge is now clearly isolated to retrieval + reasoning infrastructure

That is progress.

The conversation behind this repository made the boundaries much clearer:
