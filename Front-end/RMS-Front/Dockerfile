# stage 1
FROM node:10.13.0 AS node
WORKDIR /app
COPY package.json yarn.lock ./
RUN yarn install
COPY . .
RUN npm run build --prod

# stage 2
FROM nginx:alpine
COPY --from=node /app/dist/admin-dashboard-angular /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
