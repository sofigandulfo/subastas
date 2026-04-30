package com.tallerwebi.dominio;

public class Subasta {

    private String nombre;
    private String descripcion;
    private double precioInicial;
    private double precioMaximo;
    private String categoria;
    private String estadoArticulo;
    private String estadoSubasta;

    public Subasta() {
    }

    public Subasta(String nombre, String descripcion, double precioInicial, double precioMaximo, String categoria, String estadoArticulo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioInicial = precioInicial;
        this.precioMaximo = precioMaximo;
        this.categoria = categoria;
        this.estadoArticulo = estadoArticulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioInicial() {
        return precioInicial;
    }

    public void setPrecioInicial(double precioInicial) {
        this.precioInicial = precioInicial;
    }

    public double getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(double precioMaximo) {
        this.precioMaximo = precioMaximo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estadoArticulo;
    }

    public void setEstado(String estadoArticulo) {
        this.estadoArticulo = estadoArticulo;
    }

    public String getEstadoSubasta() { 
        return estadoSubasta; 
    }
    
    public void setEstadoSubasta(String estadoSubasta) {
         this.estadoSubasta = estadoSubasta; 
        }
    
}
