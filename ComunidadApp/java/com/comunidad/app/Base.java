package com.comunidad.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Base extends AppCompatActivity {
    @Override protected void onPostCreate(Bundle b) {
        super.onPostCreate(b);
        if (getSupportActionBar() != null && !(this instanceof MainActivity) && !(this instanceof WelcomeActivity))
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
