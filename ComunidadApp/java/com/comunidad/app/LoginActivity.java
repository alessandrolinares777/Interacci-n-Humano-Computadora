package com.comunidad.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class LoginActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);
        setTitle("Iniciar sesión");
        EditText email = findViewById(R.id.etEmail), pass = findViewById(R.id.etPass);
        findViewById(R.id.btnEntrar).setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty()) { email.setError("Escribe tu correo"); return; }
            if (pass.getText().toString().isEmpty()) { pass.setError("Escribe tu contraseña"); return; }
            if (Db.get(this).login(this, email.getText().toString().trim(), pass.getText().toString()) == null) {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show(); return; }
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
