package com.infinitystation.MarcosDesarrolloWeb.model;

import java.math.BigDecimal;

public class Pedido {

    private Long id;
    private String cliente;
    private String producto;
    private BigDecimal total;
    private String estado;
    private String plataforma;
    private int cantidad;
    private String imagen;

    public Pedido() {
    }

    public Pedido(
            Long id,
            String cliente,
            String producto,
            BigDecimal total,
            String estado,
            String plataforma,
            int cantidad,
            String imagen) {

        this.id = id;
        this.cliente = cliente;
        this.producto = producto;
        this.total = total;
        this.estado = estado;
        this.plataforma = plataforma;
        this.cantidad = cantidad;
        this.imagen = imagen;
    }

    public Pedido(
            String cliente,
            String producto,
            BigDecimal total,
            String estado) {

        this.cliente = cliente;
        this.producto = producto;
        this.total = total;
        this.estado = estado;
        this.plataforma = "PS5";
        this.cantidad = 1;
        this.imagen = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}