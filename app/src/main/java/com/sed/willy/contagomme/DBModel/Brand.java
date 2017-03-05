package com.sed.willy.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class Brand {
    int brandId;
    String brandName;
    int order;

    public Brand() {
        super();
    }

    public Brand(String name) {
        brandName = name;
    }

    public Brand(String name, int brandOrder) {
        brandName = name;
        order = brandOrder;
    }

    // setters

    public int getId() {
        return brandId;
    }

    public void setId(int brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return brandName;
    }
    // getters

    public void setName(String brandName) {
        this.brandName = brandName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


}
