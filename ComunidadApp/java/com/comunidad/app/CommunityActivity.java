package com.comunidad.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CommunityActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_community);
        setTitle("Mi comunidad");
        Db.User u = Db.get(this).sesion(this);
        ((TextView) findViewById(R.id.tvNombre)).setText(u.comunidad);
        ((TextView) findViewById(R.id.tvInfo)).setText(Db.get(this).miembros(u.comunidad) + " miembros");
        findViewById(R.id.btnReportes).setOnClickListener(v -> abrir(Db.REPORTE, "Reportes comunitarios"));
        findViewById(R.id.btnAvisos).setOnClickListener(v -> abrir(Db.AVISO, "Avisos importantes"));
        findViewById(R.id.btnMensajes).setOnClickListener(v -> startActivity(new Intent(this, ConversationsActivity.class)));
    }
    private void abrir(String tipo, String titulo) {
        startActivity(new Intent(this, HostActivity.class).putExtra("tipo", tipo).putExtra("titulo", titulo));
    }
}
