package com.jonasrosendo.organizze.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jonasrosendo.organizze.R;
import com.jonasrosendo.organizze.config.ConfigFirebase;
import com.jonasrosendo.organizze.helper.Base64Custom;
import com.jonasrosendo.organizze.helper.DateCustom;
import com.jonasrosendo.organizze.model.Movimentacao;
import com.jonasrosendo.organizze.model.Usuario;

public class DespesasActivity extends AppCompatActivity {

    private EditText edt_valor;
    private TextInputEditText edt_data, edt_categoria, edt_descricao;
    private FloatingActionButton fab_despesas;
    private Movimentacao movimentacao;
    private DatabaseReference reference = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfigFirebase.getAuth();
    private double despesaTotal, despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        edt_valor = findViewById(R.id.edt_valor_despesa);
        edt_categoria = findViewById(R.id.edt_categoria_despesa);
        edt_data = findViewById(R.id.edt_data_despesa);
        edt_descricao = findViewById(R.id.edt_descricao_despesa);
        fab_despesas = findViewById(R.id.fab_salvar_despesas);
        //recuperar despesa total no firebase
        recuperarDespesas();
        //preencher campo com data atual
        edt_data.setText(DateCustom.dataAtual());

        fab_despesas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDespesa();
            }
        });
    }

    public void salvarDespesa(){

        if(validarCampos()){
            movimentacao = new Movimentacao();
            String data = edt_data.getText().toString().trim();
            double valor = Double.parseDouble(edt_valor.getText().toString().trim());

            movimentacao.setValor(valor);
            movimentacao.setCategoria(edt_categoria.getText().toString().trim());
            movimentacao.setDescricao(edt_descricao.getText().toString().trim());
            movimentacao.setData(data);
            movimentacao.setTipo("d");

            despesaAtualizada = despesaTotal + valor;
            atualizarDespesas( despesaAtualizada);
            movimentacao.Salvar(data);

            Toast.makeText(getApplicationContext(), getString(R.string.salvo_sucesso), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public boolean validarCampos(){

        String valor = edt_valor.getText().toString().trim();
        String categoria = edt_categoria.getText().toString().trim();
        String descricao = edt_descricao.getText().toString().trim();
        String data = edt_data.getText().toString().trim();

        if(!valor.isEmpty()){
            if(!data.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!descricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(this, getString(R.string.preencha_descricao), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this, getString(R.string.preencha_categoria), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, getString(R.string.preencha_data), Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, getString(R.string.preencha_valor), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarDespesas(){

        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUsuario);

        DatabaseReference refUser = reference.child("usuarios").child(idUser);
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarDespesas(double despesaAtualizada){
        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference refUser = reference.child("usuarios").child(idUser);
        refUser.child("despesaTotal").setValue(despesaAtualizada);
    }
}
