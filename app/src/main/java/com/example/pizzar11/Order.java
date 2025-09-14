package com.example.pizzar11;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Order {
    private String id;
    private String customerName;
    private String address;
    private List<Map<String, Object>> items;
    private Date date;
    private String paymentMethod;
    private String status;

    public Order() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}