package com.comunidad.app;

import android.os.Bundle;
import android.widget.*;

public class RegisterActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_register);
        setTitle("Registrarse");
        EditText n = findViewById(R.id.etNombre), e = findViewById(R.id.etEmail),
                p = findViewById(R.id.etPass), c = findViewById(R.id.etComunidad);
        findViewById(R.id.btnCrear).setOnClickListener(v -> {
            if (n.getText().toString().trim().isEmpty()) { n.setError("Obligatorio"); return; }
            if (!e.getText().toString().contains("@")) { e.setError("Correo no válido"); return; }
            if (p.getText().length() < 6) { p.setError("Mínimo 6 caracteres"); return; }
            if (c.getText().toString().trim().isEmpty()) { c.setError("Indica tu colonia o zona"); return; }
            if (!Db.get(this).registrar(n.getText().toString().trim(), e.getText().toString().trim(),
                    p.getText().toString(), c.getText().toString().trim())) {
                e.setError("Ese correo ya está registrado"); return; }
            Toast.makeText(this, "Cuenta creada. Ahora inicia sesión", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
