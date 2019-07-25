## 1. API文档管理
当下互联网开发前后端分离是趋势，大点的企业一般存在对外提供企业级openapi，比如百度、腾讯、阿里云，或者第三方软件工具如支付系统等，API文档管理成为一个难题。现有API管理工具有：
1. postman(免费版功能人数有限)
2. eolinker（免费版功能人数有限）
3. 阿里云RAP（开源）
4. appiza（免费版功能人数有限）
5. easyapi（免费版功能人数有限）
6. showdoc（国内的api 管理工具，比较简洁）
7. swagger（开源，基于注释生成文档）
8. apiview(html，格式，收费)
9. apidoc（类似swagger）

很多首付版的API文档管理功能确实强大，测试，权限等等。但是大一点的企业，一般不会把接口文档暴漏在外，基本是自己搭建一个API管理平台。这里主要介绍下spring rest docs。

## 2. Spring Rest Docs

Spring REST Docs 默认使用Asciidoctor处理纯文本并生成HTML，基于Spring MVC测试生成文档。

### 2.1 依赖
- Java 8
- Spring Framework 5 (5.0.2 or later)

### 2.2 asciidoc

**生成文档样例：**

[![TIM20190725101540.png](http://www.wailian.work/images/2019/07/25/TIM20190725101540.png)](http://www.wailian.work/image/5Qt1u4)

rest docs默认有以下asciidoc模版文档，模版文档在spring-rests-docs-core.xxx.jar的org/springframework/restdocs/templates/asciidoctor目录下。

模版文档|描述
---|---
curl-request.adoc|包含与 正在记录curl的MockMvc调用等效的命令
httpie-request.adoc|包含与 正在记录HTTPie的MockMvc调用等效的命令
http-request.adoc|包含与MockMvc正在记录的调用等效的HTTP请求
http-response.adoc|包含返回的HTTP响应
request-body.adoc|包含已发送的请求的正文
response-body.adoc|包含返回的响应的主体

可以看到对于restful接口来说，请求参数、包头、响应等，几个重要的都包含了。但是有时候我们像不使用默认的模版生成，需要自定义的asciidoc文档，自定义很简单，只需在自己的项目src/test/resources/org/springframework/restdocs/templates/asciidoctor覆盖相同文件即可。


### 2.3 Hello World

**配置asciidoc文档**

[![TIM20190725104202.th.png](http://www.wailian.work/images/2019/07/25/TIM20190725104202.th.png)](http://www.wailian.work/image/5Qtyx5)

API文档首页，index.adoc
```
= XXX项目api文档
作者 - XXX项目组;
:sectnums:
:sectnumlevels: 5
:toc: left
:toclevels: 3
:page-layout: docs
:doctype: book
:toc-title: 章节
:icons: font
:source-highlighter: highlightjs

[[examples]]
== 列表
include::find-all/index.adoc[]
include::find-one/index.adoc[]
include::save/index.adoc[]

```

find-all/defaulting.adoc
```
[[users_find_all_defaulting]]
==== 默认
When leaving away the `page` and `size` parameters, they're being defaulted to 1 and 10 respectively.

[[users_find_all_defaulting_request]]
===== 请求
include::{snippets}/find-all-should-default-to-page-one-and-size-ten/http-request.adoc[]

[[users_find_all_defaulting_response]]
===== 响应
The response is the same as the successful response (see <<users_find_all_success_response>>).

[[users_find_all_defaulting_curl]]
===== cURL
include::{snippets}/find-all-should-default-to-page-one-and-size-ten/curl-request.adoc[]
```

**gradle配置：**

```
buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.1.6.RELEASE")
        classpath("org.asciidoctor:asciidoctor-gradle-plugin:1.5.3")
    }
}

plugins {
    id "io.spring.dependency-management" version "1.0.5.RELEASE"
}

apply plugin: 'java'
apply plugin: 'org.asciidoctor.convert'
apply plugin: 'eclipse'
apply plugin: 'idea'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'


group = 'com.czarea'
version = '1.0'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

ext {
    set('snippetsDir', file("build/snippets"))
}

dependencies {
    compileOnly 'org.projectlombok:lombok:1.18.6'
    annotationProcessor 'org.projectlombok:lombok:1.18.6'

    runtime 'org.hsqldb:hsqldb:2.4.1'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.alibaba:fastjson:1.2.56'
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

asciidoctor {
    sourceDir 'src/main/asciidoc'
    attributes 'snippets': file('build/snippets')
    inputs.dir snippetsDir
    dependsOn test
}
```


在运行 Gradle 构建之后，可以在 build/asciidoc/html5 下面找到生成的 index.html 文件，这是最终的 API 文档，可以直接在浏览器中查看。

**前端校验：**

```
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    @NotNull(message = "Last name should not be empty")
    @Size(min = 1, max = 60, message = "Last name should be between 1 and 60 characters")
    private String lastName;
    @Size(max = 60, message = "Middle name should be at most 60 characters")
    private String middleName;
    @NotNull(message = "First name should not be empty")
    @Size(min = 1, max = 60, message = "First name should be between 1 and 60 characters")
    private String firstName;
    @NotNull(message = "Date of birth should not be empty")
    @Past(message = "Date of birth should be in the past")
    private LocalDate dateOfBirth;
    @NotNull(message = "The amount of siblings should not be empty")
    @PositiveOrZero(message = "The amount of siblings should be positive")
    private Integer siblings;
}
```

**Mock测试**

```
@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureRestDocs(outputDir = "build/snippets")
public class SpringRestDocsApplicationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository repository;

    @Test
    public void findAllShouldReturnListOfUsers() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Lists.newArrayList(
            new User(1L, "Doe", null, "John", LocalDate.of(2010, 1, 1), 0),
            new User(2L, "Doe", "Foo", "Jane", LocalDate.of(1999, 12, 31), 2))));
        mockMvc.perform(get("/api/user?page=2&size=5").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(1)))
            .andExpect(jsonPath("$.[0].lastName", is("Doe")))
            .andExpect(jsonPath("$.[0].middleName", nullValue()))
            .andExpect(jsonPath("$.[0].firstName", is("John")))
            .andExpect(jsonPath("$.[0].dateOfBirth", is("2010-01-01")))
            .andExpect(jsonPath("$.[0].siblings", is(0)))
            .andExpect(jsonPath("$.[1].id", is(2)))
            .andExpect(jsonPath("$.[1].lastName", is("Doe")))
            .andExpect(jsonPath("$.[1].middleName", is("Foo")))
            .andExpect(jsonPath("$.[1].firstName", is("Jane")))
            .andExpect(jsonPath("$.[1].dateOfBirth", is("1999-12-31")))
            .andExpect(jsonPath("$.[1].siblings", is(2)))
            .andExpect(header().longValue("X-Users-Total", 2L))
            .andDo(document("{method-name}", pageParameters(), userCollection(), pageHeaders()));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(captor.getValue().getPageSize()).isEqualTo(5);
    }

    @Test
    public void findAllShouldDefaultToPageOneAndSizeTen() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Lists.newArrayList(
            new User(1L, "Doe", null, "John", LocalDate.of(2010, 1, 1), 0),
            new User(2L, "Doe", "Foo", "Jane", LocalDate.of(1999, 12, 31), 2))));
        mockMvc.perform(get("/api/user").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(1)))
            .andExpect(jsonPath("$.[0].lastName", is("Doe")))
            .andExpect(jsonPath("$.[0].middleName", nullValue()))
            .andExpect(jsonPath("$.[0].firstName", is("John")))
            .andExpect(jsonPath("$.[0].dateOfBirth", is("2010-01-01")))
            .andExpect(jsonPath("$.[0].siblings", is(0)))
            .andExpect(jsonPath("$.[1].id", is(2)))
            .andExpect(jsonPath("$.[1].lastName", is("Doe")))
            .andExpect(jsonPath("$.[1].middleName", is("Foo")))
            .andExpect(jsonPath("$.[1].firstName", is("Jane")))
            .andExpect(jsonPath("$.[1].dateOfBirth", is("1999-12-31")))
            .andExpect(jsonPath("$.[1].siblings", is(2)))
            .andExpect(header().longValue("X-Users-Total", 2L))
            .andDo(document("{method-name}", pageParameters(), userCollection()));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(captor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    public void findAllShouldReturnErrorIfPageIsNegative() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Lists.newArrayList(
            new User(1L, "Doe", null, "John", LocalDate.of(2010, 1, 1), 0),
            new User(2L, "Doe", "Foo", "Jane", LocalDate.of(1999, 12, 31), 2))));
        mockMvc.perform(get("/api/user?page=-1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("findAll.page")))
            .andExpect(jsonPath("$.[0].message", is("Page number should be a positive number")))
            .andDo(document("{method-name}", pageParameters(), apiError()));
    }

    @Test
    public void findAllShouldReturnErrorIfPageSizeIsNegative() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Lists.newArrayList(
            new User(1L, "Doe", null, "John", LocalDate.of(2010, 1, 1), 0),
            new User(2L, "Doe", "Foo", "Jane", LocalDate.of(1999, 12, 31), 2))));
        mockMvc.perform(get("/api/user?size=-1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("findAll.size")))
            .andExpect(jsonPath("$.[0].message", is("Page size should be a positive number")))
            .andDo(document("{method-name}", pageParameters(), apiError()));
    }

    @Test
    public void saveShouldReturnUser() throws Exception {
        when(repository.saveAndFlush(any())).thenReturn(new User(3L, "Doe", "Bar", "Joe", LocalDate.of(2000, 1, 1), 4));
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":4}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.lastName", is("Doe")))
            .andExpect(jsonPath("$.middleName", is("Bar")))
            .andExpect(jsonPath("$.firstName", is("Joe")))
            .andExpect(jsonPath("$.dateOfBirth", is("2000-01-01")))
            .andExpect(jsonPath("$.siblings", is(4)))
            .andDo(document("{method-name}", userInput(), user()));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getLastName()).isEqualTo("Doe");
        assertThat(captor.getValue().getMiddleName()).isEqualTo("Bar");
        assertThat(captor.getValue().getFirstName()).isEqualTo("Joe");
        assertThat(captor.getValue().getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(captor.getValue().getSiblings()).isEqualTo(4);
    }

    @Test
    public void saveShouldReturnErrorIfLastNameIsEmpty() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":null,\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Last name should not be empty")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("NotNull", "NotNull.lastName", "NotNull.userInput.lastName", "NotNull.java.lang.String")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfLastNameIsTooLong() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"should be less than sixty characters otherwise we get an error\",\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Last name should be between 1 and 60 characters")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("Size", "Size.lastName", "Size.userInput.lastName", "Size.java.lang.String")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfMiddleNameIsTooLong() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"should be less than sixty characters otherwise we get an error\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Middle name should be at most 60 characters")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("Size", "Size.middleName", "Size.userInput.middleName", "Size.java.lang.String")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfFirstNameIsEmpty() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":null,\"dateOfBirth\":\"2000-01-01\",\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("First name should not be empty")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("NotNull", "NotNull.firstName", "NotNull.userInput.firstName", "NotNull.java.lang.String")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfFirstNameIsTooLong() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":\"should be less than sixty characters otherwise we get an error\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("First name should be between 1 and 60 characters")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("Size", "Size.firstName", "Size.userInput.firstName", "Size.java.lang.String")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfDateOfBirthIsEmpty() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":null,\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Date of birth should not be empty")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("NotNull", "NotNull.dateOfBirth", "NotNull.userInput.dateOfBirth", "NotNull.java.time.LocalDate")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfDateOfBirthIsInFuture() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2999-01-01\",\"siblings\":4}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("Date of birth should be in the past")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("Past", "Past.dateOfBirth", "Past.userInput.dateOfBirth", "Past.java.time.LocalDate")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfSiblingsIsNull() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":null}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("The amount of siblings should not be empty")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("NotNull", "NotNull.siblings", "NotNull.userInput.siblings", "NotNull.java.lang.Integer")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void saveShouldReturnErrorIfSiblingsIsNegative() throws Exception {
        mockMvc.perform(post("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"lastName\":\"Doe\",\"middleName\":\"Bar\",\"firstName\":\"Joe\",\"dateOfBirth\":\"2000-01-01\",\"siblings\":-2}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].message", is("The amount of siblings should be positive")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("PositiveOrZero", "PositiveOrZero.siblings", "PositiveOrZero.userInput.siblings", "PositiveOrZero.java.lang.Integer")))
            .andDo(document("{method-name}", userInput(), apiError()));
    }

    @Test
    public void findOneShouldReturnUser() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new User(1L, "Doe", null, "John", LocalDate.of(2010, 1, 1), 0)));
        mockMvc.perform(get("/api/user/{id}", 1L).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.lastName", is("Doe")))
            .andExpect(jsonPath("$.middleName", nullValue()))
            .andExpect(jsonPath("$.firstName", is("John")))
            .andExpect(jsonPath("$.dateOfBirth", is("2010-01-01")))
            .andExpect(jsonPath("$.siblings", is(0)))
            .andDo(document("{method-name}", pathParameters(
                parameterWithName("id").description("The unique identifier of the user")
            ), user()));
    }

    @Test
    public void findOneShouldReturnErrorIfNotFound() throws Exception {
        when(repository.findById(-1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/user/{id}", -1L).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].message", is("User with id '-1' is not found")))
            .andExpect(jsonPath("$.[0].codes", containsInAnyOrder("user.notfound")))
            .andDo(document("{method-name}", pathParameters(
                parameterWithName("id").description("The unique identifier of the user")
            ), apiError()));
    }

    private ResponseFieldsSnippet apiError() {
        return responseFields(
            fieldWithPath("[].codes").description("A list of technical codes describing the error"),
            fieldWithPath("[].message").description("A message describing the error").optional());
    }

    private ResponseFieldsSnippet user() {
        return responseFields(
            fieldWithPath("id").description("The unique identifier of the user"),
            fieldWithPath("lastName").description("The last name of the user"),
            fieldWithPath("middleName").description("The optional middle name of the user").optional(),
            fieldWithPath("firstName").description("The first name of the user"),
            fieldWithPath("dateOfBirth").description("The birthdate of the user in ISO 8601 format"),
            fieldWithPath("siblings").description("The amount of siblings the user has"));
    }

    private RequestFieldsSnippet userInput() {
        ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(UserInput.class);
        return requestFields(
            fieldWithPath("lastName").description("The last name of the user")
                .attributes(key("constraints").value(constraintDescriptions.descriptionsForProperty("lastName"))),
            fieldWithPath("middleName").description("The optional middle name of the user").optional()
                .attributes(key("constraints").value(constraintDescriptions.descriptionsForProperty("middleName"))),
            fieldWithPath("firstName").description("The first name of the user")
                .attributes(key("constraints").value(constraintDescriptions.descriptionsForProperty("firstName"))),
            fieldWithPath("dateOfBirth").description("The birthdate of the user in ISO 8601 format")
                .attributes(key("constraints").value(constraintDescriptions.descriptionsForProperty("dateOfBirth"))),
            fieldWithPath("siblings").description("The amount of siblings the user has")
                .attributes(key("constraints").value(constraintDescriptions.descriptionsForProperty("siblings"))));
    }

    private RequestParametersSnippet pageParameters() {
        return requestParameters(
            parameterWithName("page").description("The page to retrieve").optional(),
            parameterWithName("size").description("The number of elements within a single page").optional()
        );
    }

    private ResponseFieldsSnippet userCollection() {
        return responseFields(
            fieldWithPath("[].id").description("The unique identifier of the user"),
            fieldWithPath("[].lastName").description("The last name of the user"),
            fieldWithPath("[].middleName").description("The optional middle name of the user").optional(),
            fieldWithPath("[].firstName").description("The first name of the user"),
            fieldWithPath("[].dateOfBirth").description("The birthdate of the user in ISO 8601 format"),
            fieldWithPath("[].siblings").description("The amount of siblings the user has"));
    }

    private ResponseHeadersSnippet pageHeaders() {
        return responseHeaders(headerWithName("X-Users-Total").description("The total amount of users"));
    }
}
```

**markdown**

spring rests docs 也可以使用markdown，markdown的模版和asciidoc文档模版在同一个jar包不同目录下。

```
this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
.apply(documentationConfiguration(this.restDocumentation)
.snippets().withTemplateFormat(TemplateFormats.markdown()))
.build();
```


## 3. 总结

对于 REST API 的开发者来说，不管 API 的用户是内部团队，还是第三方，高质量的文档都是不可或缺的。长久以来，API 文档的正确性一直困扰着开发人员。Spring REST Docs 采用的手动编写内容与从单元测试自动生成代码片段相结合的方式，为解决 API 文档与代码的同步问题提供了一个很好的思路。

>[参考一：restdocs-reference](https://docs.spring.io/spring-restdocs/docs/2.0.3.RELEASE/reference/html5/#getting-started-requirements)

>[参考二：asciidoctor](https://asciidoctor.org/docs/user-manual/)


[Hello World 项目地址](https://github.com/czarea/spring-rest-docs.git)
