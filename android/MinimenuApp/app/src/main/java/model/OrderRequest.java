package com.ozandgtn.minimenuapp.model;

import java.util.List;

public class OrderRequest {

    private String address;
    private String cardHolderName;
    private String cardNumber;
    private List<Item> items;

    // API'ye giden her satir
    public static class Item {
        private Long productId;
        private int quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
