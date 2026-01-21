#!/bin/bash

echo "--------------- batch 시작 -----------------"
docker stop batch-app || true
docker rm batch-app || true

docker pull 989775483620.dkr.ecr.ap-northeast-2.amazonaws.com/mopl/batch:latest

cd /home/ubuntu/batch
docker compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null || true
docker compose -f docker-compose.prod.yml up -d --build
echo "--------------- batch 끝 ------------------"
