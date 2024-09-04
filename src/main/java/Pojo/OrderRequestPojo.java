package Pojo;

import java.util.List;

public class OrderRequestPojo {
    public List<OrderDetail> getOrder() {
        return Order;
    }

    public void setOrder(List<OrderDetail> order) {
        Order = order;
    }

    public List<OrderDetail> Order;

}
