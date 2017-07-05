package com.api.bean;

import java.util.List;

public class DrugstoreInfoList extends BaseResponse {
    private List<DrugstoreInfo> root;
    private int size;
    private int totleSize;


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DrugstoreInfo> getRoot() {
        return root;
    }

    public void setRoot(List<DrugstoreInfo> root) {

        this.root = root;
    }

    public int getTotleSize() {
        return totleSize;
    }

    public void setTotleSize(int totleSize) {
        this.totleSize = totleSize;
    }
}
