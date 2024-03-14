#!/bin/bash
migration_path="./src/main/resources/db/migration"

echo
echo "Flyway 최신 마이그레이션 생성 스크립트"
echo "현재 최신 버전을 기준으로 다음 버전의 Flyway 마이그레이션 파일을 생성합니다."
echo "마이그레이션 경로 : ${migration_path}"
echo

latest_version=$(ls -tr ${migration_path} | tail -n 1 | cut -d '_' -f 1 | cut -c 2-)
latest_version_file=$(ls -tr ${migration_path} | tail -n 1)
if [ -z "$latest_version" ]; then
  echo '프로젝트 루트경로에서 스크립트를 실행해주세요.' >&2
  exit 1;
fi

echo "현재 최신 버전은 V$latest_version 입니다. "
read -p "다음 버전의 마이그레이션 파일을 생성하겠습니까? (y/n) " decision
echo
if [ "$decision" != "Y" ] && [ "$decision" != "y" ]; then
    exit
fi

read -p "마이그레이션 설명을 입력해주세요 (예: 사용자 테이블 생성합니다) : " description
echo
if [ -z "$description" ]; then
  exit
fi

echo "
# $description
# 작성자 : $(git config user.name) ($(git config user.email))
# 작성 날짜  : $(date +'%Y-%m-%d')
# 현재 버전  : V$(($latest_version + 1)) (이전 버전 : $latest_version_file)

" > ${migration_path}/V$(($latest_version + 1))__application.sql

echo "마이그레이션 파일이 생성되었습니다."
echo "${migration_path}/V$(($latest_version + 1))__application.sql 파일을 이용해 SQL 스크립트를 작성해주세요."
