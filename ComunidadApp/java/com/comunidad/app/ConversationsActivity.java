package com.comunidad.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.util.*;

public class ConversationsActivity extends Base {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_conversations);
        setTitle("Mensajes");
    }
    @Override protected void onResume() {
        super.onResume();
        List<Db.User> l = Db.get(this).conversaciones(Db.get(this).sesion(this).id);
        findViewById(R.id.tvVacio).setVisibility(l.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        List<String> n = new ArrayList<>(); for (Db.User u : l) n.add(u.nombre);
        ListView lv = findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, n));
        lv.setOnItemClickListener((p, v, i, id) -> startActivity(new Intent(this, ChatActivity.class)
                .putExtra("otro", l.get(i).id).putExtra("nombre", l.get(i).nombre)));
    }
}
