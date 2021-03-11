# 안드로이드 앱 만들기
#### 안드로이드 스튜디오 툴 익히며, AVD 모듈로 확인해보기 실습

### PREVIEW
<img align="left" width="40%;" src="https://user-images.githubusercontent.com/63999784/110773931-9d473580-82a0-11eb-9778-2fb732bbba15.PNG"> 
<img width="40%;" src="https://user-images.githubusercontent.com/63999784/110773967-a3d5ad00-82a0-11eb-9ed1-9a4b5e7cfdb8.PNG">

- 안드로이드앱의 라이프사이클
- <b>LoginActivity 기준</b> 6개의 상태
- <b>onCreate</b>(생성), <b>onStart</b>(시작), <b>onResume</b>(실행), <b>onPause</b>(일시정지), <b>onStop</b>(정지), <b>onDestroy</b>(소멸)
- 로그인했을 때(현재 화면(로그인화면)을 벗어날때): onPause(일시정지) -> onStop(정지)
- 로그인 후 뒤로가기 했을 때: onStart(시작) -> onResume(실행)
- 로그아웃했을때: onCreate(생성) -> onStart(시작) -> onResume(실행)
- 
- 로그인 후, 아이디 값과 암호 값 가져가기
- <b>LoginActivity.java</b>

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    btnLogin = findViewById(R.id.btnLogin);
    btnLogin.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        EditText editTextID, editTextPassword;
        editTextID = findViewById(R.id.editTextID);
        editTextPassword=findViewById(R.id.editTextPassword);
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.putExtra("editTextID", editTextID.getText().toString()); //아이디
        mainIntent.putExtra("editTextPassword", editTextPassword.getText().toString()); //암호
        startActivity(mainIntent);
        Toast.makeText(LoginActivity.this, "onCreate상태1", Toast.LENGTH_LONG).show();
    }
});
```
- <b>MainActivity.java</b>

```java
 //MainActivity가 onCreate로 생성이 될 때, Intent로 받은 값을 출력 가능
//기술 참조
Intent intent = new Intent(this.getIntent());
String userId = intent.getStringExtra("editTextID");
String userPw = intent.getStringExtra("editTextPassword");
textViewWelcome = findViewById(R.id.textViewWelcome);
textViewWelcome.setText(userId+"님 환영합니다.");
```
