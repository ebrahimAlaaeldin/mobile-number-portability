# --- Build stage ------------------------------------------------------------
FROM node:22-alpine AS build
WORKDIR /app

# Dependencies first so a source-only change doesn't re-trigger npm install.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build

# --- Runtime stage ------------------------------------------------------------
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
