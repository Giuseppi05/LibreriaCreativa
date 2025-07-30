/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LibreriaCreativa.LibreriaCreativa.model;

import java.util.List;

public class PedidoRequest {

    private Float subtotal;
    private Float envio;
    private Float descuento;
    private Float total;
    private String direccion;
    private String tipoPago;
    private String imgYape;
    private DatosTarjeta datosTarjeta;

    private List<ProductoItem> productos;

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }

    public Float getEnvio() {
        return envio;
    }

    public void setEnvio(Float envio) {
        this.envio = envio;
    }

    public Float getDescuento() {
        return descuento;
    }

    public void setDescuento(Float descuento) {
        this.descuento = descuento;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getImgYape() {
        return imgYape;
    }

    public void setImgYape(String imgYape) {
        this.imgYape = imgYape;
    }

    public DatosTarjeta getDatosTarjeta() {
        return datosTarjeta;
    }

    public void setDatosTarjeta(DatosTarjeta datosTarjeta) {
        this.datosTarjeta = datosTarjeta;
    }

    public List<ProductoItem> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoItem> productos) {
        this.productos = productos;
    }

    public static class ProductoItem {
        public Integer idProducto;
        public Integer cantidad;
        public Float subtotal;

        public Integer getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public Float getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Float subtotal) {
            this.subtotal = subtotal;
        }
       
    }
    
    public static class DatosTarjeta {
        private String numero;
        private String cvv;
        private String expiracion;

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public String getExpiracion() {
            return expiracion;
        }

        public void setExpiracion(String expiracion) {
            this.expiracion = expiracion;
        }
        
        
    }
}
