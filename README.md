# 🔨 SPRING ADVANCED 🔨 - Task " Spring 심화 주차 개인 과제 "


---

## 🧑‍💻 개발자 소개

|  |                                   팀원                                   |
|:-------------:|:----------------------------------------------------------------------:|
|프로필| ![image](https://avatars.githubusercontent.com/u/94594402?v=4&size=64) |
|이름|                                  박신영                                   |
|GitHub|                              sinyoung0403                              |
|기술블로그|                 [블로그](https://sintory-04.tistory.com/)                 |

---

## 📌 TASK

### 1. 과제 이름

- **" SPRING ADVANCED "**

### 2. 과제 소개

- 스파르타 Spring 실습에서는 기존에 주어진 구조나 코드 흐름 속에서 문제점을 직접 분석하고, 이를 리팩토링하는 방식으로 학습을 진행했습니다. 
- 단순 구현이 아니라, JPA 기반의 CRUD 로직, 예외 처리, 유효성 검증, 로깅, 인터셉터 등도 함께 다뤘습니다. 
- 특히 Mock을 활용한 테스트 코드 작성을 통해, 코드 품질을 높이기 위한 리팩토링 방법과 테스트가 어떻게 서비스의 안정성을 개선하는지에 대해 더 깊이 이해할 수 있었습니다.

---

## 🎆 과제 풀이

### Lv 1. 코드 개선

1) Early Return
   
- 회원가입 로직에 Early Return 패턴을 적용하여, 불필요한 로직 실행을 사전에 차단했습니다. 

    → 이메일 중복 여부를 먼저 확인하고, 중복일 경우 passwordEncoder.encode() 같은 리소스 낭비가 발생하지 않도록 흐름을 개선했습니다.

2) 불필요한 if-else 피하기

- if-else 중첩 구조를 제거하고, 불필요한 else 블록을 제거하여 코드의 가독성과 흐름을 개선했습니다.
   
  → 상태 코드와 응답 본문에 대한 예외 처리를 순차적으로 Early Return 형태로 분리하여, 보다 명확한 로직 흐름을 구성했습니다.

3) Validation

- 비밀번호 유효성 검증 로직을 Service 계층에서 DTO 로 분리하여 책임을 명확히 했습니다.

    → @Size, @Pattern 등의 Bean Validation 어노테이션을 활용해, 입력값 검증을 요청 단계에서 처리하고, Service는 비즈니스 로직에만 집중할 수 있도록 개선했습니다.

---
### Lv 2. N+1 문제

- 기존 JPQL 기반 쿼리에서 N+1 문제를 해결하기 위해 Fetch Join을 사용하고 있었지만, @EntityGraph를 활용하여 연관된 User 엔티티를 함께 조회하도록 리팩토링했습니다.
    
    → @EntityGraph(attributePaths = {"user"})를 사용함으로써, JPA가 연관된 엔티티를 Lazy 로딩이 아닌 한 번의 쿼리로 조회하게 되어 성능을 개선할 수 있었습니다.

---

### Lv 3. 테스트코드 연습

1) 테스트 코드 연습 - 1

- PassEncoderTest의 테스트 메서드에서 예상된 값과 실제 결과가 일치하지 않아 테스트가 실패하는 문제를 해결했습니다.

    → PasswordEncoder.matches() 메서드의 정확한 동작 방식에 맞춰, 실제 인코딩된 비밀번호를 기준으로 비교하도록 테스트 코드를 리팩토링했습니다.

2) 테스트 코드 연습 - 2

- 테스트 코드에서 실제로 발생하는 예외는 NullPointerException이 아닌 InvalidRequestException이었기 때문에,
메서드명을 manager_목록_조회_시_Todo가_없다면_IRE_에러를_던진다()로 수정하고,
예외 타입과 메시지를 정상적으로 검증할 수 있도록 테스트 코드를 리팩토링했습니다.

3) 테스트 코드 연습 - 3
- 기존에는 Todo 엔티티의 User가 null일 경우에 대한 처리가 없어 테스트가 실패했지만,
   서비스 로직에서 User가 null인 경우 예외를 던지도록 수정하여
   todo의_user가_null인_경우_예외가_발생한다() 테스트가 정상 동작하도록 리팩토링했습니다.
  
---

### 4. API 로깅

- AdminInterceptor를 구현하여 어드민 API 접근 시 요청 로그를 남기도록 구성했습니다.

- 요구사항을 충족한 구현 포인트:

  - userRole 정보를 HttpServletRequest에서 받아와 어드민인지 확인

  - 비어있는 role은 401 Unauthorized, 어드민이 아닌 경우 403 Forbidden 반환

  - 어드민 인증을 통과한 경우, 요청 URL과 접근 시각을 로그로 기록 (log.info)

- 대상 컨트롤러:

  - CommentAdminController.deleteComment()

  - UserAdminController.changeUserRole()

  - Interceptor를 통해 사전 인증 처리 + 요청 로깅을 분리하며 유지보수성과 보안성을 높였습니다.

---
### Lv 5. 위 제시된 기능 이외 ‘내’가 정의한 문제와 해결 과정

> 정리한 블로그: https://sintory-04.tistory.com/260

---
### Lv 6. 테스트 커버리지

#### TestCoverage

![TestCoverage](https://github.com/user-attachments/assets/879151f3-e374-4805-8392-78a945e8b591)

---

1) TodoServiceTest

(1) `saveTodo_successfully()`

- 기능: 새로운 Todo 항목을 정상적으로 저장하는 기능을 테스트합니다.

