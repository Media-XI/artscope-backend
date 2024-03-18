# Artscope Backend

부산 금샘미술관의 청년전시전 공모를 받기 위해 시작한 프로젝트로, 작품 전시를 온라인 갤러리 공간으로 형성하고 누구나 좋아하는 작품을 감상할 수 있고 예술인들의 교류를 할 수 있는 서비스입니다.

## Tech
- Java 17
- Spring Boot 3.2, Spring MVC, Spring Security, Swagger
- Spring Data JPA
- MySQL, H2, Redis, ElasticSearch, log4jdbc
- OAuth 2.0, JWT
- Docker, Gradle, Nginx, Kubernetes, Pinpoint, Prometheus, Grafana, ELK
- AWS EC2, AWS CloudFront, AWS S3, AWS Lambda@Edge
- Github Actions, ArgoCD, AWS CodeDeploy

## Architecture
![artscope drawio](https://github.com/Media-XI/artscope-backend/assets/5029567/b18e0b00-e7d8-430a-9d29-bbbbc5bd22e0)

## 정적 파일 업로드 로직
![정적파일](https://github.com/Media-XI/artscope-backend/assets/5029567/6a02d4fd-1920-458c-901b-69f0309dff0b)

## CI/CD 파이프라인
![CI_CD](https://github.com/Media-XI/artscope-backend/assets/5029567/b72723e8-a190-4e73-8712-db4173b82eb5)

# 서버 구동
```shell
> ./gradlew bootRun
```
or
```shell
> docker build -t backend:0.1 . # 도커 이미지 생성 후
> docker-compose up -d
```
