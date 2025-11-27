package com.example.backend.dto;

import java.util.List;

/**
 * 領収書PDFのためのDTO（1注文分）
 */
public class ReceiptDto {

  private final Long orderId;
  private final String orderDate;      // "2025/01/01 15:23"
  private final String yearMonth;      // "2025/01"
  private final String receiptNumber;  // "R-20250101-00001"

  // 発行元情報（今回は固定値）
  private final String companyPostCode;
  private final String companyAddress;
  private final String companyName;

  // 金額
  private final int subTotal;
  private final int shipping;
  private final int total;

  // 明細行
  private final List<ReceiptItemDto> items;

  public ReceiptDto(
      Long orderId,
      String orderDate,
      String yearMonth,
      String receiptNumber,
      String companyPostCode,
      String companyAddress,
      String companyName,
      int subTotal,
      int shipping,
      int total,
      List<ReceiptItemDto> items
  ) {
    this.orderId = orderId;
    this.orderDate = orderDate;
    this.yearMonth = yearMonth;
    this.receiptNumber = receiptNumber;
    this.companyPostCode = companyPostCode;
    this.companyAddress = companyAddress;
    this.companyName = companyName;
    this.subTotal = subTotal;
    this.shipping = shipping;
    this.total = total;
    this.items = items;
  }

  public Long getOrderId() { return orderId; }
  public String getOrderDate() { return orderDate; }
  public String getYearMonth() { return yearMonth; }
  public String getReceiptNumber() { return receiptNumber; }
  public String getCompanyPostCode() { return companyPostCode; }
  public String getCompanyAddress() { return companyAddress; }
  public String getCompanyName() { return companyName; }
  public int getSubTotal() { return subTotal; }
  public int getShipping() { return shipping; }
  public int getTotal() { return total; }
  public List<ReceiptItemDto> getItems() { return items; }
}
