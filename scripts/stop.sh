CONTAINER_NAME=art-backend

# 이전 도커 컨테이너 종료
echo "이전 도커 컨테이너 종료"
docker stop $CONTAINER_NAME

echo "이전 도커 컨테이너 삭제"
docker rm $CONTAINER_NAME