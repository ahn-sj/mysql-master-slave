## mysql-master-slave

---

<br/>

일반적으로 디비에 대한 트래픽 분산을 위해서 Mysql Replication 를 통해서 트래픽 집중 문제를 해결할 수 있다.

**Master**에게는 데이터 동시성이 아주 높게 요구되는 트랜잭션을 담당하고,
**Slave**에게는 읽기만 데이터 동시성이 꼭 보장될 필요는 없는 경우에 읽기 전용으로 데이터를 가져오게 한다.

즉, Replication을 통해 읽기 전용으로 트랜잭션을 사용하여 DB에 대한 트래픽을 분산할 수 있다.

<br/>

---

<br/>

### STEP 01. AWS 설정

01-1. Read Replica 생성
![image](https://user-images.githubusercontent.com/64416833/205101968-277811d8-96bc-4ed2-bb8e-01c5a31fa333.png)

<br/>

01-2. 읽기 전용 복제본 생성 옵션

1) DB 인스턴스 식별자 작성
![image](https://user-images.githubusercontent.com/64416833/205102510-0915291a-b0f2-4d51-bcf7-5fec04eaf8db.png)

2) 스토리지 자동 조정 활성화 OFF
![image](https://user-images.githubusercontent.com/64416833/205128642-9f1ee343-e8b2-4618-a335-dab1f45228ed.png)

3) 퍼블릭 액세스 가능
![image](https://user-images.githubusercontent.com/64416833/205128855-f744796e-163d-425e-ad63-34d05aee3769.png)


3. 읽기 전용 복제본 2개 생성
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

### [실행결과]
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

---

<br/>

### [참고자료]

https://huisam.tistory.com/entry/mysql-replication<br/>
https://github.com/backtony/blog-code/blob/master/mysql-master-slave/src/main/resources/application.yml<br/>
https://junghwanta.tistory.com/16
