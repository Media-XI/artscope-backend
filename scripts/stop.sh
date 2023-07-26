CONTAINER_NAME=art-backend

# 도커 컨테이너가 있는지 확인
RUNNING_CONTAINER_ID=$(docker ps -q --filter "name=$CONTAINER_NAME")
echo "실행중인 컨테이너 ID: $RUNNING_CONTAINER_ID"

#
# 이전 도커 컨테이너 종료
if [ -n "$RUNNING_CONTAINER_ID" ]; then
  echo "이전 도커 컨테이너 종료 및 삭제합니다"
  docker stop CONTAINER_NAME && docker rm CONTAINER_NAME
fi
