package com.infinitystation.MarcosDesarrolloWeb.model;

import java.math.BigDecimal;

public class Pago {

    public enum Metodo {
        YAPE,
        TARJETA
    }

    private Long id;
    private Metodo metodo;
    private BigDecimal monto;
    private String estado;

    public Pago() {
    }

    public Pago(Long id, Metodo metodo, BigDecimal monto, String estado) {
        this.id = id;
        this.metodo = metodo;
        this.monto = monto;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Metodo getMetodo() {
        return metodo;
    }

    public void setMetodo(Metodo metodo) {
        this.metodo = metodo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
