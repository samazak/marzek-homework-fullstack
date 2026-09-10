FROM node:24.12.0-alpine AS frontend
WORKDIR /ui
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm test && npm run build

FROM gradle:9.7.1-jdk21 AS backend
WORKDIR /workspace
COPY backend/build.gradle backend/settings.gradle ./
COPY backend/src/ ./src/
COPY --from=frontend /ui/dist/ ./src/main/resources/static/
RUN gradle --no-daemon test bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=backend --chown=app:app /workspace/build/libs/trade-finance-demo-1.0.0.jar app.jar
USER app
ENV SERVER_ADDRESS=0.0.0.0
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
