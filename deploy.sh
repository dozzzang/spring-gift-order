#!/bin/bash

echo "배포 시작"

pkill -f spring-gift || echo "실행 중인 앱 없음"

sudo nohup java -jar \
    -Dserver.port=8080 \
    -DKAKAO_CLIENT_ID="${KAKAO_CLIENT_ID:-temp}" \
    -DKAKAO_SECRET_KEY="${KAKAO_SECRET_KEY:-temp}" \
    /home/ubuntu/spring-gift-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "배포 완료"