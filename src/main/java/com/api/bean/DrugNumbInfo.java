package com.api.bean;

import java.io.Serializable;

/**
 * Created by marson on 2016/2/19.
 */
public class DrugNumbInfo implements Serializable {
    private String id;
    private String drugNumb;//批号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrugNumb() {
        return drugNumb;
    }

    public void setDrugNumb(String drugNumb) {
        this.drugNumb = drugNumb;
    }
}
