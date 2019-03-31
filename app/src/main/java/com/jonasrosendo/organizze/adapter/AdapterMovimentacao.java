package com.jonasrosendo.organizze.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jonasrosendo.organizze.R;
import com.jonasrosendo.organizze.model.Movimentacao;

import java.util.List;

public class AdapterMovimentacao extends RecyclerView.Adapter<AdapterMovimentacao.MyViewHolder> {

    private List<Movimentacao> movimentacaoList;
    private Context context;

    public AdapterMovimentacao(List<Movimentacao> movimentacaoList, Context context) {
        this.movimentacaoList = movimentacaoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_resumo, viewGroup, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Movimentacao movimentacao = movimentacaoList.get(i);

        holder.txv_titulo.setText(movimentacao.getDescricao());
        holder.txv_categoria.setText(movimentacao.getCategoria());
        holder.txv_valor.setText("R$ " + String.valueOf(movimentacao.getValor()));
        holder.txv_valor.setTextColor(context.getResources().getColor(R.color.colorReceitaPrimary));

        if(movimentacao.getTipo().equals("d")){
            holder.txv_valor.setTextColor(context.getResources().getColor(R.color.colorDespesaAccent));
            holder.txv_valor.setText("R$ -" + movimentacao.getValor());
        }
    }

    @Override
    public int getItemCount() {
        return movimentacaoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txv_titulo;
        TextView txv_categoria;
        TextView txv_valor;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txv_titulo = itemView.findViewById(R.id.txv_titulo);
            txv_categoria = itemView.findViewById(R.id.txv_categoria);
            txv_valor = itemView.findViewById(R.id.txv_valor);
        }
    }
}
