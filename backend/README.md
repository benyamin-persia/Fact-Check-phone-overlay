# Backend

This backend exposes:

- `POST /factcheck`
- `GET /health`

It calls OpenAI's Responses API with the web search tool enabled so the mobile app can use live internet data.

## Run

```bash
export OPENAI_API_KEY=your_key_here
export OPENAI_MODEL=gpt-5
python3 backend/server.py
```

Then point the Android app settings to:

```text
http://YOUR_PHONE_OR_SERVER_IP:8080/factcheck
```

## Notes

- The API key should stay on the backend, not in the APK.
- If you expose this beyond your LAN, put it behind HTTPS and authentication.
