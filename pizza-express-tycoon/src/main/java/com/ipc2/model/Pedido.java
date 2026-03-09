package com.ipc2.model;

public class Pedido {

    private int idPedido;
    private int idPartida;
    private String estadoActual;
    private int tiempoLimiteSegundos;
    private int tiempoRestanteSegundos;
    private String nombreProducto;

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public int getTiempoLimiteSegundos() {
        return tiempoLimiteSegundos;
    }

    public void setTiempoLimiteSegundos(int tiempoLimiteSegundos) {
        this.tiempoLimiteSegundos = tiempoLimiteSegundos;
    }

    public int getTiempoRestanteSegundos() {
        return tiempoRestanteSegundos;
    }

    public void setTiempoRestanteSegundos(int tiempoRestanteSegundos) {
        this.tiempoRestanteSegundos = tiempoRestanteSegundos;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}