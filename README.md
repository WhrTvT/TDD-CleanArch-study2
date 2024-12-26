# 클린 아키텍처와 JPA 동시성 제어에 대한 분석
**1. 개요**

**1-1 생각정리**
- 시스템에 변경이 생기거나 유지보수를 하기 위해서 기본적으로 시스템 파악이 선행될 것이다.<br>
  좋은 시스템이라면 얼마 안가서 작업에 돌입할 수 있겠지만, 도저히 못 알아먹을 시스템이라면 난감해진다.<br>
  그렇다면 개발자의 관점에서 좋은 시스템이란 무엇일까?🤔 내 주관적인 판단으로는 아래와 같다.<br>
    - 패키지 구조에 규칙이 있어야 한다.(계층형과 도메인형)<br>
    - 파일명에 규칙이 있어야 한다.(admin?, ad?)<br>
    - ...<br>

**1-2 클린 아키텍처**
- 아키텍처는 유지보수를 용이하게 하고, DIP와 OCP 원칙 등 SOLID 원칙에 대한 기준 등을 세움으로서 개발자들간의 커뮤니케이션에 지대한 영향을 미친다.<br>
  but. 초기 개발비용이 커진다는 점과 제약사항이 생긴다는 단점도 있으니, 꼭 요구사항을 파악해서 시스템에 맞는 FIT한 아키텍처를 선택하는 것이 중요하다.
- 클린 아키텍처는 Layered 아키텍처를 기반으로, Domain(Service 계층)을 중심으로 DATA 계층과 API 계층이 Domain을 의존하도록 한다.<br>
  장점은 Layered 아키텍처의 수직적이고 Domain이 보호받지 못한다는 단점을 보완하고, 헥사고날 아키텍처 등에 비해 간결하다는 장점이 있다.
- 클린 아키텍처는 기존 아키텍처들의 비대함을 덜어내고, 확장성도 챙긴 아키텍처이기 때문에 상황에 맞게 변형하여 사용하는 것이 맞는 사용 방법이다.
- 포인트는 '클린 아키텍처는 정해진 방법이 있지 않다는 점'이니, 견지망월(달을 가리켰더니 손가락만 봄)하지 않도록 주의하자.
---
**2. 패키지 설계**
```
src/
└── main/
    └── java/
        └── io/
            └── hhplus/
                └── cleanarch/
                    ├── domain/        ---> Domain 계층(Business / Entity)
                    ├── exception/     ---> 비즈니스 로직의 예외 처리
                    ├── infrastructure/---> DATA 계층(JPA, Impl)
                    └── interfaces/    ---> API 계층(presentation / Controller / DTO)
```
- `domain` : **domain 은 왕**이다.<br>
  Entity 를 도메인 모델로 쓰고 있다면, 비즈니스 계층의 탈출부이다.<br>
  엔티티가 아닌 xxInfo/xxResult와 같이 DB 패러다임과 관계없는 개념 객체를 반환하는 것이 좋다.
- `exception` : 비즈니스 로직의 예외 처리를 담당한다.
- `infrastructure` :infrastructure 는 **왕의 집사**다.<br>
  왕의 집사는 왕이 가져오라는 것을 어떻게든 가져와야 하고, 왕이 전달한 물건을 어디에든 가져다 놓아야 한다.
- `interfaces` : infrastructure 는 **왕의 집사**다<br>
  domain이 필요로 하는 input으로 Request를 반환해 호출하고, domain 의 반환 값을 Response 로 감아서 응답한다.
---
**3. DB 테이블 설계와 ERD**

