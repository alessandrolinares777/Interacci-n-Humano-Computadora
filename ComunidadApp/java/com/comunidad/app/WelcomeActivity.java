package com.comunidad.app;

import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Db.get(this).sesion(this) != null) { startActivity(new Intent(this, MainActivity.class)); finish(); return; }
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        findViewById(R.id.btnLogin).setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        findViewById(R.id.btnRegistro).setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
