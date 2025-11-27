#!/bin/bash

# 인자값 확인
if [ -z "$1" ]; then
    echo "버전 값이 비어져 있습니다. 스크립트를 종료합니다. (첫번째 인자값 : 버전)"
    exit 1
fi

# 인자값 설정
VERSION=$1

# 인자값 설명
# VERSION : 버전 값. 도커 이미지의 버전을 관리하는 태그 역할을 수행한다.

echo "프로젝트 빌드를 시작합니다."

# 운영체제 확인
echo "현재 운영체제는 $OSTYPE 입니다."
if [[ "$OSTYPE" == "darwin"* ]]; then
    # MacOS
    gradle build -x test
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    ./gradlew build -x test
elif [[ "$OSTYPE" == "msys"* ]]; then
    # MSYS or similar
    ./gradlew build -x test
else
    gradlew build -x test
    echo $OSTYPE
fi

sleep 1

echo "Docker 이미지 빌드를 시작합니다."
docker build -t kspia01/sis-batch:${VERSION} .
echo "Docker 이미지 빌드가 완료되었습니다."