![image](https://github.com/user-attachments/assets/3835f4e1-2c75-46d3-b152-b5153cbd93de)

**3-1 Lecture Table**
- 특강이 저장되는 테이블, 각 특강 정보(TITLE, LECTURER, LECTURE_AT)를 입력받고 CAPACITY_CURRENT로 신청자 수를 검증한다.
- LECTURE_ID를 AUTO_INCREMENT로 할당한다.
- CREATED_AT, UPDATED_AT는 자동 값을 할당한다.

**3-2 LectureRegister Table**
- 특강 신청 내역이 저장되는 테이블, 신청 정보(LECTURE_ID, USER_ID)를 입력받고,<br>
  정상 신청 된다면 LECTURE.CAPACITY_CURRENT + 1을 비즈니스 로직에서 실행한다.
- REGISTER_ID를 AUTO_INCREMENT로 할당한다.
- LECTURE_ID를 외래키로 할당하여 Register 테이블과 Lectrure 테이블을 연결한다.
- REGISTER_AT는 CLIENT가 신청한 시간으로 할당되며, CREATED_AT, UPDATED_AT는 자동 값을 할당한다.
---
**4.동시성 제어 방법**
- DB의 동시성 제어 방식은 총 3가지가 있다. 그 중에 네임드 락을 제외한 2가지에 대한 내용은 아래와 같다.
- 락의 해제 시점은 @Trasactional이 종료 시점인 것을 유념해야 한다.<br>
  만약 테스트에 @Trasactional 어노테이션이 붙는다면, 테스트가 정상적으로 실행되지 않는다.

**4-1 낙관적 락 : VERSION**
- 기본적으로 정합성이 상관없다.
- 누군가가 UPDATE한다면, version이 바뀌게 되어 만약 충돌이 일어나서 version 정합성이 틀어진다면 roolback 된다.
- 충돌이 일어나지 않는 상황에서 속도가 제일 빠르다.
- ex) 30명의 동시성 테스트 :
    - 30명 시도 → 29명 실패, 1명만 성공 → 28명 실패, 1명만 성공 ...
    - 위와 같이 충돌이 발생하는 상황에서 총 60초가 걸린다(30 x 2 sec)
- 적용 방법은 아래와 같다.
  ```Java
  public interface StockRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithOptimisticLock(Long id);
  }
   ```

**⭐4-2 비관적 락 : PESSIMISTIC**
- WRITE 방식 : 정합성을 보장하는 락 방식으로, LOCK을 기준으로 이미 사용 중이라면 `읽기/쓰기 모두 불가능`하다.
- READ 방식 : 정합성을 보장하는 락 방식으로, 공유 락을 통해 사용이어도 `읽기는 가능`하다.
- ex) 30명의 동시성 테스트 :
    - 1명 lock → 29명 wait → until lock 해제 → 1명 lock → 28명 wait ...
    - 위와 같이 충돌이 발생하는 상황에서 총 0.3초가 걸린다(30 x 0.01 sec)
- 적용 방법은 아래와 같다.
  ```Java
  public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
  ```
---
**5. 테스트**
- 병렬 Thrad(executerService)를 통해 40개에 대한 Thread를 동시에 요청하여 테스트를 진행했다.
---
**6. 마무리(회고)**
- Facade : `Controller`에서 `Service`를 직접 호출하지 않도록 `Facade`를 도입한다.<br>
  이렇게 되면 계층 분리가 명확해지고, 데이터를 과다하게 노출/전달하지 않는 장점이 있다.(확장되는 라이브러리가 많다면 적용 선호)
- DTO : 데이터 전송 객체로, 왜 필요한가에 대한 의문이 있었지만 직접 사용해보면서 느낌을 알게되었다.

**6-1 Keep**
- BusinessException을 통한 비즈니스 로직의 예외 처리.
- Domain 계층(Interface)과 DATA 계층(infrastructer / Jpa / Impl)의 분리로 DIP 원칙 준수.
- Entity와 Service, Controller에 대한 Unit 테스트 작성. (but. 작성 수준과 시간은 개선할 여지가 있음.)

**6-2 Problem**
- 동시성 테스트에 대한 검증을 Log로 출력해서 지연시간과 결과를 시각적으로 확인할 수 있도록 개선 필요.
- 현재 비즈니스 로직(BusinessException)에서 예외 처리하는데, DB에서 발생한 예외는 DB단(Entity)에서 처리 필요.
- 현재 H2를 사용해서 테스트를 진행하지만, 실무에서 사용하기에는 부족한 기술이기 떄문에 TestContainer 사용 필요.

**6-3 Try**
- `Service` 쪽에도 DTO를 추가해서 사용해보기.
- Facade 사용해보기.
- TestContainer 사용해보기.