- 검증 내용

  - 외부 날씨 API(WeatherClient)를 통해 날씨 정보를 가져옵니다.

  - Todo 저장 후 반환 객체(TodoSaveResponse)에 날씨, 사용자 정보가 정확히 포함됩니다.

(2) `getTodos_successfully()`

- 기능: 전체 Todo 리스트를 페이지 단위로 조회하는 기능을 테스트합니다.

- 검증 내용

  - 저장된 Todo 목록을 최근 수정일 기준으로 정렬하여 페이징 처리합니다.

  - 조회된 각 Todo의 제목과 날씨 정보가 정확히 매핑되어 반환됩니다.

(3) `getTodo_successfully()`

- 기능: 특정 ID에 해당하는 단일 Todo 항목을 조회하는 기능을 테스트합니다.

- 검증 내용

  - 주어진 ID로 Todo를 조회하고, 해당 Todo에 연결된 사용자 정보와 함께 반환합니다.

  - 제목, 날씨, 사용자 이메일 등의 세부 정보가 정확히 조회됩니다.

2) AuthServiceTest

(1) `signup_successfully()`

- 기능: 사용자가 정상적으로 회원 가입을 할 수 있는지 테스트합니다.

- 검증 내용

  - 이메일이 존재하지 않는지 확인 후, 비밀번호를 암호화하여 저장합니다.

  - 회원 정보가 저장되고, `JWT` 토큰이 생성되어 반환되는지 검증합니다.

(2) `signup_failsWhenUserEmailDoesExist()`

- 기능: 이미 존재하는 이메일로 회원 가입을 시도할 경우 실패하는지 테스트합니다.

- 검증 내용

  - 이메일이 이미 존재할 경우 `InvalidRequestException` 예외가 발생하고, 적절한 에러 메시지가 전달되는지 확인합니다.

(3) `signin_successfully()`

- 기능: 사용자가 정상적으로 로그인을 할 수 있는지 테스트합니다.

- 검증 내용

  - 사용자가 입력한 이메일과 비밀번호가 일치하면 `JWT` 토큰을 생성하여 반환되는지 확인합니다.

(4) `signin_failsWhenUserDoesNotExist()`

- 기능: 가입되지 않은 이메일로 로그인 시도가 있을 경우 실패하는지 테스트합니다.

- 검증 내용

  - 사용자가 가입되지 않은 이메일을 입력할 경우 `InvalidRequestException` 예외가 발생하고, 적절한 에러 메시지가 전달되는지 확인합니다.

(5) `signin_fails_whenPasswordDoesNotMatch()`

- 기능: 이메일과 비밀번호가 일치하지 않는 경우 로그인에 실패하는지 테스트합니다.

- 검증 내용

  - 사용자가 잘못된 비밀번호를 입력할 경우 `AuthException` 예외가 발생하고, "잘못된 비밀번호입니다." 메시지가 전달되는지 확인합니다.

3) CommentAdminServiceTest

(1) `deleteComment_successfully()`

- 기능: 어드민 사용자가 댓글을 정상적으로 삭제할 수 있는지 테스트합니다.

- 검증 내용

  - `CommentRepository` 에서 해당 댓글 `ID` 가 존재하는지 확인합니다.

  - 댓글이 존재할 경우 `deleteById()` 메서드가 호출되어 댓글이 삭제되는지 검증합니다.

  - 삭제 후, 더 이상 다른 메서드가 호출되지 않도록 `verifyNoMoreInteractions()` 를 사용하여 호출을 검증합니다.

(2) `deleteComment_failsWhenCommentDoesNotExist()`
   
- 기능: 댓글 식별자가 존재하지 않아 댓글 삭제가 실패하는 경우를 테스트합니다.

- 검증 내용

  - 댓글 `ID` 가 존재하지 않을 경우 `InvalidRequestException` 예외가 발생해야 합니다.

  - 예외가 발생하면 `deleteById()` 메서드는 호출되지 않음을 검증합니다.

4) WeatherClientTest

(1) `getTodayWeather_successfully()`

- 기능: 외부 날씨 `API` 에서 오늘 날짜에 해당하는 날씨 정보를 정상적으로 가져오는 기능을 테스트합니다.

- 검증 내용

  - `RestTemplate` 을 통해 받은 응답 중, 오늘 날짜(MM-dd)에 해당하는 날씨 정보가 정확히 반환되는지 확인합니다.

  - 응답 데이터 내에서 날짜 필터링 로직이 올바르게 동작하는지를 검증합니다.

(2) `getTodayWeather_WhenTodayWeatherNotFound_ThrowsServerException()`

- 기능: 응답 데이터에 오늘 날짜에 해당하는 날씨 정보가 존재하지 않을 경우 예외 처리되는지 테스트합니다.

- 검증 내용

  - 오늘 날짜가 포함되지 않은 응답을 받을 경우 `ServerException` 이 발생해야 합니다.

  - 에러 처리 로직이 정상적으로 작동하는지 확인합니다.

5) AdminInterceptorTest

(1) `AdminInterceptor_successfully()`

- 기능: `AdminInterceptor` 가 정상적으로 작동하여, 로거가 적절하게 출력되는지 테스트합니다.

- 검증 내용

  - `request` 객체에 `userRole` 을 "ADMIN"으로 설정합니다.

  - 인터셉터가 정상적으로 `preHandle` 메서드를 실행하고, `Logger` 객체의 `info` 메서드가 호출되는지 확인합니다.

  - `preHandle` 이 `true` 를 반환하는지 검증하여, 인터셉터가 요청을 정상적으로 처리하는지 확인합니다.

