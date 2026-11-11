package com.comunidad.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Db.User u = Db.get(this).sesion(this);
        if (u == null) { finish(); return; }
        setContentView(R.layout.activity_main);
        setTitle(u.comunidad);
        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId(); Fragment f;
            if (id == R.id.nav_eventos) f = PostsFragment.nuevo(Db.EVENTO);
            else if (id == R.id.nav_ayudas) f = PostsFragment.nuevo(Db.AYUDA);
            else if (id == R.id.nav_comparte) f = PostsFragment.nuevo(Db.COMPARTE);
            else if (id == R.id.nav_perfil) f = new ProfileFragment();
            else f = PostsFragment.nuevo(null); // Inicio: feed general
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            return true;
        });
        if (b == null) nav.setSelectedItemId(R.id.nav_inicio);
    }
    @Override public boolean onCreateOptionsMenu(Menu m) { getMenuInflater().inflate(R.menu.main_menu, m); return true; }
    @Override public boolean onOptionsItemSelected(MenuItem i) {
        if (i.getItemId() == R.id.action_comunidad) { startActivity(new Intent(this, CommunityActivity.class)); return true; }
        return super.onOptionsItemSelected(i);
    }
}
