# 요리 레시피 공유 게시판 프로젝트 (CookingRecipe)
&nbsp;
> 전반적인 웹의 기능을 담은 게시판 프로젝트입니다.
> 
![메인페이지](https://github.com/user-attachments/assets/905011ec-e580-44b6-9887-a87a22d5923d)  

&nbsp;
# 목차
1.  [프로젝트 개요](#1-프로젝트-개요)
2.  [주요 기능](#2-주요-기능)
3.  [사용 기술 스택](#3-사용-기술-스택)
    - [백엔드](#백엔드)
    - [프론트엔드](#프론트엔드)
    - [데이터베이스](#데이터베이스)
4.  [실행 화면](#4-실행-화면)
5.  [개발 내용 정리](#5-개발-내용-정리) 
6.  [구조 및 설계](#6-구조-및-설계)
    - [DB 설계](#db-설계)
    - [API 설계](#api-설계)
7.  [마무리](#7-마무리)
    - [보완](#보완)
    - [후기](#후기)
&nbsp;

## 1. 프로젝트 개요
요리 레시피를 공유, 저장하고 사용자 간 자유롭게 의견을 나눌 수 있는 웹 플랫폼입니다.
독학으로 관련 기술들을 학습한 이후 제가 실제로 필요하다고 생각했던 주제로 제작하게 된 개인 프로젝트입니다.
&nbsp;

요리할 레시피를 검색하고 나온 많은 사이트 중에서 각종 광고와 물품을 판매하기 위한 글을 걸러내기 위해 여러 웹 페이지를 왔다갔다 해야했던 복잡하고 불편했던 기억이 있습니다.
그 기억을 바탕으로 대중적으로 유명한 레시피를 조회하거나 또는 자신만의 특별한 레시피를 공유하는 등의 요리 레시피 저장소 사이트를 개발하게 되었습니다.

&nbsp;
&nbsp;
&nbsp;


## 2. 주요 기능
* 게시판 - CRUD 기능, 조회수, 북마크, 좋아요, 카테고리 및 좋아요 수 바탕 페이징 및 검색 처리
* 사용자 - Security 회원가입 및 로그인, OAuth 2.0 카카오 로그인, 마이페이지, 회원정보 수정, 비밀번호 변경 및 재발급, 작성글 및 북마크한 글 조회, 회원가입시 유효성 검사 및 중복 검사
* 댓글 - CRUD 기능

&nbsp;
&nbsp;
&nbsp;


## 3. 사용 기술 스택
- **백엔드**
  - JAVA 17
  - Spring Boot
  - Spring Security
  - JPA & QueryDSL
- **프론트엔드**
  - Thymleaf, Bootstrap
- **데이터베이스**
  - MySQL
- **테스트툴**
  - Postman
 
&nbsp;
&nbsp;
&nbsp;


## 4. 실행 화면
<details>
  <summary>메인 페이지</summary>

### 로그인 여부에 따라 헤더에 다른 목록 표시

&nbsp;
![비로그인 메인페이지](https://github.com/user-attachments/assets/905011ec-e580-44b6-9887-a87a22d5923d)  

**[비로그인 메인페이지]** : 회원가입 / 로그인

&nbsp;
![로그인 메인페이지](https://github.com/user-attachments/assets/ddce3359-0612-4d78-a469-3f7dfd7e383f)  

**[로그인 메인페이지]** : 해당 사용자 닉네임 / 마이페이지 / 북마크 / 로그아웃

</details>

<details>
  <summary>게시글 관련</summary>

  &nbsp;
### 1. 게시글 전체 조회
![게시글 전체 조회](https://github.com/user-attachments/assets/a8e90211-668d-47b5-b8d6-73d5927b1649)  

  **게시글 전체를 페이징 처리하여 조회**

&nbsp;
### 2. 게시글 작성
![게시글 작성1](https://github.com/user-attachments/assets/b38100ed-ce27-4671-adcf-3947a3a8a444)  
![게시글 작성2](https://github.com/user-attachments/assets/a7c5dd7a-f350-4e34-bfbb-a457cb05da5e)  

  **비로그인 상태에서 [글쓰기] 버튼 클릭 시 로그인으로 리다이렉트**  
  **[단계 추가] 버튼 클릭 시 단계 추가 가능**
  **게시글 작성 완료 시 해당 게시글로 리다이렉트**

&nbsp;
### 3. 게시글 상세 조회
![게시글 상세 조회1](https://github.com/user-attachments/assets/53aa1f24-958d-4372-b5ca-d908ce73bfed)  
![게시글 상세 조회2](https://github.com/user-attachments/assets/c56be623-84ea-4754-a46e-4e48871e6e15)  
![게시글 상세 조회3](https://github.com/user-attachments/assets/2c8e9f2d-f706-4e30-b975-55c1ab3d6a4d)  
![게시글 상세 조회4](https://github.com/user-attachments/assets/587e61e1-1381-4a02-a605-9f7486096908)  

  **본인이 작성한 게시글만 수정 및 삭제 가능**

&nbsp;
### 4. 게시글 좋아요
![게시글 좋아요1](https://github.com/user-attachments/assets/cf7fa414-ffe9-497a-99da-db016d174746)  
![게시글 좋아요2](https://github.com/user-attachments/assets/97cbbd0f-1e78-49d0-8c5e-5d87514db9a0)  

  **이모티콘으로 게시글 좋아요 토글 가능**

&nbsp;
### 5. 게시글 북마크
![게시글 북마크](https://github.com/user-attachments/assets/0254ad68-50b2-4718-8b02-a9ed2e1c9703)  

  **이모티콘으로 북마크 좋아요 토글 가능**

&nbsp;
### 6. 게시글 수정
![게시글 수정1](https://github.com/user-attachments/assets/bbd00508-8aa7-4adf-8a77-b9eb91fdf88c)  
![게시글 수정2](https://github.com/user-attachments/assets/e60462d0-1c73-4f2e-bef4-44a160c0be03)  

  **게시글 수정 폼에서 [수정하기] 버튼을 누르면 수정 완료 후 해당 게시글로 리다이렉트**

&nbsp;
### 7. 게시글 삭제
![게시글 삭제](https://github.com/user-attachments/assets/37d09bd5-2285-4851-8c87-3360642f1d0b)  

  **Confirm으로 삭제 여부를 확인하고 삭제 후 전체 게시글 목록으로 리다이렉트**

&nbsp;
### 8. 게시글 검색
  - 검색 기준에 따른 게시글 검색
![검색 기준](https://github.com/user-attachments/assets/aa5ba923-70ed-43cd-94cb-e7b37eb824e1)  

&nbsp;
![게시글 검색1](https://github.com/user-attachments/assets/89d1313f-4226-47a7-84c3-58476044bc94)  
  **게시글 제목+내용 검색**
![게시글 검색2](https://github.com/user-attachments/assets/6f58a220-0f11-4ad8-b529-33f4bfb03e53)  
  **게시글 재료 검색**
![게시글 검색3](https://github.com/user-attachments/assets/202ad1fd-9d5a-47e0-bf87-2d858686a7ec)  
  **게시글 작성자 검색**

&nbsp;
  - 좋아요 갯수에 따른 게시글 검색  
![TOP10](https://github.com/user-attachments/assets/471fe4db-5ddf-464b-938f-b064c3889e39)  
  **전체 게시글 중 좋아요 개수가 상위 10개인 게시글 검색**

![Monthly](https://github.com/user-attachments/assets/cc5ba7fe-0f14-4bb9-9208-482d19d63b6e)  
  **해당 달에 작성된 게시글 중 좋아요 개수가 상위 10개인 게시글 검색**

&nbsp;
  - 카테고리/요리방법에 따른 게시글 검색
![Category](https://github.com/user-attachments/assets/13fce5e9-c4f2-4950-aee5-06ec18a17840)  
  **설정한 카테고리에 해당하는 게시글 검색**

![Method](https://github.com/user-attachments/assets/9310c822-02df-4f90-9fad-46fffbf6d5ac)  
  **설정한 요리방법에 해당하는 게시글 검색**

</details>

<details>
  <summary>사용자 관련</summary>

&nbsp;
### 1. 회원가입  
![회원가입0](https://github.com/user-attachments/assets/b2e5eaca-eb1e-490a-8c96-3316fe0ca691)  
![회원가입1](https://github.com/user-attachments/assets/dfe0c51b-ac69-4b7a-825d-d35fa1659990)  
![회원가입2](https://github.com/user-attachments/assets/57f1ee50-ab47-439b-aecd-f66fa8d92e54)  
  **회원가입 시 유효성 검사 및 중복 확인**
&nbsp;

![회원가입3](https://github.com/user-attachments/assets/b863a8a4-b35d-437e-8a0d-90cbdcd71056)  
![회원가입4](https://github.com/user-attachments/assets/e52dca3a-8c5f-49c6-9ba9-133791bf350b)  
  **회원가입 성공 시 자동 로그인 후 메인페이지로 리다이렉트**

&nbsp;
### 2. OAuth 2.0 소셜 회원가입  
![소셜 회원가입1](https://github.com/user-attachments/assets/6bb92380-606c-426b-87ae-44d42ab1f575)  
  **회원가입 화면에서 [카카오로 회원가입] 클릭 시 카카오 로그인 페이지로 이동**

&nbsp;
![소셜 회원가입2](https://github.com/user-attachments/assets/afcd7158-a079-4a05-80ca-a242731778c5)  
  **카카오 계정 로그인 완료 시, 추가 정보 입력 페이지로 이동**  
  **카카오로 회원가입 시 추가 정보 입력하기 전까지 카카오 실제 계정 이름으로 헤더에 표시**

&nbsp;
![소셜 회원가입3](https://github.com/user-attachments/assets/460e07e4-b192-4002-bd74-276f8b6a9f9f)  
  **추가 정보 입력을 완료하면 메인 페이지로 리다이렉트**  
  **추가 정보 입력시 입력한 닉네임으로 헤더에 표시**

&nbsp;
### 3. 로그인  
![로그인1](https://github.com/user-attachments/assets/fccd2379-a679-43f9-9c86-dc3f1e371bee)  
  **로그인 실패 시 에러 메세지 출력**  
  **카카오로 로그인 시 정보가 세션에 남아있으면 자동으로 로그인**
  
&nbsp;
### 4. 아이디 찾기  
![아이디 찾기](https://github.com/user-attachments/assets/1d322ea1-ce66-4d57-ac8c-3c7a4aba1144)  
  **아이디 찾기 성공 시 입력한 정보에 해당하는 로그인 아이디 출력**  
![아이디 찾기2](https://github.com/user-attachments/assets/c531901e-61e2-4b2e-ae8c-d20614a7ac01)  
  **잘못된 정보 입력 시 에러 메세지 출력**

&nbsp;
### 5. 비밀번호 찾기  
![비밀번호 찾기1](https://github.com/user-attachments/assets/7c2d2fb6-af8a-4efa-aca6-50b02c91dece)  
  **비밀번호 찾기 성공 시 uuid로 생성된 임시 비밀번호 반환하고 로그인 시 비밀번호 변경 권장**  
![비밀번호 찾기2](https://github.com/user-attachments/assets/3aeefc4f-f209-4c3b-929c-fd65c95d5d2f)  
  **잘못된 정보 입력 시 에러 메세지 출력**
  
&nbsp;
### 6. 마이페이지  
  - 마이페이지
![마이페이지](https://github.com/user-attachments/assets/63a553e1-2c49-453f-bddb-e2b047e8e87e)  

&nbsp;
  - 회원정보 조회
![회원정보 조회](https://github.com/user-attachments/assets/0b9c2951-1d5d-4df6-ac01-c7353f402c75)

&nbsp;
  - 회원정보 수정
![회원정보 수정1](https://github.com/user-attachments/assets/ae79d0b2-f1ea-40f4-80c4-7bec82feca6b)  
![회원정보 수정2](https://github.com/user-attachments/assets/e257373a-7d8b-458a-8444-ee7d065df0e1)  
  **아이디, 생년월일은 수정 불가**  
  **회원정보 수정 완료 시 회원정보 조회 화면으로 리다이렉트**

&nbsp;
  - 작성한 글/북마크한 글 조회
![작성글](https://github.com/user-attachments/assets/d2c6dde9-3a2c-4819-bee2-204d35a8f3d5)  
![북마크글](https://github.com/user-attachments/assets/9868981c-dd6d-4ae2-a000-8eecc3586ea4)  

  &nbsp;
  - 비밀번호 재설정
![비밀번호 재설정1](https://github.com/user-attachments/assets/7266d09b-1af4-4abb-8c11-1c2570516d25)  
![비밀번호 재설정2](https://github.com/user-attachments/assets/1d140ded-d5e7-4cd7-bb09-8d2826b698a6)  
  **비밀번호 재설정 완료 시 회원정보 조회 화면으로 리다이렉트**

&nbsp;
  - 회원 탈퇴
![회원 탈퇴1](https://github.com/user-attachments/assets/ee408b2f-6a32-4f92-8ae0-7cf39ad0bb66)  
![회원 탈퇴2](https://github.com/user-attachments/assets/3c244767-fb06-4589-81e1-21ec8c421849)  
  **잘못된 비밀번호 입력 시 에러 메세지 출력**  
  **회원 탈퇴 시 사용자의 정보뿐만 아니라 해당 사용자가 작성한 글도 CASCADE로 삭제**

</details>

<details>
  <summary>댓글 관련</summary>

![댓글1](https://github.com/user-attachments/assets/0f595dee-7ac0-4a2a-96e6-615a4257a0fe)  
![댓글2](https://github.com/user-attachments/assets/17b191fc-e899-4826-bdd6-896eb546dd41)  
![댓글3](https://github.com/user-attachments/assets/a16bb14f-a994-4fb8-a610-b563124f3e7b)  
![댓글4](https://github.com/user-attachments/assets/6c647462-d415-467a-9a46-84da46924673)  

  **기본적인 CRUD**
  
</details>

<details>
  <summary>실시간 쪽지(API)</summary>
    
&nbsp;
### 1. 쪽지 전송
![쪽지1](https://github.com/user-attachments/assets/abc0d517-55a8-44b4-bcfb-96dc3bbfa2fc)
![쪽지2](https://github.com/user-attachments/assets/5b7d81f3-2e74-4bc1-97b7-f965a99981d6)

### 2. 보낸 쪽지 전체 조회
![보낸 메세지 조회](https://github.com/user-attachments/assets/0668cf3a-763e-4c0c-b646-b0f5e2cf9b55)

### 3. 안읽은 쪽지 개수 조회
![안읽은 쪽지 개수](https://github.com/user-attachments/assets/fdbc6f0a-5f6c-4e93-b3da-4c2574eaa058)

### 4. 받은 쪽지 전체 조회
![받은 쪽지 전체 개수 조회](https://github.com/user-attachments/assets/9e49bf62-fa3e-4b71-b78b-bc8543a71246)

### 5. 쪽지 읽음으로 상태 변경
![메세지 읽음 변경](https://github.com/user-attachments/assets/20b2c6ab-8f6d-4514-9ec8-e1f53b100e34)
![메세지 읽음 후 전체 메세지 조회](https://github.com/user-attachments/assets/fe57f32d-5606-4793-b846-2e8c9ed1e14b)  
**ID:1의 쪽지의 읽음 상태가 true로 변경됨**

### 6. 받은 쪽지 단일 조회
![단일 메세지 조회](https://github.com/user-attachments/assets/b635c8e3-d417-4e1a-84f7-32864b57d709)

  
</details>

## 5. 개발 내용 정리  
[[Spring Security] 로그인 설정](https://velog.io/@kswdot/Spring-Security-%EB%A1%9C%EA%B7%B8%EC%9D%B8)  
[[OAuth 2.0] 카카오 로그인](https://velog.io/@kswdot/OAuth-2.0-%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8)  
[[@Validation] 유효성 검증 및 중복 검사](https://velog.io/@kswdot/Validation-%EC%9C%A0%ED%9A%A8%EC%84%B1-%EA%B2%80%EC%A6%9D-%EB%B0%8F-%EC%A4%91%EB%B3%B5-%EA%B2%80%EC%82%AC)  
[[JPA] Lazy 로딩, 영속성 컨텍스트, 프록시 객체와 LazyInitializationException 해결](https://velog.io/@kswdot/JPA-Lazy-%EB%A1%9C%EB%94%A9-%EB%AC%B8%EC%A0%9C%EC%99%80-LazyInitializationException-%ED%95%B4%EA%B2%B0)  
[[QueryDSL] 게시글 검색 기능 구현](https://velog.io/@kswdot/QueryDSL-%EA%B2%8C%EC%8B%9C%EA%B8%80-%EA%B2%80%EC%83%89-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84)  
[[QueryDSL] 게시글 페이징 처리 구현](https://velog.io/@kswdot/QueryDSL-%EA%B2%8C%EC%8B%9C%EA%B8%80-%ED%8E%98%EC%9D%B4%EC%A7%95-%EC%B2%98%EB%A6%AC-%EA%B5%AC%ED%98%84)  
[[Spring Boot] 파일(이미지) 업로드](https://velog.io/@kswdot/Spring-Boot-%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%85%EB%A1%9C%EB%93%9C-%EA%B5%AC)  
[[Spring Boot] 파일(이미지) 조회](https://velog.io/@kswdot/Spring-Boot-%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%A1%B0%ED%9A%8C)  
[[Spring Boot] 파일(이미지) 수정](https://velog.io/@kswdot/Spring-Boot-%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%88%98%EC%A0%95)  
[[Spring Boot] 좋아요/북마크 기능과 관련 서비스](https://velog.io/@kswdot/Spring-Boot-%EC%A2%8B%EC%95%84%EC%9A%94%EC%99%80-%EB%B6%81%EB%A7%88%ED%81%AC-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84%EA%B3%BC-%EA%B4%80%EB%A0%A8-%EC%84%9C%EB%B9%84%EC%8A%A4)  
[[Session] 조회수 중복 증가 방지 구현](https://velog.io/@kswdot/Session-%EC%A1%B0%ED%9A%8C%EC%88%98-%EC%A4%91%EB%B3%B5-%EC%A6%9D%EA%B0%80-%EB%B0%A9%EC%A7%80)  
[[JPA] 양방향 연관 관계와 무한 순환 참조 문제 해결](https://velog.io/@kswdot/JPA-%EC%96%91%EB%B0%A9%ED%96%A5-%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84%EC%99%80-%EB%AC%B4%ED%95%9C-%EC%88%9C%ED%99%98-%EC%B0%B8%EC%A1%B0-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)  
[[Redis] 조회수 중복 방지 방지 구현2](https://velog.io/@kswdot/Redis-%EC%A1%B0%ED%9A%8C%EC%88%98-%EC%A4%91%EB%B3%B5-%EC%A6%9D%EA%B0%80-%EB%B0%A9%EC%A7%80-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84)  
[[Spring Security] 경로별 접근 제어](https://velog.io/@kswdot/Security-Spring-Security-%EA%B2%BD%EB%A1%9C%EB%B3%84-%EC%A0%91%EA%B7%BC-%EC%A0%9C%EC%96%B4)  
[[Clean Code] Service 계층 클린 코드](https://velog.io/@kswdot/Clean-Code-Service-%EA%B3%84%EC%B8%B5-%ED%81%B4%EB%A6%B0-%EC%BD%94%EB%93%9C)  
![WebSocket] 실시간 쪽지 기능 구현](https://velog.io/@kswdot/WebSocket-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%AA%BD%EC%A7%80-%EA%B8%B0%EB%8A%A5)  

&nbsp;

## 6. 구조 및 설계
<details>
  <summary>DB 설계</summary>
<br>
  
### DB 설계
  
![DB 설계](https://github.com/user-attachments/assets/2975effb-f278-4645-bff2-60c7985dcbe2)  
&nbsp;
  
![Board DB](https://github.com/user-attachments/assets/9a3a300b-2701-4a3c-9b04-068f7151859f)  
![User DB](https://github.com/user-attachments/assets/ab4ae3e0-1226-49d1-ba5a-bd5d5f5ebee1)  
![Recipe DB](https://github.com/user-attachments/assets/35b94083-4735-46f3-b463-8a8951915710)  
![Comment DB](https://github.com/user-attachments/assets/e00763cc-05fe-482e-b843-9ae1250aa789)  
![Like DB](https://github.com/user-attachments/assets/cea02a89-b632-495e-9707-794bd05e48be)  
![Bookmark DB](https://github.com/user-attachments/assets/24b0ae48-1222-485d-ab37-fc5c68673781)  


</details>

<details>
  <summary>API 설계</summary>
  &nbsp;

  ### API 설계

![Board API](https://github.com/user-attachments/assets/d23f444a-5a04-4ab4-b7af-c06c7791f7fa)  
![User API](https://github.com/user-attachments/assets/b80b13af-b8b6-4ae3-a1df-8de481fd8416)  
![Comment API](https://github.com/user-attachments/assets/d54cc019-d369-4fd8-bc9f-92086c7ef742)  
![Login API](https://github.com/user-attachments/assets/ce85b8e0-2fa8-41df-aecc-c98eb0a6aa94)  
![Notification API](https://github.com/user-attachments/assets/2e0fffb8-f6fa-406e-b9ec-f200ba802ecc)  
![Admin API](https://github.com/user-attachments/assets/6233d034-48fe-43c5-b3a1-0fe152969ddb)  
  

</details>

&nbsp;

## 7. 마무리
### - 보완  
  1) 실시간 알림 기능 구현 - 현재 실시간 알림이 아닌 비실시간 알림만 구현되어있으므로 추후 구현 예정
  2) 채팅 기능 구현 - 사용자간 간단한 채팅이나 쪽지를 주고 받을 수 있는 기능
  3) 통계 기능 - 조회수, 좋아요 수를 바탕으로 관리자가 게시글의 통계를 낼 수 있는 기능
 
 ### - 후기  
  학교 재학 중에는 코드를 고치면 실시간으로 내가 변경한 코드가 반영된 것이 쉽게 보여지는 프론트엔드를 선호했고  
  졸업 프로젝트도 높은 수준은 아니었지만 프론트엔드를 담당해서 진행했습니다. 하지만 졸업 후 토이 프로젝트에서    
  간단한 백엔드를 처음 접하게 되었고 사용자 인터페이스에 직접적으로는 보이지는 않지만 제가 설계한 방향으로 시스템이  
  작동하는 것을 보며 데이터베이스 설계나 API 개발 같은 작업을 통해 일상생활에서 접하는 복잡한 시스템이  
  유기적으로 동작하도록 만드는 것이 평소 무엇이든 구조화하는 것에 흥미를 느끼던 저에게 큰 매력으로 다가왔습니다.
  &nbsp;
  
  그렇게 해서 만들게 된 것이 이번 프로젝트이고 혼자 독학한 내용을 바탕으로 한 프로젝트이다보니 제가 배웠던 내용들을  
  적용해보고 동작하는 것을 확인할 수 있다는 설렘만큼이나 부족한 부분에 대한 아쉬움도 많이 남습니다.
  실제로 프로젝트가 원하는 것처럼 순조롭게 진행되지는 않았습니다. 분명 공부하면서 확실히 습득했다고 생각했던 로직들도 에러가 나는 경우가 허다했고 발생한 오류 중 일부분은 해결 방법을 찾기 위해 꼬박 하루를 투자하기도 했었습니다.
    보다 효율적인 설계를 위해  
  고민하고 블로그, 강의, 유튜브에서 예시를 찾아보며 실제로 많이 공부할 수 있었던 부분도 많았습니다. 공부할 때나 토이  
  프로젝트보다 다뤄야할 데이터와 구현해야 할 서비스가 많았기 때문에 계층 간 주고 받는 데이터의 DTO 생성부터  
  Config 설정, 로직 단계 처리까지 혼자서 많은 자책과 고민을 할 수 밖에 없었던 시간이었습니다.
  &nbsp;

  하지만 이번 프로젝트는 저의 부족한 부분을 스스로 알 수 있는 좋은 계기가 되었고 더 깊게 공부하며 스스로 발전할 수 있는 방향을 찾을 수 있게 되었습니다.
  또한 직접 개발 단계까지 가야 경험할 수 있는 Security 설정과 OAuth 2.0, Redis 등 새로운  
  기술을 다뤄볼 수 있었던 좋은 기회가 되었던 것 같습니다. 아직 많이 부족한 새내기 개발자이지만 제가 만들어가는 코드에  
  애정을 가질 수 있었고 여기서 더 나아가 자부심을 느낄 수 있는 그런 개발을 하고 싶다는 생각을 가지는 계기도 된 것 같습니다.
  &nbsp;

  이 프로젝트를 시작한지 2달 정도가 지났습니다. 정말 진심으로 에러가 발생하면 발생한 오류를 한번에 해결하지 못하는 저를 자책하며 막막한 시간을 보냈고 이것저것 변경해보며 오류가 해결되면
  웃으며 행복해했습니다. 제가 처음으로 이렇게 애정을 담아 진행한 프로젝트인만큼 이후에도 제가 틈틈히 추가 기능을 구현하고 더 나은 로직으로 변경해가며 서비스를 배포 완료 할 예정입니다.  
  &nbsp;
  
  긴 글 읽어주셔서 감사합니다.

  
&nbsp;

