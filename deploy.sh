#!/bin/bash

echo "배포 시작"

pkill -f spring-gift || echo "실행 중인 앱 없음"

if [[ -z "${KAKAO_CLIENT_ID}" || -z "${KAKAO_CLIENT_SECRET}" ]]; then
  echo "KAKAO_CLIENT_ID와 KAKAO_CLIENT_SECRET은 반드시 세팅되어야한다."
  exit 1
fi
sudo nohup java -jar \
    -Dserver.port=8080 \
    -Dkakao.redirect.url=http://13.124.50.121 \
    -DKAKAO_CLIENT_ID="${KAKAO_CLIENT_ID}" \
    -DKAKAO_SECRET_KEY="${KAKAO_CLIENT_SECRET}" \
    /home/ubuntu/spring-gift-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "배포 완료"