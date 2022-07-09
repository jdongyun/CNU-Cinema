영화관 서비스(CNU Cinema)
====
## 프로젝트 소개
- 기본적인 영화관 예매 서비스를 구현한 프로젝트입니다.
- 3학년 1학기 데이터베이스 과목의 텀프로젝트 과제입니다.

## 기술 스택
- Spring Boot
- MariaDB, H2
- JDBC(JPA 등을 사용하지 않은 Raw SQL 쿼리를 작성을 위해)

## 상세 구현
- Spring Security와 연동하여 회원가입 및 로그인이 가능합니다.
- 사용자는 상영 중이거나 상영 예정인 영화를 검색할 수 있으며 예매가 가능합니다.
- 예매 완료 시 미리 등록된 E-mail 주소로 예매 정보 메일이 전송됩니다.
- 예매한 내역에 대해 상영 시작 전까지 예매 취소가 가능합니다.


# 스크린샷
## 로그인
<img width="581" alt="login" src="https://user-images.githubusercontent.com/3584734/178102826-2227f197-131a-4282-b994-f4b3dc642f05.png">

## 메인 화면
<img width="581" alt="main" src="https://user-images.githubusercontent.com/3584734/178102831-2e24e497-9f5f-4b7e-a640-6869405e3350.png">

## 영화 목록
<img width="612" alt="movie_list" src="https://user-images.githubusercontent.com/3584734/178102839-d0191a8f-90e7-4559-94f4-3e0a1af7def8.png">

## 영화 정보
<img width="552" alt="movie_detail" src="https://user-images.githubusercontent.com/3584734/178102843-74555393-8fdb-4373-8d09-17accd27135b.png">

## 영화 예매
<img width="661" alt="movie_reservation" src="https://user-images.githubusercontent.com/3584734/178102848-d386d633-e79d-4eff-8d29-4f0feefd673d.png">

## 영화 예매 완료
<img width="661" alt="movie_reservation_complete" src="https://user-images.githubusercontent.com/3584734/178102850-1aab6c63-d23a-423d-907a-184b23d9825e.png">

## 사용자 관람 정보
<img width="709" alt="user_info" src="https://user-images.githubusercontent.com/3584734/178102851-3a39eaaa-e449-45fd-ae29-ad3705b85672.png">

## 관리자 통계 정보
<img width="706" alt="admin_stats" src="https://user-images.githubusercontent.com/3584734/178102853-9951f693-09a0-40f6-b769-ed397294734f.png">
