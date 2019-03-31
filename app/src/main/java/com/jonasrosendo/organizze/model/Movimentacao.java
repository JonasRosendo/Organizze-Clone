package com.jonasrosendo.organizze.model;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.jonasrosendo.organizze.config.ConfigFirebase;
import com.jonasrosendo.organizze.helper.Base64Custom;
import com.jonasrosendo.organizze.helper.DateCustom;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private double valor;
    private String id;

    public Movimentacao() {
    }

    public void Salvar(String dataEscolhida){

        FirebaseAuth auth = ConfigFirebase.getAuth();
        DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();

        String mesAno = DateCustom.mesAnoEscolhido(dataEscolhida);

        String idUsuario = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());
        reference.child("movimentacao").child(idUsuario).child(mesAno).push().setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
