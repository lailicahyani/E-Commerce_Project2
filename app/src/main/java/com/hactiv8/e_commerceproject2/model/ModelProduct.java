package com.hactiv8.e_commerceproject2.model;

import java.lang.reflect.Constructor;

public class ModelProduct {
    private String productId, productTitle, productDesc, productCategory, productQuantity, logoProduct, originalPrice,
            discountPrice, discountNote, discountAvailable, timestamp, uid;

    public ModelProduct() {

    }

    public ModelProduct(String productId, String productTitle, String productDesc, String productCategory,
                        String productQuantity, String logoProduct, String originalPrice, String discountPrice,
                        String discountNote, String discountAvailable, String timestamp, String uid) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productDesc = productDesc;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.logoProduct = logoProduct;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getLogoProduct() {
        return logoProduct;
    }

    public void setLogoProduct(String logoProduct) {
        this.logoProduct = logoProduct;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
