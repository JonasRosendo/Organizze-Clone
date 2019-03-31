package com.jonasrosendo.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jonasrosendo.organizze.R;
import com.jonasrosendo.organizze.adapter.AdapterMovimentacao;
import com.jonasrosendo.organizze.config.ConfigFirebase;
import com.jonasrosendo.organizze.helper.Base64Custom;
import com.jonasrosendo.organizze.model.Movimentacao;
import com.jonasrosendo.organizze.model.Usuario;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView materialCalendarView;
    private RecyclerView recyclerView;
    private TextView txv_saudacao, txv_saldo;

    private FirebaseAuth auth = ConfigFirebase.getAuth();
    private DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference refUser;
    private ValueEventListener valueEventListenerUser;

    private double despesaTotal = 0.00, receitaTotal = 0.00, resumoUsuario = 0.00;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacaoList = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private ValueEventListener valueEventListenerMovimentacao;
    private String mesAnoSelecionado;

    private Movimentacao movimentacaoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Organizze");
        setSupportActionBar(toolbar);

        txv_saldo = findViewById(R.id.txv_saldo);
        txv_saudacao = findViewById(R.id.txv_saudacao);
        recyclerView = findViewById(R.id.recyclerView);
        materialCalendarView = findViewById(R.id.calendarView);
        configCalendarView();
        swipe();

        adapterMovimentacao = new AdapterMovimentacao(movimentacaoList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapterMovimentacao);

    }


    public void atualizarSaldo(){
        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUsuario);
        refUser = reference.child("usuarios").child(idUser);

        if(movimentacaoDialog.getTipo().equals("r")){
            receitaTotal -= movimentacaoDialog.getValor();
            refUser.child("receitaTotal").setValue(receitaTotal);

        }

        if(movimentacaoDialog.getTipo().equals("d")){
            despesaTotal -= movimentacaoDialog.getValor();
            refUser.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void recupeparMovimentacao(){

        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUsuario);
        movimentacaoRef = reference.child("movimentacao").child(idUser).child(mesAnoSelecionado);

        valueEventListenerMovimentacao = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movimentacaoList.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setId(dados.getKey());
                    movimentacaoList.add(movimentacao);
                }

                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo(){
        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUsuario);

        refUser = reference.child("usuarios").child(idUser);

        valueEventListenerUser = refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);

                txv_saudacao.setText("Olá, " + usuario.getNome());
                txv_saldo.setText("R$ " + resultadoFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void configCalendarView(){
        String meses[] = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        materialCalendarView.setTitleMonths(meses);

        CalendarDay dataAtual = materialCalendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", dataAtual.getMonth());
        mesAnoSelecionado = String.valueOf(mesSelecionado + "" + dataAtual.getYear());


        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", date.getMonth());
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + date.getYear());
                movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);
                recupeparMovimentacao();
            }
        });
    }

    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setTitle("Excluir movimentação da conta");
        alert.setMessage("Você tem certeza que deseja excluir movimentação ?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacaoDialog = movimentacaoList.get(position);

                String emailUsuario = auth.getCurrentUser().getEmail();
                String idUser = Base64Custom.codificarBase64(emailUsuario);

                movimentacaoRef = reference.child("movimentacao").child(idUser).child(mesAnoSelecionado);
                movimentacaoRef.child(movimentacaoDialog.getId()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();

            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapterMovimentacao.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.create();
        alert.show();
    }

    public void addDespesa(View view){
        startActivity(new Intent(PrincipalActivity.this, DespesasActivity.class));
    }

    public void addReceita(View view){
        startActivity(new Intent(PrincipalActivity.this, ReceitasActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refUser.removeEventListener(valueEventListenerUser);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recupeparMovimentacao();
    }
}