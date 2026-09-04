// Dev default: the backend on localhost:8080, whether it's run via
// `mvn spring-boot:run`, `docker compose up`, or the IDE. The production
// build (Vercel) swaps this whole file for api.config.prod.ts via the
// "production" fileReplacements entry in angular.json, since frontend and
// backend now deploy to separate hosts (Vercel + Railway).
export const API_BASE = 'http://localhost:8080/api';

// STOMP-over-WebSocket endpoint registered in WebSocketConfig on the backend.
// ws:// (not http://) — this is handed straight to the native WebSocket transport.
export const WS_BASE = 'ws://localhost:8080/ws';
