package com.jonasrosendo.organizze.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.jonasrosendo.organizze.R;
import com.jonasrosendo.organizze.config.ConfigFirebase;
import com.jonasrosendo.organizze.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private Button btn_entrar;
    private EditText edt_log_email, edt_log_senha;
    private FirebaseAuth auth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        btn_entrar = findViewById(R.id.btn_entrar);
        edt_log_email = findViewById(R.id.edt_log_email);
        edt_log_senha = findViewById(R.id.edt_log_senha);

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recupera textos dos campos
                String email = edt_log_email.getText().toString().trim();
                String senha = edt_log_senha.getText().toString().trim();

                //verifica se campos foram preenchido
                if (!email.isEmpty()){
                    if(!senha.isEmpty()){
                        Usuario usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);
                        validarLogin(usuario);
                    }else{
                        Toast.makeText(LoginActivity.this, getString(R.string.preencha_senha), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, getString(R.string.preencha_email), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarLogin(Usuario usuario){
        auth = ConfigFirebase.getAuth();

        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //envia usuário para tela principal do app
                    abrirTelaPricipal();
                }else {

                    //trata exceções
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        exception = getString(R.string.usuario_nao_cadastrado);
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = getString(R.string.senha_email_invalido);
                    }catch (Exception e){
                        exception = getString(R.string.falha_login) + " " + e.getMessage();
                        e.printStackTrace();
                    }
                    //apresenta mensagem de erro
                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaPricipal(){
        startActivity( new Intent(this, PrincipalActivity.class));
        finish();
    }
}
