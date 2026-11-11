package com.comunidad.app;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class PostFormActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_form);
        String tipo = getIntent().getStringExtra("tipo");
        setTitle("Nuevo: " + PostAdapter.etiqueta(tipo).toLowerCase());
        EditText t = findViewById(R.id.etTitulo), d = findViewById(R.id.etDesc),
                f = findViewById(R.id.etFecha), l = findViewById(R.id.etLugar);
        Spinner sp = findViewById(R.id.spEstado);
        boolean ev = tipo.equals(Db.EVENTO);
        findViewById(R.id.tilFecha).setVisibility(ev ? View.VISIBLE : View.GONE);
        findViewById(R.id.tilLugar).setVisibility(ev || tipo.equals(Db.REPORTE) ? View.VISIBLE : View.GONE);
        // Comparte: modalidad (regalar/intercambiar/prestar). Reporte/Ayuda/Evento/Aviso: estado inicial = primera opción.
        sp.setVisibility(tipo.equals(Db.COMPARTE) ? View.VISIBLE : View.GONE);
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Db.opciones(tipo)));
        findViewById(R.id.btnPublicar).setOnClickListener(v -> {
            if (t.getText().toString().trim().isEmpty()) { t.setError("El título es obligatorio"); return; }
            if (d.getText().toString().trim().isEmpty()) { d.setError("Agrega una descripción"); return; }
            if (ev && f.getText().toString().trim().isEmpty()) { f.setError("Indica fecha y hora"); return; }
            if (ev && l.getText().toString().trim().isEmpty()) { l.setError("Indica el lugar"); return; }
            Db.Post p = new Db.Post(); p.tipo = tipo; p.titulo = t.getText().toString().trim();
            p.descripcion = d.getText().toString().trim(); p.fecha = f.getText().toString().trim();
            p.lugar = l.getText().toString().trim();
            p.estado = tipo.equals(Db.COMPARTE) ? (String) sp.getSelectedItem() : Db.opciones(tipo)[0];
            Db.get(this).publicar(p, Db.get(this).sesion(this));
            Toast.makeText(this, "Publicado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
