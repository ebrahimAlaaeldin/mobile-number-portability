// Swapped in for api.config.ts by the "production" build configuration
// (angular.json fileReplacements) — this is what ships to Vercel. Points at
// the backend's public Railway domain instead of localhost:8080.
export const API_BASE = 'https://mobile-number-portability-production.up.railway.app/api';

// STOMP-over-WebSocket endpoint registered in WebSocketConfig on the backend.
// wss:// (not https://) — this is handed straight to the native WebSocket transport.
export const WS_BASE = 'wss://mobile-number-portability-production.up.railway.app/ws';
