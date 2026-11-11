package com.comunidad.app;

import android.os.Bundle;
import android.widget.*;
import java.util.*;

public class ChatActivity extends Base {
    private ArrayAdapter<String> ad; private int yo, otro; private Db db;
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_chat);
        db = Db.get(this); yo = db.sesion(this).id; otro = getIntent().getIntExtra("otro", -1);
        setTitle(getIntent().getStringExtra("nombre"));
        EditText et = findViewById(R.id.etMsg);
        String sobre = getIntent().getStringExtra("sobre"); // mensaje vinculado a una publicación
        if (sobre != null) et.setText("Hola, me interesa tu publicación: " + sobre);
        ad = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        ListView lv = findViewById(R.id.lvMsgs); lv.setAdapter(ad);
        cargar();
        findViewById(R.id.btnEnviar).setOnClickListener(v -> {
            String s = et.getText().toString().trim(); if (s.isEmpty()) return;
            db.enviar(yo, otro, s); et.setText(""); cargar(); lv.setSelection(ad.getCount() - 1);
        });
    }
    private void cargar() {
        ad.clear();
        for (String[] m : db.hilo(yo, otro)) ad.add((Integer.parseInt(m[0]) == yo ? "Yo: " : getTitle() + ": ") + m[1]);
    }
}
