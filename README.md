DataTransferSW

- 설정 파일(Json)을 읽어 특정 폴더의 파일 생성을 감지하고 명시된 외부 서버에 파일을 전송하는 SW
- 설정 파일은 UI를 통해 수정이 가능하고 수정 즉시 프로그램에 적용

개발환경

- JAVA (JDK 1.8 이상)
- GUI : JavaFX

클래스 구조

- data
  - Config : Config 파일의 Data 클래스
  - Receive : Config 하위의 Data 클래스
  - Transfer : Config 하위의 Data 클래스
  - Target : Receive, Transfer 클래스 하위의 Target 클래스
  - Setting : 프로그램 세부 설정 데이터 클래스 ex ) 파일 전송 실패 제한 수
  - WatchedList : 감시 대상 폴더들의 List가 담겨 있는 Data 클래스
- manager 
  - JSONManager : Config Json 파일 Parsing, Binding 기능을 담당하는 클래스
  - FolderObserver : 해당 Folder의 파일생성을 감지하는 클래스
  - FileSender : SMB, FTPS 프로토콜로 외부 서버에 파일 전송을 담당하는 클래스
  - SMBClientManager : SMB 전송클래스
  - FTPSClientManager : FTPS 전송클래스
- ui
  - UiUtil : Ui 에 공통적으로 사용되는 기능을 제공하는 클래스
  - MainController : Main page의 Controller 클래스
  - Main : Main page 뷰 loader 클래스
  - config 
    - AddTargetController : TreeView Target Add 시 사용되는 Controller 클래스
    - ConfigController : Config 파일 Viewer의 Controller 클래스
      - ModifyController : TreeView의 content modify 시 사용되는 Controller 클래스
    - ConfigContentViewModel : Config Tree의 Content ViewModel 클래스
  - setting
    - SettingController: Setting View의 Controller 클래스
- util
  - DataType : DataType 들이 명시된 Enum 클래스



사용 라이브러리

- Gson : jsonParsing, Binding 기능 구현시 사용
- Commons-net : FTPS Client 관련 기능 구현시 사용
- JCIFS : SMB Client관련 기능 구현시 사용
- Jfoenix : UI 구현 시 Design 부분 상요
- logback : 수행 로그 작성 시 사용
