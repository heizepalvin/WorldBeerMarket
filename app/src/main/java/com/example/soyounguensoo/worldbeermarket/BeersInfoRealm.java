package com.example.soyounguensoo.worldbeermarket;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BeersInfoRealm extends RealmObject {
    @PrimaryKey
    String id;
    String name;
    String tagline;
    String first_brewed;
    String description;
    String image_url;
    String abv;
    String ibu;
    String srm;
    String foodpairingA;
    String foodpairingB;
    String foodpairingC;
    String brewers_tips;
    String contributed_by;
    String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getFirst_brewed() {
        return first_brewed;
    }

    public void setFirst_brewed(String first_brewed) {
        this.first_brewed = first_brewed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getSrm() {
        return srm;
    }

    public void setSrm(String srm) {
        this.srm = srm;
    }

    public String getFoodpairingA() {
        return foodpairingA;
    }

    public void setFoodpairingA(String foodpairingA) {
        this.foodpairingA = foodpairingA;
    }

    public String getFoodpairingB() {
        return foodpairingB;
    }

    public void setFoodpairingB(String foodpairingB) {
        this.foodpairingB = foodpairingB;
    }

    public String getFoodpairingC() {
        return foodpairingC;
    }

    public void setFoodpairingC(String foodpairingC) {
        this.foodpairingC = foodpairingC;
    }

    public String getBrewers_tips() {
        return brewers_tips;
    }

    public void setBrewers_tips(String brewers_tips) {
        this.brewers_tips = brewers_tips;
    }

    public String getContributed_by() {
        return contributed_by;
    }

    public void setContributed_by(String contributed_by) {
        this.contributed_by = contributed_by;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
