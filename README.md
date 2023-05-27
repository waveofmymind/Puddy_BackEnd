# PUDDY💙

> 전문가 기반 반려동물 질의응답 중심의 커뮤니티 서비스

![image](https://user-images.githubusercontent.com/93868431/232716519-3f43848d-1f78-4c6d-a429-a759300fa31b.png)

[PUDDY 서비스 바로가기](http://www.puddy.world/) <br/>
> 현재 360*740에 최적화되어있습니다. (갤럭시S8+)

# 퍼디는?

퍼디(Puddy)는 'Pet'과 'Buddy'의 합성어로,

반려견을 가족과 친구처럼 소중하게 생각하는 의미를 담고 있습니다.

반려견의 주인이 서로 정보를 교환하고, 경험을 공유하며 도움을 주고받을 수 있는 플랫폼입니다. 

또한, 인증된 전문가들을 통해 궁금증을 해결할 수 있습니다.

## 기술 스택

### BackEnd

- Java 17
- Spring Boot 2.7.9
- Spring Security
- Gradle
- JPA
- Querydsl
- S3
- AWS RDBMS
- MySQL
- Redis
- GCP Ubuntu 20.04
- Docker
- Jenkins

## 시스템 아키텍처

![image](https://user-images.githubusercontent.com/93868431/230275264-20f15fc0-3b38-47d5-8577-eb8645e37571.png)

## ERD

![puddy-erd-0419](https://user-images.githubusercontent.com/93868431/232953874-7f95fe60-1899-4058-a109-e02b2a98fb54.png)

## API 명세서

[API 명세서](https://documenter.getpostman.com/view/23164315/2s93RMVFEm#22ba6470-2481-40e0-a70f-dbf2b4361306)

## 단위 테스트 및 통합 테스트

![image](https://github.com/waveofmymind/Puddy_BackEnd/assets/93868431/e49008f0-1a42-40ee-bcc8-d11947779d22)

# **트러블 슈팅 및 기술적 도전**

---

- **도메인 설계에 대한 고민**: 혼자서 도메인 모델링을 해야했기 때문에, 퍼디의 핵심 도메인에 대해서 기존의 설계에 대한 문제점을 인지하고 개선했습니다.
    
    **[도메인 설계에 대한 고민](https://waveofymymind.tistory.com/130)**
    
- **연관관계를 고려한 JPA N+1 문제 개선하기**: 프로젝트를 진행함에 따라 다양한 도메인이 얽히게 되었고, 그에 대한 연관관계를 다시 수립하고 조회 쿼리 수를 8개를 3개로 줄일 수 있었습니다.
    
    **[연관관계를 고려한 JPA N+1 문제 개선하기](https://waveofymymind.tistory.com/125)**
    
- **Redis 캐시를 사용한 Refresh 토큰 관리하기**: JWT 인증으로 사용했던 Refresh 토큰을 RDB에서 관리하는 것을 Redis 캐시를 사용함으로써 자원 관리의 효율성을 높이고 응답 속도를 개선했습니다.
    
    **[Redis 캐시를 사용한 JWT 리프레시 토큰 관리하기](https://waveofymymind.tistory.com/113)**
    
- **조회수 동시성 문제 해결하기**: 퍼디 프로젝트에 조회수와 좋아요 수에 대한 동시성 문제를 해결하고 JMeter를 통해 검증했습니다.
    
    **[조회수 필드에 대해 동시성 문제 해결하기](https://waveofymymind.tistory.com/108)**
    
- **프로젝트 리팩토링**: 레이어 패턴으로 설계했던 퍼디 프로젝트는, 레이어간 DTO를 공유하거나, 구체적인 클래스를 의존하여 DIP를 위배하는 문제가 있었습니다. 이에 대해 구현체를 직접 의존하지 않도록 하여 확장에 용이하도록 변경했고, 레이어간 DTO를 구분해서 결합성을 낮출 수 있었습니다.
- **무중단 CI/CD 배포 환경 구성**: 개발 과정에서 변경 사항이 있을 때마다 직접 실행을 했던 불편함을 개선하고자, 젠킨스, 도커를 사용해서 클라우드 환경에 자동으로 배포할 수 있는 환경을 구성했습니다.

# 회고

---

**[첫 프로젝트 마무리](https://waveofymymind.tistory.com/120)**

# 회의록

---

팀 프로젝트시 진행했던 회의에 대한 기록 페이지입니다.

[](https://www.notion.so/4c28bccf185c4a689455fdb642d901db)
