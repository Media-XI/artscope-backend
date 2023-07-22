IMAGE_FILE_PATH="/home/ubuntu/deploy/image.txt"
IMAGE_NAME=$(cat "$IMAGE_FILE_PATH")
CONTAINER_NAME=art-backend

# 이전 도커 컨테이너 종료
echo "이전 도커 컨테이너 종료"
docker ps | grep $CONTAINER_NAME | awk '{print $1}' | xargs docker stop

# 새로운 도커 컨테이너 실행
echo "IMAGE_NAME: $IMAGE_NAME 도커 실행"
docker run -d -p 18080:8080 --name $CONTAINER_NAME $IMAGE_NAME