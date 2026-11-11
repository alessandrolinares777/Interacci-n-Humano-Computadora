package com.comunidad.app;

import android.os.Bundle;

/** Aloja un PostsFragment (Reportes o Avisos). */
public class HostActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_host);
        setTitle(getIntent().getStringExtra("titulo"));
        if (b == null) getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PostsFragment.nuevo(getIntent().getStringExtra("tipo"))).commit();
    }
}
