# spring-data
http://projects.spring.io/spring-data-jpa/
CrudRepository interface

### core
#### CrudRepository
``` java
public interface CrudRepository<T, ID extends Serializable>
    extends Repository<T, ID> {

    <S extends T> S save(S entity); //Saves the given entity.

    T findOne(ID primaryKey);     //Returns the entity identified by the given id.  

    Iterable<T> findAll();       //Returns all entities   

    Long count();                //Returns the number of entities.   

    void delete(T entity);       //Deletes the given entity.   

    boolean exists(ID primaryKey);  //Indicates whether an entity with the given id exists.

    // … more functionality omitted.
   Page<T> findAll(Pageable pageable);   //repository.findAll(new PageRequest(1, 20));
   Long countByLastname(String lastname);

   Long deleteByLastname(String lastname);

   List<User> removeByLastname(String lastname);
}
``` 

#### query methods
1.实现接口  
``` java  
interface PersonRepository extends Repository<Person, Long> { … }  
``` 

2.编写方法    
``` java
interface PersonRepository extends Repository<Person, Long> {
  List<Person> findByLastname(String lastname);
}
``` 

3.编写配置  
``` java 	
@Configuration
@EnableJpaRepositories("com.acme.repositories")
class ApplicationConfiguration {

  @Bean
  public EntityManagerFactory entityManagerFactory() {
    // …
  }
}
``` 


4.使用
``` java
public class SomeClient {

  @Autowired
  private PersonRepository repository;

  public void doSomething() {
    List<Person> persons = repository.findByLastname("Matthews");
  }
}
``` 

#### Defining query methods

Query creation
``` java
public interface PersonRepository extends Repository<User, Long> {

  List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

  // Enables the distinct flag for the query
  List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
  List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

  // Enabling ignoring case for an individual property
  List<Person> findByLastnameIgnoreCase(String lastname);
  // Enabling ignoring case for all suitable properties
  List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

  // Enabling static ORDER BY for a query
  List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
  List<Person> findByLastnameOrderByFirstnameDesc(String lastname);

  // Property expressions
  List<Person> findByAddressZipCode(ZipCode zipCode);//、x.address.zipCode

  //Special parameter handling
Page<User> findByLastname(String lastname, Pageable pageable);

Slice<User> findByLastname(String lastname, Pageable pageable);

List<User> findByLastname(String lastname, Sort sort);

List<User> findByLastname(String lastname, Pageable pageable);

//Limiting query results

User findFirstByOrderByLastnameAsc();

User findTopByOrderByAgeDesc();

Page<User> queryFirst10ByLastname(String lastname, Pageable pageable);

Slice<User> findTop3ByLastname(String lastname, Pageable pageable);

List<User> findFirst10ByLastname(String lastname, Sort sort);

List<User> findTop10ByLastname(String lastname, Pageable pageable);


//Streaming query results
@Query("select u from User u")
Stream<User> findAllByCustomQueryAndStream();

Stream<User> readAllByFirstnameNotNull();

@Query("select u from User u")
Stream<User> streamAllPaged(Pageable pageable);


//异步
@Async
Future<User> findByFirstname(String firstname);               

@Async
CompletableFuture<User> findOneByFirstname(String firstname); 

@Async
ListenableFuture<User> findOneByLastname(String lastname);    
}

``` 



Supported keywords inside method names            

|Keyword	|Sample	|JPQL snippet|
|---|---|---|   
|And|findByLastnameAndFirstname|… where x.lastname = ?1 and x.firstname = ?2|
|Or|findByLastnameOrFirstname|… where x.lastname = ?1 or x.firstname = ?2|
|Is,Equals|findByFirstname,findByFirstnameIs,findByFirstnameEquals|… where x.firstname = ?1|
|Between |findByStartDateBetween |… where x.startDate between ?1 and ?2|
|LessThan|findByAgeLessThan|… where x.age < ?1|
|LessThanEqual|findByAgeLessThanEqual|… where x.age <= ?1|
|GreaterThan |findByAgeGreaterThan |… where x.age > ?1|
|GreaterThanEqual|findByAgeGreaterThanEqual|… where x.age >= ?1|
|After|findByStartDateAfter|… where x.startDate > ?1|
|Before|findByStartDateBefore|… where x.startDate < ?1|
|IsNull|findByAgeIsNull|… where x.age is null|
|IsNotNull,NotNull|findByAge(Is)NotNull|… where x.age not null|
|Like|findByFirstnameLike|… where x.firstname like ?1|
|NotLike|findByFirstnameNotLike|… where x.firstname not like ?1|
|StartingWith|findByFirstnameStartingWith|… where x.firstname like ?1 (parameter bound with appended %)|
|EndingWith|findByFirstnameEndingWith|… where x.firstname like ?1 (parameter bound with prepended %)|
|Containing|findByFirstnameContaining|… where x.firstname like ?1 (parameter bound wrapped in %)|
|OrderBy|findByAgeOrderByLastnameDesc|… where x.age = ?1 order by x.lastname desc|
|Not|findByLastnameNot|… where x.lastname <> ?1|
|In |findByAgeIn(Collection<Age> ages) |… where x.age in ?1|
|NotIn| findByAgeNotIn(Collection<Age> age)| … where x.age not in ?1|
|True |findByActiveTrue()|… where x.active = true|
|False |findByActiveFalse() |… where x.active = false|
|IgnoreCase |findByFirstnameIgnoreCase |… where UPPER(x.firstame) = UPPER(?1)|



