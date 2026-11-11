package com.comunidad.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

/** Lista reutilizable: tipo == null -> feed de Inicio (todo). */
public class PostsFragment extends Fragment {
    private String tipo; private PostAdapter adapter; private Db db; private Db.User yo;
    private TextView vacio;

    public static PostsFragment nuevo(String tipo) {
        PostsFragment f = new PostsFragment(); Bundle b = new Bundle(); b.putString("tipo", tipo); f.setArguments(b); return f; }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = i.inflate(R.layout.fragment_posts, c, false);
        tipo = getArguments().getString("tipo"); db = Db.get(requireContext()); yo = db.sesion(requireContext());
        vacio = v.findViewById(R.id.tvVacio);
        RecyclerView rv = v.findViewById(R.id.rv); rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(new PostAdapter.L() {
            @Override public void accion(Db.Post p) { alPulsar(p); }
            @Override public void largo(Db.Post p) { opciones(p); }
        });
        rv.setAdapter(adapter);
        v.findViewById(R.id.fab).setOnClickListener(x -> publicar());
        return v;
    }
    @Override public void onResume() { super.onResume(); recargar(); }
    private void recargar() {
        java.util.List<Db.Post> l = db.posts(tipo, yo.comunidad); adapter.set(l);
        vacio.setVisibility(l.isEmpty() ? View.VISIBLE : View.GONE);
    }
    private void publicar() {
        String t = tipo;
        if (t == null) { // desde Inicio, el usuario elige qué publicar
            String[] n = {"Evento", "Solicitud de ayuda", "Objeto para compartir", "Reporte"};
            String[] k = {Db.EVENTO, Db.AYUDA, Db.COMPARTE, Db.REPORTE};
            new AlertDialog.Builder(requireContext()).setTitle("¿Qué quieres compartir?")
                    .setItems(n, (d, w) -> abrirForm(k[w])).show(); return;
        }
        if (t.equals(Db.AVISO) && !yo.admin()) {
            Toast.makeText(getContext(), "Solo usuarios autorizados pueden publicar avisos", Toast.LENGTH_LONG).show(); return; }
        abrirForm(t);
    }
    private void abrirForm(String t) { startActivity(new Intent(getContext(), PostFormActivity.class).putExtra("tipo", t)); }

    private void alPulsar(Db.Post p) {
        if (p.tipo.equals(Db.EVENTO)) {
            if (p.estado.equals("Cancelado")) { Toast.makeText(getContext(), "Este evento fue cancelado", Toast.LENGTH_SHORT).show(); return; }
            db.confirmar(p.id); recargar(); Toast.makeText(getContext(), "Asistencia confirmada", Toast.LENGTH_SHORT).show();
        } else {
            if (p.autorId == yo.id) { Toast.makeText(getContext(), "Es tu publicación", Toast.LENGTH_SHORT).show(); return; }
            startActivity(new Intent(getContext(), ChatActivity.class).putExtra("otro", p.autorId)
                    .putExtra("nombre", p.autor).putExtra("sobre", p.titulo));
        }
    }
    /** Autor o administrador: cambiar estado (con motivo si se cierra/cancela) o eliminar. */
    private void opciones(Db.Post p) {
        if (!(yo.admin() || p.autorId == yo.id)) return;
        new AlertDialog.Builder(requireContext()).setItems(new String[]{"Cambiar estado", "Eliminar"}, (d, w) -> {
            if (w == 1) { db.eliminar(p.id); recargar(); return; }
            String[] op = Db.opciones(p.tipo);
            new AlertDialog.Builder(requireContext()).setTitle("Nuevo estado").setItems(op, (d2, k) -> {
                boolean cierra = op[k].equals("Cancelado") || op[k].equals("Cerrado") || op[k].equals("Cerrada");
                if (!cierra) { db.cambiarEstado(p, op[k], null); recargar(); return; }
                EditText et = new EditText(getContext()); et.setHint("Motivo");
                new AlertDialog.Builder(requireContext()).setTitle("Indica el motivo").setView(et)
                        .setPositiveButton("Aceptar", (d3, x) -> { db.cambiarEstado(p, op[k], et.getText().toString().trim()); recargar(); })
                        .setNegativeButton("Cancelar", null).show();
            }).show();
        }).show();
    }
}
