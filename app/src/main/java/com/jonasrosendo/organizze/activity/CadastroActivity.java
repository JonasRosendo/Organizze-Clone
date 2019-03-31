package com.jonasrosendo.organizze.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.jonasrosendo.organizze.R;
import com.jonasrosendo.organizze.config.ConfigFirebase;
import com.jonasrosendo.organizze.helper.Base64Custom;
import com.jonasrosendo.organizze.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private Button btn_cadastrar;
    private EditText edt_nome, edt_email, edt_senha;
    private FirebaseAuth auth;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //componentes
        edt_nome = findViewById(R.id.edt_cad_nome);
        edt_email = findViewById(R.id.edt_cad_email);
        edt_senha = findViewById(R.id.edt_cad_senha);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);

        //cadastrar usuário
        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recupera textos dos campos
                String nome = edt_nome.getText().toString().trim();
                String email = edt_email.getText().toString().trim();
                String senha = edt_senha.getText().toString().trim();

                //verifica se campos foram preenchido
                if(!nome.isEmpty()){
                    if (!email.isEmpty()){
                        if(!senha.isEmpty()){
                            //configura usuário para cadastro
                            usuario = new Usuario();
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setSenha(senha);
                            cadastrarUsuario(usuario);
                        }else{
                            Toast.makeText(CadastroActivity.this, getString(R.string.preencha_senha), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this, getString(R.string.preencha_email), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, getString(R.string.preencha_nome), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void cadastrarUsuario(final Usuario usuario) {
        auth = ConfigFirebase.getAuth();

        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String id_usuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(id_usuario);
                    usuario.salvarUser();
                    finish();
                }else{

                    //tratar exceções
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){ //exceção senha fraca
                        exception = getString(R.string.digite_senha_forte)
                        ;
                    }catch (FirebaseAuthInvalidCredentialsException e){ //exceção e-mail inválido
                        exception = getString(R.string.digite_email_valido);
                    }catch (FirebaseAuthUserCollisionException e){ //exceção e-mail já cadastrado
                        exception = getString(R.string.usuario_cadastrado);
                    }catch (Exception e){
                        exception = getString(R.string.falha_cad_user) + " " + e.getMessage();
                        e.printStackTrace();
                    }
                    //apresenta mensagem de erro
                    Toast.makeText(CadastroActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}