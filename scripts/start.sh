IMAGE_FILE_PATH="/home/ubuntu/deploy/image.txt"
IMAGE_NAME=$(cat "$IMAGE_FILE_PATH")
CONTAINER_ENV_PATH="/home/ubuntu/env/.env"
SERVICE_NAME=art-be

# 새로운 도커 컨테이너 실행
echo "IMAGE_NAME: $IMAGE_NAME 도커 실행"
echo "version: '3.8'

services:
  art-be:
    container_name: art-backend
    image: ${IMAGE_NAME}
    ports:
      - 18080:8080
    env-file:
      - ${CONTAINER_ENV_PATH}
    volumes:
      - logs_data:/logs
      - ./pinpoint/profiles:/pinpoint-agent/profiles:rw
      - pinpoint-volumes:/pinpoint-agent
    depends_on:
      - redis
      - pinpoint-agent

  pinpoint-agent:
    image: pinpointdocker/pinpoint-agent:2.5.2
    volumes:
      - pinpoint-volumes:/pinpoint-agent

  redis:
    image: redis:7.0-alpine
    command: redis-server --port 6379
    container_name: art-redis
    labels:
      - "name=redis"
      - "mode=standalone"
    ports:
      - 16379:6379

  filebeat:
    image: filebeat-custom:0.18
    container_name: art-filebeat
    volumes:
      - logs_data:/logs
    ports:
      - 5044:5044
    depends_on:
      - art-be

volumes:
  logs_data:
  pinpoint-volumes:

networks:
  default:
    name: art
    external: true" > docker-compose.yaml

docker-compose up -d $SERVICE_NAME