### Using @Query
``` java
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u where u.emailAddress = ?1")
  User findByEmailAddress(String emailAddress);

@Query("select u from User u where u.firstname like %?1")
  List<User> findByFirstnameEndsWith(String firstname);

 //nativeQuery 
  @Query(value = "SELECT * FROM USERS WHERE EMAIL_ADDRESS = ?1", nativeQuery = true)
  User findByEmailAddress(String emailAddress);

  @Query(value = "SELECT * FROM USERS WHERE LASTNAME = ?1",
    countQuery = "SELECT count(*) FROM USERS WHERE LASTNAME = ?1",
    nativeQuery = true)
  Page<User> findByLastname(String lastname, Pageable pageable);

//Using named parameters
  @Query("select u from User u where u.firstname = :firstname or u.lastname = :lastname")
  User findByLastnameOrFirstname(@Param("lastname") String lastname,
                                 @Param("firstname") String firstname);
}
``` 

### update
``` java
@Modifying
@Query("update User u set u.firstname = ?1 where u.lastname = ?2")
int setFixedFirstnameFor(String firstname, String lastname);
``` 

### delete
``` java
void deleteByRoleId(long roleId);

  @Modifying
  @Query("delete from User u where user.role.id = ?1")
  void deleteInBulkByRoleId(long roleId);

``` 
### Projection

我的理解是按指定的格式返回数据，和建立虚拟列
``` java
interface FullNameAndCountry {

  @Value("#{target.firstName} #{target.lastName}")
  String getFullName();

  @Value("#{target.address.country}")
  String getCountry();
}

``` 
### StoredProcedure
``` java
@Entity
@NamedStoredProcedureQuery(name = "User.plus1", procedureName = "plus1inout", parameters = {
  @StoredProcedureParameter(mode = ParameterMode.IN, name = "arg", type = Integer.class),
  @StoredProcedureParameter(mode = ParameterMode.OUT, name = "res", type = Integer.class) })
public class User {}



@Procedure("plus1inout")
Integer explicitlyNamedPlus1inout(Integer arg);

``` 
### Auditing
``` java
class Customer {

  @CreatedBy
  private User user;

  @CreatedDate
  private DateTime createdDate;

  // … further properties omitted
}
``` 

### Spring Data REST
Spring Data REST是基于Spring Data的repository之上，可以把 repository 自动输出为REST资源，目前支持Spring Data JPA、Spring Data MongoDB、Spring Data Neo4j、Spring Data GemFire、Spring Data Cassandra的 repository 自动转换成REST服务。注意是自动。简单点说，Spring Data REST把我们需要编写的大量REST模版接口做了自动化实现。

1.入门
```java
@RepositoryRestResource(path="user")
public interface UserRepository extends JpaRepository<User, Long>{  
}
```
访问http://127.0.0.1:8080/api/customer   可以查看
http://127.0.0.1:8080/api/customer?page=2&size=2  可以查询数据


如上已经支持了普通的增删改查了。
2.自定义
1.屏蔽自动化方法    
在实际生产环境中，不会轻易的删除用户数据，此时我们不希望DELETE的提交方式生效，可以添加@RestResource注解，并设置exported=false


2.自定义输出字段    
可以使用 @JsonIgnore，@JsonView也可以参考@Projections

test:
访问http://localhost:8080/api/browser/index.html 可以查看服务列表





