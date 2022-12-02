## mysql-master-slave

---

<br/>

웹 계층은 로드밸런서로 인해 부하 분산을 한다면 데이터 계층은 어떻게 부하를 분산해야 하는가??<br/>
데이터 계층의 부하를 분산시키는 방법으로 주(Master)-부(Slave)관계를 설정하고 데이터 원본은 주 서버에, 사본은 부 서버에 저장하는 방식이 있다. 즉, **데이터베이스 다중화**라는 기술로 해결할 수 있다.

쓰기 연산(write operation)은 마스터에서만 지원하고, 부 데이터베이스는 주 데이터베이스로부터 그 사본을 전달받아 읽기 연산(read opertation)만을 지원한다.

데이터베이스를 변경하는 명령어들, 가령 insert, delete, update 등은 주 데이터베이스로만 전달되어야 한다. 대부분의 애플리케이션은 읽기 연산의 비중이 쓰기 연산보다 훨씬 높다. 따라서 통상 부 데이터베이스의 수가 주 데이터베이스의 수보다 많다.

데이터베이스를 다중화하면 다음과 같은 이점이 있다.

1. 더 나은 성능: 주-부 다중화 모델에서 모든 데이터 변경 연산은 주 데이터베이스 서버로만 전달되는 반면 읽기 연산은 부 데이터베이스 서버들로 분산된다. 다시 말해 병렬로 처리될 수 있는 질의(query)의 수가 늘어나므로, 성능이 좋아진다.

2. 안전성(reliability): 자연 재해 등의 이유로 데이터베이스 서버 가운데 일부가 파괴되어도 데이터가 보존될 수 있다.

3. 가용성(availability): 데이터를 여러 지역에 복제해 둠으로써, 하나의 데이터베이스 서버에 장애가 발생하더라도 다른 서버에 있는 데이터를 가져와 계속 서비스를 할 수 있게 된다.

데이터베이스를 다중화하게 될 경우의 구성은 다음과 같다.

![image](https://user-images.githubusercontent.com/64416833/205225456-e89271b6-1333-4976-8658-7859106b40a4.png)



<br/>

---

<br/>

### STEP 01. AWS 설정

01-1. Read Replica 생성

![image](https://user-images.githubusercontent.com/64416833/205101968-277811d8-96bc-4ed2-bb8e-01c5a31fa333.png)

<br/>

01-2. 읽기 전용 복제본 생성 옵션

1) DB 인스턴스 식별자 작성<br>
![image](https://user-images.githubusercontent.com/64416833/205102510-0915291a-b0f2-4d51-bcf7-5fec04eaf8db.png)

2) 스토리지 자동 조정 활성화 OFF<br>
![image](https://user-images.githubusercontent.com/64416833/205128642-9f1ee343-e8b2-4618-a335-dab1f45228ed.png)

3) 퍼블릭 액세스 가능<br>
![image](https://user-images.githubusercontent.com/64416833/205128855-f744796e-163d-425e-ad63-34d05aee3769.png)


3. 읽기 전용 복제본 2개 생성<br>
![image](https://user-images.githubusercontent.com/64416833/205104055-cf751f04-ea56-48c3-90c6-61bd95152295.png)

<br/>

---


<br/>

### STEP 02. 프로젝트 초기 설정

![image](https://user-images.githubusercontent.com/64416833/205096022-316eeb40-d0de-4723-9aa8-7e107bd094f1.png)

<br/>

---

<br/>

### STEP 03. yml 설정 및 Config 파일 추가

https://velog.io/@backtony/Spring-AWS-RDS로-MySQL-Replication-적용하기-feat.-다중-AZ

<br/>

---

<br/>

### [실행결과 1]
1. /test/java/aws/masterslave/service/ProductServiceSimpleTest.SLAVE_읽기()

- Case: READ
- Required: @Transaction(readOnly=True)
- Result: Slave

    ![image](https://user-images.githubusercontent.com/64416833/205137490-8537d54a-8b5a-479b-9ac9-89060f0fdd0f.png)

<br/>

2. /test/java/aws/masterslave/service/ProductServiceTest.비관적_락()

- Case: PessimisticLock
- Required: STOCK = 1000, THREAD_COUNT = 900
- Result: STOCK - THREAD_COUNT = 100

    ![image](https://user-images.githubusercontent.com/64416833/205138549-184dc2f7-8d02-425a-9be3-37d49e4bcf21.png)

<br/>

3. JMeter
- Case: time: 7sec , Request: 200

    ![image](https://user-images.githubusercontent.com/64416833/205143335-e9c1416e-cf0b-48bc-9da6-d26662eb9d33.png)

<br/>

---

<br/>

### [실행결과 2]

쓰레드와 커넥션의 개수를 제한한 결과 비관적 락과 레디슨에 대해 정상적인 응답을 받을 수 있었다.

![image](https://user-images.githubusercontent.com/64416833/205220611-6e474215-7f68-4476-bd74-e9660a91573a.png)

<br/>

#### 1. 레디슨

- Required: 2000Request STOCK = 10000, THREAD_COUNT = 2000
- Env: lock.tryLock(10, 5, TimeUnit.SECONDS)
- TPS: lowest 18 / highst 24
- Result: 89sec

1-1) JMeter TPS (Redisson)

![image](https://user-images.githubusercontent.com/64416833/205221727-70a27393-bb97-4a66-8586-493a71133644.png)

1-2) Spring Boot Console (Redisson)

![image](https://user-images.githubusercontent.com/64416833/205221808-9bf22f5a-7ddd-4ada-8092-7f6b17476dcd.png)

<br/>

---

<br/>

#### 2. 비관적 락

- Required: 2000Request STOCK = 10000, THREAD_COUNT = 2000
- TPS: lowest 57 / highest 64
- Result: 32sec


1-1) JMeter TPS (Pessimistic Lock)

![image](https://user-images.githubusercontent.com/64416833/205222666-4b0f64ce-a9e4-4ca0-a314-6814948ea9bd.png)

1-2) Spring Boot Console (Pessimistic Lock)

![image](https://user-images.githubusercontent.com/64416833/205222691-90f43b7c-694a-4f55-8366-31b5dba452a9.png)

<br/>

---

<br/>

### [참고자료]

https://huisam.tistory.com/entry/mysql-replication<br/>
https://github.com/backtony/blog-code/blob/master/mysql-master-slave/src/main/resources/application.yml<br/>
https://junghwanta.tistory.com/16<br/>
https://velog.io/@sihyung92/how-does-springboot-handle-multiple-requests<br/>
https://inf.run/PKxY<br/>
http://www.yes24.com/Product/Goods/102819435<br/>