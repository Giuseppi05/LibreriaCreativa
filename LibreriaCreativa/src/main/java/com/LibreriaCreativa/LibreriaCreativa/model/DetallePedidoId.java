package com.LibreriaCreativa.LibreriaCreativa.model;

import java.io.Serializable;
import java.util.Objects;

public class DetallePedidoId implements Serializable {

    private Integer pedido;
    private Integer producto;

    public DetallePedidoId() {}

    public DetallePedidoId(Integer pedido, Integer producto) {
        this.pedido = pedido;
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetallePedidoId)) return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return Objects.equals(pedido, that.pedido) &&
               Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, producto);
    }
    
    
}
