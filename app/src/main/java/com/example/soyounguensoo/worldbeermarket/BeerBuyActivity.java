package com.example.soyounguensoo.worldbeermarket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BeerBuyActivity extends AppCompatActivity {
    @BindView(R.id.beer_buy_view_bubble_ani)
    LottieAnimationView beerBuyBubbleAnimation;
    @BindView(R.id.beer_buy_view_kakaoBtn)
    ImageView beerBuyKakaoBtn;
    @BindView(R.id.beer_buy_view_name)
    EditText beerBuyUserName;
    @BindView(R.id.beer_buy_view_image)
    ImageView beerBuyImage;
    @BindView(R.id.beer_buy_view_beer_name)
    TextView beerBuyBeerName;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String nextRedirectAppUrl;
    private String beerName;
    private String beerImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_buy_view);
        ButterKnife.bind(this);
        //lottie 사용
        beerBuyBubbleAnimation.setAnimation("beer_bubbles.json");
        //구매하기 버튼 누른 맥주 정보
        Intent getBuyBeerInfo = getIntent();
        beerName = getBuyBeerInfo.getStringExtra("name");
        beerImageUrl = getBuyBeerInfo.getStringExtra("image_url");
        beerBuyBeerName.setText(beerName);
        Glide.with(this).load(beerImageUrl).into(beerBuyImage);
        Glide.with(this).load(R.drawable.kakaopay)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(100)))
                .into(beerBuyKakaoBtn);
        //카카오페이 결제 버튼
        beerBuyKakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(beerBuyUserName.getText().toString().replace(" ","").equals("")){
                    Toast.makeText(BeerBuyActivity.this, "이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    kakaopayPaymentRequest kakaopayPaymentRequest = new kakaopayPaymentRequest();
                    kakaopayPaymentRequest.execute("5000원",beerName,beerBuyUserName.getText().toString());
                }
            }
        });
    }

    //카카오페이 결제요청 api
    private class kakaopayPaymentRequest extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String userPaymentPrice = strings[0];
            String beerName = strings[1];
            String userName = strings[2];
            RequestBody formbody = new FormBody.Builder()
                    .add("cid","TC0ONETIME")
                    .add("partner_order_id","StreetLive")
                    .add("partner_user_id",userName)
                    .add("item_name",beerName + " " + userPaymentPrice)
                    .add("quantity",userPaymentPrice.substring(0,userPaymentPrice.length()-1))
                    .add("total_amount",userPaymentPrice.substring(0,userPaymentPrice.length()-1))
                    .add("tax_free_amount","0")
                    .add("vat_amount","0")
                    .add("approval_url","http://210.89.190.131/kakaoPaymentApprove.php?userid="+userName)
                    .add("fail_url","http://210.89.190.131/kakaoPaymentFail.php?userid="+userName)
                    .add("cancel_url","http://210.89.190.131/kakaoPaymentCancel.php?userid="+userName)
                    .build();

            Request request = new Request.Builder()
                    .url("https://kapi.kakao.com/v1/payment/ready")
                    .post(formbody)
                    .addHeader("Authorization","KakaoAK a4dc70fa79ece1aeb64f46da172c4af8")
                    .build();

            okHttpClient.newCall(request).enqueue(kakaoPaymentRequestCallback);
            return null;
        }

        //카카오페이 결제요청 API 결과
        private Callback kakaoPaymentRequestCallback = new Callback() {
            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("kakaoPaymentRequestCallback","error Message : "+ e.getMessage());
            }


            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                Log.e("kakaoPaymentRequestCallback","responseData : " + responseData);
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String tid = jsonObject.getString("tid");
                    nextRedirectAppUrl = jsonObject.getString("next_redirect_app_url");
                    String androidAppScheme = jsonObject.getString("android_app_scheme");

                    kakaopayTidSendToApproveUrl kakaopayTidSendToApproveUrl = new kakaopayTidSendToApproveUrl();
                    kakaopayTidSendToApproveUrl.execute(tid,beerBuyUserName.getText().toString());

                    Intent webViewSendUrl = new Intent(BeerBuyActivity.this,KakaoPaymentWebViewActivity.class);
                    webViewSendUrl.putExtra("redirectUrl",nextRedirectAppUrl);
                    webViewSendUrl.putExtra("tid",tid);
                    webViewSendUrl.putExtra("androidScheme",androidAppScheme);
                    startActivity(webViewSendUrl);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //카카오페이 결제승인 API 요청 전 TID  redis에 저장하기위해 tid와 유저 닉네임 보내주는 AsyncTask
        private class kakaopayTidSendToApproveUrl extends AsyncTask<String,String,String>{

            @Override
            protected String doInBackground(String... strings) {
                String userTid = strings[0];
                String userId = strings[1];
                RequestBody formbody = new FormBody.Builder()
                        .add("ID",userId)
                        .add("tid",userTid)
                        .build();

                Request request = new Request.Builder()
                        .url("http://106.10.43.183:80/kakaopay/")
                        .post(formbody)
                        .build();
                okHttpClient.newCall(request).enqueue(kakaoPaymentRequestTidCallback);

                return null;
            }
        }
        //카카오페이 결제승인 API 요청 전 TID  redis에 저장하기위해 tid와 유저 닉네임 보내준 결과
        private Callback kakaoPaymentRequestTidCallback = new Callback() {
                @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("kakaoPaymentReqestTidCallback","error Message : "+ e.getMessage());

            }
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.e("kakaoPaymentReqestTidCallback","responseData : " + responseData);
            }
        };
    }
}
