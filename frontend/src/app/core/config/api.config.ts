// Single deployment target, so no environment-file machinery: the backend is
// always published on :8080, whether it's run via `mvn spring-boot:run`,
// `docker compose up`, or the IDE — dev and the containerized frontend both
// reach it at the same URL.
export const API_BASE = 'http://localhost:8080/api';

// STOMP-over-WebSocket endpoint registered in WebSocketConfig on the backend.
// ws:// (not http://) — this is handed straight to the native WebSocket transport.
export const WS_BASE = 'ws://localhost:8080/ws';
