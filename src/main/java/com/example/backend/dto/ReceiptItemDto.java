package com.example.backend.dto;

/**
 * 領収書明細1行分
 */
public class ReceiptItemDto {

  private final String productName;
  private final int quantity;
  private final int unitPrice;
  private final int amount;

  public ReceiptItemDto(String productName, int quantity, int unitPrice, int amount) {
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.amount = amount;
  }

  public String getProductName() { return productName; }
  public int getQuantity() { return quantity; }
  public int getUnitPrice() { return unitPrice; }
  public int getAmount() { return amount; }
}
