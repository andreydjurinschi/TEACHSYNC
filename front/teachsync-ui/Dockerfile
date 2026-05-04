FROM node:22-alpine AS build

WORKDIR /app
COPY front/teachsync-ui/package*.json ./
RUN npm ci

COPY front/teachsync-ui .
RUN npm run build

FROM nginx:1.27-alpine

COPY front/teachsync-ui/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/teachsync-ui/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
