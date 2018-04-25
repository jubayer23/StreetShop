
package com.creative.streetshop.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("customer_group_id")
    @Expose
    private String customerGroupId;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("language_id")
    @Expose
    private String languageId;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("telephone")
    @Expose
    private String telephone;
    @SerializedName("fax")
    @Expose
    private String fax;
    @SerializedName("wishlist")
    @Expose
    private List<Object> wishlist = null;
    @SerializedName("newsletter")
    @Expose
    private String newsletter;
    @SerializedName("address_id")
    @Expose
    private String addressId;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("safe")
    @Expose
    private String safe;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("date_added")
    @Expose
    private String dateAdded;
    @SerializedName("account_custom_field")
    @Expose
    private AccountCustomField accountCustomField;
    @SerializedName("custom_field")
    @Expose
    private CustomField customField;
    @SerializedName("wishlist_total")
    @Expose
    private String wishlistTotal;
    @SerializedName("cart_count_products")
    @Expose
    private Integer cartCountProducts;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerGroupId() {
        return customerGroupId;
    }

    public void setCustomerGroupId(String customerGroupId) {
        this.customerGroupId = customerGroupId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public List<Object> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<Object> wishlist) {
        this.wishlist = wishlist;
    }

    public String getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(String newsletter) {
        this.newsletter = newsletter;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public AccountCustomField getAccountCustomField() {
        return accountCustomField;
    }

    public void setAccountCustomField(AccountCustomField accountCustomField) {
        this.accountCustomField = accountCustomField;
    }

    public CustomField getCustomField() {
        return customField;
    }

    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }

    public String getWishlistTotal() {
        return wishlistTotal;
    }

    public void setWishlistTotal(String wishlistTotal) {
        this.wishlistTotal = wishlistTotal;
    }

    public Integer getCartCountProducts() {
        return cartCountProducts;
    }

    public void setCartCountProducts(Integer cartCountProducts) {
        this.cartCountProducts = cartCountProducts;
    }


    public static class AccountCustomField {

        @SerializedName("residential_address")
        @Expose
        private String residentialAddress;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("city")
        @Expose
        private String city;

        public String getResidentialAddress() {
            return residentialAddress;
        }

        public void setResidentialAddress(String residentialAddress) {
            this.residentialAddress = residentialAddress;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }



    public class CustomField {

        @SerializedName("account")
        @Expose
        private Account account;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

    }

    public class Account {

        @SerializedName("residential_address")
        @Expose
        private String residentialAddress;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("city")
        @Expose
        private String city;

        public String getResidentialAddress() {
            return residentialAddress;
        }

        public void setResidentialAddress(String residentialAddress) {
            this.residentialAddress = residentialAddress;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }

}
