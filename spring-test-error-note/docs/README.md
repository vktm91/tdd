## 테스트의 목적

1. 회귀 버그(regression) 방지
2. 좋은 설계(유연한 설계)로 개선
    - 테스트를 쉽게 만들어준다.
    - 테스트를 결정적이게 만들어준다.

테스트와 설계는 긴밀하게 연결되어 있다. 테스트를 고민하면 설계가 개선되고, 설계를 고민하면 테스트가 쉬워진다.

테스트 코드를 작성하다 보면 테스트가 외부 시스템에 의존하는 상황을 피하라는 신호를 보낸다.

**주의!** 레거시에 테스트를 넣으려면 코드 개선이 필요하다 (레거시 코드에 테스트를 넣는 것이 TDD는 아니다)


### TDD Three Steps

**RED**
1. 깨지는 테스트를 먼저 작성한다. (테스트가 실패 여부를 확인해야 한다.)

**GREEN**
2. 깨지는 테스트를 성공시킨다.

**BLUE**
3. 리팩토링한다. (이전 단계에서 테스트 성공을 확인했기 때문에 파괴적인 리팩토링을 해도 테스트를 돌려보면 된다.)


### TDD 장점과 단점

#### 장점

1. 깨지는 테스트를 먼저 작성해야 하기 때문에, 인터페이스를 먼저 만드는 것이 강제된다. 즉 객체지향의 핵심 원리 중 하나인 행동에 집중하게 된다.
2. 장기적인 관점에서 개발 비용 감소

#### 단점

1. 초기 개발 비용 증가
2. 테스트 코드 작성 난이도에 따른 진입 장벽

### 고민

1. **무의미한 테스트 (Recap)**
    - 너무 명확해서 테스트가 필요 없는 부분에는 꼭 테스트를 작성할 필요가 없을 듯 하다.
    - 중요한 로직을 잘 구분해서 그 부분에 테스트를 넣는 것이 좋다.

2. **느리고 쉽게 깨지는 테스트**
    - 테스트가 많아지는 것과 병렬 처리, H2 연결 등도 고려해야 한다.

3. **테스트가 불가한 코드**
    - 로그인 시점과 같이 기대값 예측 불가능한 경우


## 테스트의 필요성

`어떻게 테스트를 할까?`를 고민하다 보면 `SOLID` 원칙이 자연스럽게 따라오게 된다.

## SOLID 원칙

### S: 단일 책임 원칙 (Single Responsibility Principle)

- 하나의 클래스는 하나의 책임만을 가져야 한다.
- 테스트는 명료하고 간단하게 작성해야 하기 때문에 단일 책임 원칙을 지키게 된다.
- 테스트가 너무 많아져서 이게 무슨 목적의 클래스인지 눈에 안 들어오는 지점이 생긴다.
- 이 때가 클래스를 분할해야 하는 시점이며, 그러면서 책임이 자연스럽게 분배된다.

### O: 개방-폐쇄 원칙 (Open/Closed Principle)

- 확장에는 열려 있어야 하고, 수정에는 닫혀 있어야 한다.
- 테스트 컴포넌트와 프로덕션 컴포넌트를 나눠 작업하게 되고, 필요에 따라 이 컴포넌트를 자유자재로 탈부착이 가능하게 개발하게 됨.

### L: 리스코프 치환 원칙 (Liskov Substitution Principle)

- 슈퍼클래스의 계약을 서브 클래스가 제대로 치환하는지 확인해야 한다.
- 이상적으로 테스트는 모든 케이스에 대해 커버하고 있으므로, 서브 클래스에 대한 치환 여부를 테스트가 알아서 판단해줌.

### I: 인터페이스 분리 원칙 (Interface Segregation Principle)

- 테스트는 그 자체로 인터페이스를 직접 사용해볼 수 있는 환경이다.
- 불필요한 의존성을 실제로 확인할 수 있는 샌드박스이다.
- 인터페이스가 너무 많아서 뭘 호출해야 할지 모르겠을 때가 생긴다.
- 이 때가 인터페이스를 분리해 줘야 하는 시점이다.

### D: 의존성 역전 원칙 (Dependency Inversion Principle)

- 가짜 객체를 이용하여 테스트를 작성하려면, 의존성이 역전되어 있어야 하는 경우가 생긴다.


### 테스트 3분류

api 테스트 / 통합 테스트 / 단위 테스트 → 이 분류 방식은 애매함..

large 테스트 / medium 테스트 /

- small 테스트 (= 단위 테스트) 중요!!

  단일 서버 / 단일 프로세스 / 단일 스레드에서 돌아가는 테스트

  Disk IO가 있으면 안 되고, Blocking call이 있어서도 안 된다.

  예를들어 Thread sleeop이 있으면 안 된다.

  소형 테스트가 전체 테스트의 80%는 차지하는 게 좋다.

- medium 테스트

  단일 서버 / 멀티 프로세스 / 멀티 스레드

  h2같은 테스트용 db 사용 가능

- 멀티 서버 / 멀티 프로세스 / 멀티 스레드
