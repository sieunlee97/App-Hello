package core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 이 클래스는 AsyncTask 클래스를 상속받아서 비동기 통신 기능 구현
 */
public class PostResponseAsyncTask extends AsyncTask<String, Void, String> {
    //멤버변수 = 필드변수
    private Context context;
    private HashMap<String, String> postDataParams = new HashMap<String, String>();
    private AsyncResponse asyncResponse;
    private ProgressDialog progressDialog; // 스트링으로 요청을 보낸 후 응답 받을 때까지 '처리중입니다' 대화상자 띄우기
    private String loadingMessage = "처리중입니다";
    private boolean showLoadingMessage = true; //응답을 받을 때 위 로딩 메세지를 화면에서 지우기 위해서
    
    public PostResponseAsyncTask(Context context, HashMap postDataParams, AsyncResponse asyncResponse) {
        this.context = context;
        this.postDataParams = postDataParams;
        this.asyncResponse = asyncResponse; // 비동기 통신을 종료할 때 결과를 출력
    }

    public PostResponseAsyncTask(Context context, AsyncResponse asyncResponse) {
        this.context = context;
        this.asyncResponse = asyncResponse;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public HashMap<String, String> getPostDataParams() {
        return postDataParams;
    }

    public void setPostDataParams(HashMap<String, String> postDataParams) {
        this.postDataParams = postDataParams;
    }

    public AsyncResponse getAsyncResponse() {
        return asyncResponse;
    }

    public void setAsyncResponse(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public boolean isShowLoadingMessage() {
        return showLoadingMessage;
    }

    public void setShowLoadingMessage(boolean showLoadingMessage) {
        this.showLoadingMessage = showLoadingMessage;
    }

    @Override
    protected String doInBackground(String... requestUrls) {
        // 비동기 통신으로 요청사항을 스프링 앱에서 전달받는 기능
        String result = "";
        result = invokePost(requestUrls[0], postDataParams);
        return result;
    }

    private String invokePost(String requestUrl, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);//15초동안 커넥션 시도
            conn.setConnectTimeout(15000);//15초당안만 커넥션 시도 후 중지
            conn.setRequestMethod("POST");//스프링앱과 통신 시, POST방식만 사용
            conn.setDoInput(true);//커넥션 후 Input 허용
            conn.setDoOutput(true);//커넥션 후 Output 허용
            //postParams 받아서 처리
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();//버퍼드 갱신 = 내보낸다.
            writer.close();//객체 소멸
            os.close();//객체 소멸
            int responseCode = conn.getResponseCode();// //200,204,400
            if(responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = br.readLine()) != null){
                    response += line; //누적변수
                }
            }else{
                Log.i("PostResponseAsyncTask", responseCode +"");
                response = String.valueOf(responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response; // 전송받은 결과값을 반환
    }

    private String getPostDataString(HashMap<String, String> postDataParams) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : postDataParams.entrySet()){
            if(first){
                first = false;
            }else{
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("-");
            result.append(URLEncoder.encode(entry.getValue(), "UTF_8"));
        }
        return result.toString(); //형변환해서 반환
    }

    // 액션처리 (아래)
    @Override
    protected void onPreExecute() { //pre이벤트가 발생 시 자동 실행
        if(showLoadingMessage == true){ //"처리중입니다" 메세지 창이 있으면 실행
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(loadingMessage);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(result.equals("400")){
            progressDialog.dismiss();
            Toast.makeText(getContext(), "서버접속에러", Toast.LENGTH_LONG).show();
        }else if(result.equals("204")){
            progressDialog.dismiss();
            Toast.makeText(getContext(), "아이디/암호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
        }else{
            if(showLoadingMessage == true){
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
            result = result.trim(); //반환값의 양쪽 공백제거 트림 메서드 실행
            asyncResponse.processFinish(result);
        }
    }
}
