.PHONY: help build up down logs clean restart stop start ps test rebuild-up logs-redis-insight

help:
	@echo "Payment Module Docker Commands"
	@echo "================================"
	@echo "make build       - Build Docker images"
	@echo "make up          - Start all services"
	@echo "make down        - Stop and remove containers"
	@echo "make rebuild-up  - Rebuild app and start all services"
	@echo "make logs        - View logs from all services"
	@echo "make logs-app    - View logs from payment app"
	@echo "make logs-mongo  - View logs from MongoDB"
	@echo "make logs-redis  - View logs from Redis"
	@echo "make logs-kafka  - View logs from Kafka"
	@echo "make logs-kafka-ui - View logs from Kafka UI"
	@echo "make logs-redis-insight - View logs from Redis Insight"
	@echo "make ps          - List running containers"
	@echo "make restart     - Restart all services"
	@echo "make stop        - Stop all services"
	@echo "make start       - Start all services"
	@echo "make clean       - Remove containers and volumes"
	@echo "make test        - Run tests"

build:
	docker-compose build

rebuild-up:
	./mvnw clean package -DskipTests && docker-compose build && docker-compose up -d

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

logs-app:
	docker-compose logs -f payment-app

logs-mongo:
	docker-compose logs -f payment-mongo

logs-redis:
	docker-compose logs -f payment-redis

logs-kafka:
	docker-compose logs -f payment-kafka

logs-kafka-ui:
	docker-compose logs -f payment-kafka-ui

logs-redis-insight:
	docker-compose logs -f payment-redis-insight

ps:
	docker-compose ps

restart: down up

stop:
	docker-compose stop

start:
	docker-compose start

clean:
	docker-compose down -v

test:
	./mvnw clean test
