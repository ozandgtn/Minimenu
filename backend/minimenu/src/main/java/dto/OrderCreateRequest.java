package com.minimenu.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderCreateRequest {

    @NotBlank
    @Size(min = 10, max = 500)
    private String address;

    @NotBlank
    private String cardHolderName;

    @NotBlank
    @Size(min = 12, max = 19)
    private String cardNumber;

    @NotNull
    private List<OrderItemRequest> items;

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
