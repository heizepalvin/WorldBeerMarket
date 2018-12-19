package com.example.soyounguensoo.worldbeermarket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KakaoPaymentWebViewActivity extends AppCompatActivity {

    @BindView(R.id.kakao_payment_web_view)
    WebView kakaopayWebView;

    private String redirectUrl;
    private String tid;
    private String quantity;
    private String userName;
    private Handler javaScriptHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_payment_web_view);
        ButterKnife.bind(this);
        kakaopayWebView.getSettings().setJavaScriptEnabled(true);
        Intent webViewUrlGet = getIntent();

        redirectUrl = webViewUrlGet.getStringExtra("redirectUrl");
        tid = webViewUrlGet.getStringExtra("tid");
        Log.e("redirectURL",redirectUrl);
        kakaopayWebView.setWebViewClient(new webViewClient());
        kakaopayWebView.addJavascriptInterface(new JavascriptInterface(),"StreetLive");
        kakaopayWebView.loadUrl(redirectUrl);
    }

    final class JavascriptInterface{
        @android.webkit.JavascriptInterface
        public void paymentApproveMethod(final String result){
            javaScriptHandler.post(() -> {
                Log.e("result?",result);
                try {
                    JSONObject kakaopayObject = new JSONObject(result);
                    String tid = kakaopayObject.getString("tid");
                    userName = kakaopayObject.getString("partner_user_id");
                    String paymentType = kakaopayObject.getString("payment_method_type");
                    String itemName = kakaopayObject.getString("item_name");
                    quantity = kakaopayObject.getString("quantity");
                    String amount = kakaopayObject.getString("amount");
                    JSONObject kakaopayAmount = new JSONObject(amount);
                    String totalAmount = kakaopayAmount.getString("total");
                    String approveDateTime = kakaopayObject.getString("approved_at");
                    kakaoPaymentSaveToDB kakaoPaymentSaveToDB = new kakaoPaymentSaveToDB();
                    kakaoPaymentSaveToDB.execute(userName,tid,itemName,quantity,totalAmount,approveDateTime,paymentType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
        @android.webkit.JavascriptInterface
        public void paymentCancelMethod(final String str){
            javaScriptHandler.post(() -> {
                Intent paymentCancelActivityIntent = new Intent(KakaoPaymentWebViewActivity.this,MainActivity.class);
                startActivity(paymentCancelActivityIntent);
                finish();
            });
        }
        @android.webkit.JavascriptInterface
        public void paymentFailMethod(final String str){
            javaScriptHandler.post(() ->{
                Intent paymentFailActivityIntent = new Intent(KakaoPaymentWebViewActivity.this,MainActivity.class);
                startActivity(paymentFailActivityIntent);
                finish();
            });
        }
    }

    private class webViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(url != null && url.startsWith("intent://")){
                try{
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if(existPackage != null){
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            view.loadUrl(url);
            return false;
        }
    }

    //결제정보저장
    private class kakaoPaymentSaveToDB extends AsyncTask<String,Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent mainActivityMove = new Intent(KakaoPaymentWebViewActivity.this,MainActivity.class);
            mainActivityMove.putExtra("username",userName);
            startActivity(mainActivityMove);
            finish();
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            int pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            String nickname = strings[0];
            String tid = strings[1];
            String itemName = strings[2];
            String quantity = strings[3];
            String amount = strings[4];
            String approveDateTime = strings[5];
            String paymentType = strings[6];

            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "insert into payment.kakaopay_list (nickname,tid,item_name,amount,quantity,approve_datetime,payment_type) values('"
                        +nickname+"','"+tid+"','"+itemName+"','"+amount+"','"+quantity+"','"+approveDateTime+"','"+paymentType+"');";
                pgResult = pgStatement.executeUpdate(sql);
                if(pgResult!=0){
                    Log.e("kakaoPaymentSaveToDB","kakaopay Save To Database Success!");
                    pgStatement.close();
                }
            }catch (Exception e){
                Log.e("kakaoPaymentSaveToDB",e.toString());
            }
            return null;
        }
    }
}
