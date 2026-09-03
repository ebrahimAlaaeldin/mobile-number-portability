// Mirrors the backend's mnp.porting.request-timeout (application.properties),
// currently PT2M. There's no endpoint exposing this value, so it's a mirrored
// constant rather than a fetched one — keep the two in sync by hand.
export const PORTING_REQUEST_TIMEOUT_MS = 2 * 60 * 1000;
