package com.alena;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "orders", schema = "main", catalog = "")
public class OrdersEntity {
    private short orderId;
    private String dateGet;
    private short customerId;

    @Id
    @Column(name = "order_id")
    public short getOrderId() {
        return orderId;
    }

    public void setOrderId(short orderId) {
        this.orderId = orderId;
    }


    @Column(name = "customer_id")
    public short getCustomerId() {
        return customerId;
    }

    public void setCustomerId(short customerId) {
        this.customerId = customerId;
    }

    
    @Column(name = "date_get")
    public String getDateGet() {
        return dateGet;
    }

    public void setDateGet(String dateGet) {
        this.dateGet = dateGet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdersEntity that = (OrdersEntity) o;
        return orderId == that.orderId && Objects.equals(dateGet, that.dateGet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, dateGet);
    }
}
