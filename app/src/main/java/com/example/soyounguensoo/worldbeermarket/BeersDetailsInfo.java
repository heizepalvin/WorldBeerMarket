package com.example.soyounguensoo.worldbeermarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class BeersDetailsInfo extends AppCompatActivity {
    @BindView(R.id.beers_details_image)
    ImageView beersDetailsImage;
    @BindView(R.id.beers_details_name)
    TextView beersDetailsName;
    @BindView(R.id.beers_details_first_brewed)
    TextView beersDetailsBrewed;
    @BindView(R.id.beers_details_abv)
    TextView beersDetailsAbv;
    @BindView(R.id.beers_details_ibu)
    TextView beersDetailsIbu;
    @BindView(R.id.beers_details_srm)
    TextView beersDetailsSrm;
    @BindView(R.id.beers_details_food_a)
    TextView beersDetailsFoodA;
    @BindView(R.id.beers_details_food_b)
    TextView beersDetailsFoodB;
    @BindView(R.id.beers_details_food_c)
    TextView beersDetailsFoodC;
    @BindView(R.id.beers_details_tagline)
    TextView beersDetailsTagline;
    @BindView(R.id.beers_details_description)
    TextView beersDetailsDescription;
    @BindView(R.id.beers_details_brewers_tips)
    TextView beersDetailsBrewersTips;
    @BindView(R.id.beers_details_contributed)
    TextView beersDetailsContributed;
    @BindView(R.id.beers_details_getBtn)
    Button beersDetailsGetBtn;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beers_details_view);
        ButterKnife.bind(this);
        //Realm Database에서 맥주 데이터 가져오기
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        RealmResults<BeersInfoRealm> beerList = realm.where(BeersInfoRealm.class).findAll();
        Intent beersIdGet = getIntent();
        int id = beersIdGet.getIntExtra("id",0);
        beersDetailsName.setText(beerList.get(id).name);
        beersDetailsBrewed.setText("First_brewed : "+beerList.get(id).first_brewed);
        beersDetailsAbv.setText("ABV : "+beerList.get(id).abv);
        beersDetailsIbu.setText("IBU : "+beerList.get(id).ibu);
        beersDetailsSrm.setText("SRM : "+beerList.get(id).srm);
        beersDetailsFoodA.setText("Food_Pairing A : "+beerList.get(id).foodpairingA);
        beersDetailsFoodB.setText("Food_Pairing B : "+beerList.get(id).foodpairingB);
        beersDetailsFoodC.setText("Food_Pairing C : "+beerList.get(id).foodpairingC);
        beersDetailsTagline.setText("Tagline : "+beerList.get(id).tagline);
        beersDetailsDescription.setText("Description : "+beerList.get(id).description);
        beersDetailsBrewersTips.setText("BrewersTips : "+beerList.get(id).brewers_tips);
        beersDetailsContributed.setText("Contributed_By : "+beerList.get(id).contributed_by);
        Glide.with(this).load(beerList.get(id).image_url).into(beersDetailsImage);
        //구매하기 버튼 클릭
        beersDetailsGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beerBuyActivityMove = new Intent(BeersDetailsInfo.this,BeerBuyActivity.class);
                beerBuyActivityMove.putExtra("image_url",beerList.get(id).image_url);
                beerBuyActivityMove.putExtra("name",beerList.get(id).name);
                startActivity(beerBuyActivityMove);
                finish();
            }
        });
    }
}
