# Stage 1: Build the Angular App
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copy the built files from Stage 1 (check your angular.json output path, usually dist/project-name/browser or dist/project-name)
# Since we named the project 'frontend', it is likely dist/frontend/browser in Angular 17+
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
