# Backend

This backend exposes:

- `POST /factcheck`
- `GET /health`

It calls OpenAI's Responses API with the web search tool enabled so the mobile app can use live internet data.

## Run

```bash
export OPENAI_MODEL=gpt-5
python3 backend/server.py
```

Then point the Android app settings to:

```text
http://127.0.0.1:8080/factcheck
```

## Notes

- The backend accepts the API key from the request `Authorization: Bearer ...` header.
- It can also fall back to `OPENAI_API_KEY` from the environment if you prefer that setup.
- If you expose this beyond your LAN, put it behind HTTPS and authentication.
