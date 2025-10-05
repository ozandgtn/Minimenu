package com.ozandgtn.minimenuapp.model;

/** Sepet satırı – yalnızca uygulama içinde kullanıyoruz */
public class OrderItem {
    private Long productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;

    public OrderItem() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
