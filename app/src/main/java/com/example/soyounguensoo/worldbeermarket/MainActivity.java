package com.example.soyounguensoo.worldbeermarket;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    RecyclerView.LayoutManager beerListLayoutManager;
    private String BaseURL = "https://api.punkapi.com/";
    //punk API에서 가져온 JSON 데이터 처리
    private JsonArray result;
    private JsonArray resultB;
    private Gson gson = new Gson();
    private BeersInfo[] beersInfo;
    private BeersInfo[] randomBeersInfo;
    private ArrayList<BeersInfo> beersInfoList;
    public static String beerStyle = "all";
    private int page_count = 1;
    @BindView(R.id.beerListViewProgressBar)
    LottieAnimationView beerListViewProgressBar;
    @BindView(R.id.beerListView)
    RecyclerView beerListView;
    private Realm realm;

    private int paging_save_count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_list);
        ButterKnife.bind(this);
        //Realm 사용
        Realm.init(this);
        //Retrofit 사용
        init();
        beerListViewProgressBar.setAnimation("preloader.json");
        //구매하기 후 해당 유저의 이름 Toast로 띄우기
        Intent getUserName = getIntent();
        if(getUserName.getStringExtra("username") != null){
            Toast.makeText(this, getUserName.getStringExtra("username"), Toast.LENGTH_SHORT).show();
        }
        //Punk API에 맥주 정보 요청
        final PunkBeersInfo service = retrofit.create(PunkBeersInfo.class);
        final Call<JsonArray> request = service.getBeersInfo();
        final Call<JsonArray> requestB = service.getRandomBeersInfo();
        request.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                result = response.body();
                beersInfo = gson.fromJson(String.valueOf(result),BeersInfo[].class);
                requestB.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        //랜덤 맥주 뽑기 데이터 먼저 0번째에 집어 넣고
                        //나머지 맥주 리스트 가져오기
                        resultB = response.body();
                        randomBeersInfo = gson.fromJson(String.valueOf(resultB),BeersInfo[].class);
                        beersInfoList =  new ArrayList<>(Arrays.asList(randomBeersInfo));
                        beersInfoList.get(0).type = "random";
                        beersInfoList.get(0).id = String.valueOf(0);
                        beersInfoList.addAll(Arrays.asList(beersInfo));
                        BeersInfo helpAdd = new BeersInfo("help");
                        beersInfoList.add(1,helpAdd);
                        //Realm
                        realm = Realm.getDefaultInstance();
                        final RealmResults<BeersInfoRealm> beerList = realm.where(BeersInfoRealm.class).findAll();
                        Log.e("pagingtest beersInfoList = ", String.valueOf(beersInfoList.size()));
                        if(beerList.size() == 0){
                            insertBeersData(paging_save_count);
                        } else {
                            updateRandomBeersData(0);
                        }
                        //Recycler View
                        beerListLayoutManager = new LinearLayoutManager(getApplicationContext());
                        beerListView.setLayoutManager(beerListLayoutManager);
                        final BeerListAdapter beerListAdapter = new BeerListAdapter(beersInfoList);
                        beerListView.setAdapter(beerListAdapter);
                        //페이징 처리
                        //Recycler View 제일 하단에 닿았을 때 25개 페이지를 추가로 가져오기
                        beerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if(!recyclerView.canScrollVertically(1)){
                                    beerListViewProgressBar.setVisibility(View.VISIBLE);
                                    beerListViewProgressBar.playAnimation();
                                    page_count++;
                                    paging_save_count = beersInfoList.size();
                                    Call<JsonArray> requestPaging = service.getBeersInfoPaging(page_count);
                                    requestPaging.enqueue(new Callback<JsonArray>() {
                                        @Override
                                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                            result = response.body();
                                            beersInfo = gson.fromJson(String.valueOf(result),BeersInfo[].class);
                                            beersInfoList.addAll(Arrays.asList(beersInfo));
                                            if(paging_save_count>beerList.size()){
                                                insertBeersData(paging_save_count);
                                                Log.e("pagingtest beersList = ", String.valueOf(beerList.size()));
                                            }
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    beerListAdapter.notifyDataSetChanged();
                                                    beerListViewProgressBar.setVisibility(View.GONE);
                                                    beerListViewProgressBar.pauseAnimation();
                                                }
                                            },1000);
                                        }
                                        @Override
                                        public void onFailure(Call<JsonArray> call, Throwable t) {
                                            Log.e("페이징처리","fail");
                                        }
                                    });
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        Log.e("requestB","fail");
                    }
                });
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("request","fail");
            }
        });
    }
    //랜덤 맥주 뽑기 버튼 눌렀을 때 0번째 데이터 update
    private void updateRandomBeersData(int i){
        final BeersInfoRealm obj = new BeersInfoRealm();
        obj.setId(beersInfoList.get(i).id);
        obj.setName(beersInfoList.get(i).name);
        if(beersInfoList.get(i).abv == null){
            obj.setAbv("0");
        } else {
            obj.setAbv(beersInfoList.get(i).abv);
        }
        if(beersInfoList.get(i).ibu == null){
            obj.setIbu("0");
        } else {
            obj.setIbu(beersInfoList.get(i).ibu);
        }
        if(beersInfoList.get(i).srm == null){
            obj.setSrm("0");
        } else {
            obj.setSrm(beersInfoList.get(i).srm);
        }
        obj.setDescription(beersInfoList.get(i).description);
        obj.setFirst_brewed(beersInfoList.get(i).first_brewed);
        obj.setBrewers_tips(beersInfoList.get(i).brewers_tips);
        obj.setImage_url(beersInfoList.get(i).image_url);
        obj.setContributed_by(beersInfoList.get(i).contributed_by);
        obj.setTagline(beersInfoList.get(i).tagline);
        obj.setFoodpairingA(beersInfoList.get(i).food_pairing[0]);
        if(beersInfoList.get(i).food_pairing.length >= 2){
            obj.setFoodpairingB(beersInfoList.get(i).food_pairing[1]);
        }
        if(beersInfoList.get(i).food_pairing.length >= 3){
            obj.setFoodpairingC(beersInfoList.get(i).food_pairing[2]);
        }
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(obj));
    }
    //Realm Database에 맥주 데이터 저장
    private void insertBeersData(int count){
        for(int i=count; i<beersInfoList.size(); i++){
            if(beersInfoList.get(i).type == null || beersInfoList.get(i).type.equals("random")){
                    realm.beginTransaction();
                    BeersInfoRealm beersInfoRealm = realm.createObject(BeersInfoRealm.class,beersInfoList.get(i).id);
                    beersInfoRealm.name = beersInfoList.get(i).name;
                    if(beersInfoList.get(i).abv == null){
                        beersInfoRealm.abv = "0";
                    } else {
                        beersInfoRealm.abv = beersInfoList.get(i).abv;
                    }
                    if(beersInfoList.get(i).ibu == null){
                        beersInfoRealm.ibu = "0";
                    } else {
                        beersInfoRealm.ibu = beersInfoList.get(i).ibu;
                    }
                    if(beersInfoList.get(i).srm == null){
                        beersInfoRealm.srm = "0";
                    } else {
                        beersInfoRealm.srm = beersInfoList.get(i).srm;
                    }
                    beersInfoRealm.description = beersInfoList.get(i).description;
                    beersInfoRealm.first_brewed = beersInfoList.get(i).first_brewed;
                    beersInfoRealm.brewers_tips = beersInfoList.get(i).brewers_tips;
                    beersInfoRealm.image_url = beersInfoList.get(i).image_url;
                    beersInfoRealm.contributed_by = beersInfoList.get(i).contributed_by;
                    beersInfoRealm.tagline = beersInfoList.get(i).tagline;
                    beersInfoRealm.foodpairingA = beersInfoList.get(i).food_pairing[0];
                    if(beersInfoList.get(i).food_pairing.length >= 2){
                        beersInfoRealm.foodpairingB = beersInfoList.get(i).food_pairing[1];
                    }
                    if(beersInfoList.get(i).food_pairing.length >= 3){
                        beersInfoRealm.foodpairingC = beersInfoList.get(i).food_pairing[2];
                    }
                    realm.commitTransaction();
                }
        }
    }
    private void init(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
