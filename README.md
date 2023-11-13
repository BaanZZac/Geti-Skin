# Geti-Skin

# Spageti 피부진단 프로그램

## 🖼️ 화면 구성

### Login
    - Login with Google 버튼 디자인
  

###  Main
    - 피부 진단
    - Daily Skin(나의 피부 일지)
    - 샾 추천
    - Settings

### 피부 진단
    - 카메라로 진단
        - 버튼 클릭 시, 가이드 페이지로 넘어감
        - 가이드 페이지
            - 학습 사진과 비슷하게 찍어주세요(라는 식으로 이미지를 띄어줌)
            - Text 가이드도 이미지 아래에 추가  
            - 취소 버튼 -> 피부 진단 페이지로 넘어감
            - 진단하기 버튼 -> 카메라를 킴, 촬영후 진단 결과 창으로 넘어감  

    - 앨범으로 진단
        - 버튼 클릭 시, photopicker로 사진 고르기
            - 사진을 불러오면 이 사진이 맞습니까 페이지
            - 취소 버튼 -> 피부 진단 페이지로 넘어감
            - 진단하기 버튼 -> 진단 결과 창으로 넘어감

### 진단 결과
    - 사진갖고 model이 피부를 진단함
    - Loading  

    - 결과창
        - 위쪽
            - 사진
        - 아래쪽
            - 이마 : 건성일시 빨강, 지성일시 청록, 막대그래프(%) 
            - 코 : 건성일시 빨강, 지성일시 청록, 막대그래프(%)
            - 볼 : 건성일시 빨강, 지성일시 청록, 막대그래프(%)
            - 종합 : Text 강조 "OOO형 피부입니다."
  

### 샾 추천
    - GPS 기반으로 피부샾 추천
    - 거리와 가까운 매장을 추천 해줌
    - google 지도 이용(or Naver or Kakao)
  

### Settings
    - Logout
    


## 기능 명세서

## 🙄 APP 🙄

## Firebase

### Authentication
    - google login
    - SHA1 지문등록 필요  
    - 의존성 추가
    

### Cloud Firestore  
    - DB
    - User Daily skin Data save
        - 이마, 코, 볼의 건성, 지성 상태(%)와 최종 결과
    - 의존성 추가

### Storage
    - User별 image(skin) 저장
        - 카메라로 찍은 사진
        - 내부 저장소에서 불러온 사진
  
##

### Login

    - user가 Null이면, Navigation StartDestination
    - 어플의 전체적인 UI/UX 고려
    - 구글 로그인 버튼(아이콘 디자인)
    - 원클릭 로그인으로 이후 로그아웃 전까지 보여지지 않을 화면
    
### Main

    - user가 Null이 아니면, Navigation StartDestination
    - 상단, 하단바 X
    - 위쪽 어플 이름, ICON
    - 중앙 ~ 하단 4개 버튼으로 구성
    - 버튼은 2행 2열로 구성
    - 피부 진단, 나의 피부 일지, 샾 추천, Settings로 구성

### 피부 진단

    - 카메라로 진단, 앨범에서 진단으로 구성
    - 카메라 진단
        - 가이드 페이지 불러오기
        - 가이드 확인 이후 기본 Camera 불러와서 실행
        - 찍은 이미지 Model input에 맞게 Resize
    - 앨범에서 진단
        - PhotoPicker로 불러온 이미지 불러오기
        - Model input에 맞게 Resize

### 진단 결과
    
    - 찍은 사진 OR 앨범에서 가져온 사진이 가장 위 쪽에 위치(적절한 Padding)
    - 사진 아래 AI 분석 결과 막대그래프로 출력
        
        진단결과
        ----------
        사진
        ----------
        이마(건성, 지성 %)
        막대그래프(건성==빨강, 지성==청록)
        코(건성, 지성 %)
        막대그래프(건성==빨강, 지성==청록)
        볼(건성, 지성 %)
        막대그래프(건성==빨강, 지성==청록)
        ----------
        최종 진단 : "건성/지성/복합성 피부입니다!!"
        ----------
        취소 | 결과저장
    
    - 취소버튼 -> Main
    - 결과저장 -> 사진, 이마, 코, 볼 분석 결과와 최종진단 결과(label만) Firebase Store에 UserID와 함께 저장 -> Main

### 샾 추천

    - 버튼 클릭시 외부 지도 어플연결
    - 위치기반 검색으로 피부샾 검색된 데이터로 실행

### Settings

    - Google Logout 버튼
    - Logout시 Login 화면으로 화면전환

##
## 😱 AI 😱

### 데이터 수집

    - Python Code로 WebCrawling 또는 검색으로 Dataset 획득
    - 이마, 코, 볼 피부에 대한 Data 검색
    - 건성, 지성으로 Labeling

### 데이터 전처리

    - 수집 데이터 검증
    - 원하는 데이터가 이닐시 제거
    - 동일한 크기로 Resize
    - 필요시 EfficientNet-V2로 Contrast 개선

### 모델링

    - Geti
    - CNN

### 평가

    - 혼돈행렬 평가지표 이용
    - 정확 60% 이상의 모델 사용

##

# 개발 요구사항

## 🙄 APP 🙄

```
Android Studion Version : Giraffe
화면구성 : Navigation
이미지처리 : Coil
이미지업로드 : PhotoPicker(더 좋은 기능 있을시 교체)
Firebase Dependency : 공식문서의 버전
Server : Retrofit2, okhttp3로 Flask와 통신
```

## 😱 AI 😱

```
정확도 : 60% 이상의 분류
User입장에서 빠르게 진단을 받을 수 있도록 AI 설계
```
