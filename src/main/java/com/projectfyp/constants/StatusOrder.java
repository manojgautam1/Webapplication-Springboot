package com.projectfyp.constants;


public enum StatusOrder {
    ORDER_PROGRESS(1,"Order in progress"),
    PRODUCT_READY(2,"Ready for delivery"),
    TRANSPORT_STATUS(3,"Out for delivery"),
    DELIVERY_STATUS(4,"Delivered"),
    RECEIVED_STATUS(5,"Received order"),
    CANCEL(6,"Order Cancelled");

    private int id;
    private String name;

    StatusOrder(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
