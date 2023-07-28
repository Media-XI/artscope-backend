IMAGE_FILE_PATH="/home/ubuntu/deploy/image.txt"
IMAGE_NAME=$(cat "$IMAGE_FILE_PATH")
CONTAINER_NAME=art-backend
CONTAINER_ENV_PATH="/home/ubuntu/env/.env"

# 새로운 도커 컨테이너 실행
echo "IMAGE_NAME: $IMAGE_NAME 도커 실행"
docker run -d -p 18080:8080 --name $CONTAINER_NAME \
  --env-file $CONTAINER_ENV_PATH \
 $IMAGE_NAME