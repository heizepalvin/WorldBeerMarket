package com.example.soyounguensoo.worldbeermarket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.soyounguensoo.worldbeermarket.MainActivity.beerStyle;

public class BeerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class beerListViewHolder extends RecyclerView.ViewHolder{
        ImageView beerImage;
        TextView beerName;
        TextView beerAbv;
        TextView beerIbu;
        TextView beerSrm;
        public beerListViewHolder(View itemView) {
            super(itemView);
            beerImage = itemView.findViewById(R.id.beer_view_a_image);
            beerName = itemView.findViewById(R.id.beer_view_a_name);
            beerAbv = itemView.findViewById(R.id.beer_view_a_abv);
            beerIbu = itemView.findViewById(R.id.beer_view_a_ibu);
            beerSrm = itemView.findViewById(R.id.beer_view_a_srm);
        }
    }
    public class beerListViewHolderTips extends RecyclerView.ViewHolder{
        TextView beerTipsTitle;
        TextView beerTipsContent;
        public beerListViewHolderTips(View itemView) {
            super(itemView);
            beerTipsTitle = itemView.findViewById(R.id.beer_view_b_tips_title);
            beerTipsContent = itemView.findViewById(R.id.beer_view_b_tips_content);
        }
    }

    public class beerListViewHolderRandom extends RecyclerView.ViewHolder{
        TextView beerTodayName;
        TextView beerTodayAbv;
        TextView beerTodayIbu;
        TextView beerTodaySrm;
        ImageView beerTodayImage;
        Button beerTodayRandomBtn;
        public beerListViewHolderRandom(View itemView) {
            super(itemView);
            beerTodayName = itemView.findViewById(R.id.beer_view_c_title);
            beerTodayAbv = itemView.findViewById(R.id.beer_view_c_abv);
            beerTodayIbu = itemView.findViewById(R.id.beer_view_c_ibu);
            beerTodaySrm = itemView.findViewById(R.id.beer_view_c_srm);
            beerTodayImage = itemView.findViewById(R.id.beer_view_c_image);
            beerTodayRandomBtn = itemView.findViewById(R.id.beer_view_c_randomBtn);
        }
    }
    private ArrayList<BeersInfo> beersInfoList;
    BeerListAdapter(ArrayList<BeersInfo> beersInfoList){
        this.beersInfoList = beersInfoList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i){
            case 1:
                View viewA = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.beer_view_a,viewGroup, false);
                return new beerListViewHolder(viewA);
            case 2:
                View viewB = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.beer_view_b,viewGroup, false);
                return new beerListViewHolderTips(viewB);
            case 3:
                View viewC = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.beer_view_c,viewGroup,false);
                return new beerListViewHolderRandom(viewC);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if(viewHolder instanceof beerListViewHolder){
            final beerListViewHolder beerListViewHolder = (BeerListAdapter.beerListViewHolder) viewHolder;
            beerListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent beersDetailsInfoMove = new Intent(beerListViewHolder.itemView.getContext(),BeersDetailsInfo.class);
                    beersDetailsInfoMove.putExtra("id",i-1);
                    beerListViewHolder.itemView.getContext().startActivity(beersDetailsInfoMove);
                }
            });
            beerListViewHolder.beerName.setText(beersInfoList.get(i).name);
            beerListViewHolder.beerAbv.setText("ABV : "+beersInfoList.get(i).abv);
            beerListViewHolder.beerIbu.setText("IBU : "+beersInfoList.get(i).ibu);
            beerListViewHolder.beerSrm.setText("SRM : "+beersInfoList.get(i).srm);
            if(beersInfoList.get(i).srm != null){
                beerListViewHolder.beerSrm.setText("SRM : "+beersInfoList.get(i).srm);
            } else {
                beerListViewHolder.beerSrm.setText("SRM : 0");
            }
            Glide.with(viewHolder.itemView.getContext()).load(beersInfoList.get(i).image_url).into(beerListViewHolder.beerImage);
        }
        else if(viewHolder instanceof beerListViewHolderTips){
            final beerListViewHolderTips beerListViewHolderB = (BeerListAdapter.beerListViewHolderTips) viewHolder;
            beerListViewHolderB.beerTipsContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(beerListViewHolderB.itemView.getContext(),BeersDetailsInfo.class);
                    beerListViewHolderB.itemView.getContext().startActivity(intent);
                }
            });
        } else if(viewHolder instanceof beerListViewHolderRandom){
            init();
            final beerListViewHolderRandom beerListViewHolderRandom = (BeerListAdapter.beerListViewHolderRandom) viewHolder;
            Realm.init(beerListViewHolderRandom.itemView.getContext());
            realm = Realm.getDefaultInstance();
            beerListViewHolderRandom.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent beersDetailsInfoMove = new Intent(beerListViewHolderRandom.itemView.getContext(),BeersDetailsInfo.class);
                    beersDetailsInfoMove.putExtra("id",0);
                    beerListViewHolderRandom.itemView.getContext().startActivity(beersDetailsInfoMove);
                }
            });
            beerListViewHolderRandom.beerTodayName.setText(beersInfoList.get(i).name);
            beerListViewHolderRandom.beerTodayAbv.setText("ABV : "+beersInfoList.get(i).abv);
            beerListViewHolderRandom.beerTodayIbu.setText("IBU : "+beersInfoList.get(i).ibu);
            beerListViewHolderRandom.beerTodayRandomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PunkBeersInfo service = retrofit.create(PunkBeersInfo.class);
                    Call<JsonArray> request = service.getRandomBeersInfo();
                    request.enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                            result = response.body();
                            randomBeersInfo = gson.fromJson(String.valueOf(result),BeersInfo[].class);
                            beersInfoList.remove(0);
                            beersInfoList.addAll(0, Arrays.asList(randomBeersInfo));
                            beersInfoList.get(0).type = "random";
                            beersInfoList.get(0).id = String.valueOf(0);
                            updateBeersData(i);
                            BeerListAdapter.this.notifyItemChanged(0);
                        }
                        @Override
                        public void onFailure(Call<JsonArray> call, Throwable t) {
                            Log.e("beertodayrandomBtn","fail");
                        }
                    });
                }
            });
            if(beersInfoList.get(i).srm != null){
                beerListViewHolderRandom.beerTodaySrm.setText("SRM : "+beersInfoList.get(i).srm);
            } else {
                beerListViewHolderRandom.beerTodaySrm.setText("SRM : 0");
            }
            Glide.with(viewHolder.itemView.getContext()).load(beersInfoList.get(i).image_url).into(beerListViewHolderRandom.beerTodayImage);
        }
    }

    @Override
    public int getItemCount() {
        return beersInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewtype;
        if(beersInfoList.get(position).type != null){
            if(beersInfoList.get(position).type.equals("random")){
                viewtype = 3;
                return viewtype;
            } else if(beersInfoList.get(position).type.equals("help")){
                viewtype = 2;
                return viewtype;
            }
        } else {
            viewtype = 1;
            return viewtype;
        }
        return 0;
    }
    private Retrofit retrofit;
    private String BaseURL = "https://api.punkapi.com/";
    private Gson gson = new Gson();
    private JsonArray result;
    private BeersInfo[] randomBeersInfo;
    private Realm realm;

    private void updateBeersData(int i){
        final RealmResults<BeersInfoRealm> beerList = realm.where(BeersInfoRealm.class).findAll();
        Log.e("beersListSize", String.valueOf(beerList.size()));
        Log.e("position", String.valueOf(i));
        final BeersInfoRealm obj = new BeersInfoRealm();
        obj.setId(beersInfoList.get(i).id);
        obj.setName(beersInfoList.get(i).name);
        obj.setAbv(beersInfoList.get(i).abv);
        obj.setIbu(beersInfoList.get(i).ibu);
        obj.setSrm(beersInfoList.get(i).srm);
        obj.setDescription(beersInfoList.get(i).description);
        obj.setFirst_brewed(beersInfoList.get(i).first_brewed);
        obj.setBrewers_tips(beersInfoList.get(i).brewers_tips);
        obj.setImage_url(beersInfoList.get(i).image_url);
        obj.setContributed_by(beersInfoList.get(i).contributed_by);
        obj.setTagline(beersInfoList.get(i).tagline);
        obj.setFoodpairingA(beersInfoList.get(i).food_pairing[0]);
        obj.setFoodpairingB(beersInfoList.get(i).food_pairing[1]);
        obj.setFoodpairingC(beersInfoList.get(i).food_pairing[2]);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(obj);
            }
        });
    }
    private void init(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
