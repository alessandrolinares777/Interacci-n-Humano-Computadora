package com.comunidad.app;

import android.text.format.DateUtils;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.H> {
    public interface L { void accion(Db.Post p); void largo(Db.Post p); }
    private List<Db.Post> datos = new ArrayList<>(); private final L l;
    PostAdapter(L l) { this.l = l; }
    void set(List<Db.Post> n) { datos = n; notifyDataSetChanged(); }

    static class H extends RecyclerView.ViewHolder {
        TextView tipo, titulo, desc, meta, estado; Button btn;
        H(View v) { super(v); tipo = v.findViewById(R.id.tvTipo); titulo = v.findViewById(R.id.tvTitulo);
            desc = v.findViewById(R.id.tvDesc); meta = v.findViewById(R.id.tvMeta);
            estado = v.findViewById(R.id.tvEstado); btn = v.findViewById(R.id.btnAccion); }
    }
    @Override public H onCreateViewHolder(ViewGroup p, int t) {
        return new H(LayoutInflater.from(p.getContext()).inflate(R.layout.item_post, p, false)); }
    @Override public int getItemCount() { return datos.size(); }
    @Override public void onBindViewHolder(H h, int i) {
        Db.Post p = datos.get(i);
        h.tipo.setText(etiqueta(p.tipo)); h.titulo.setText(p.titulo); h.desc.setText(p.descripcion);
        h.estado.setText(p.estado);
        String m = "";
        if (p.fecha != null && !p.fecha.isEmpty()) m += "📅 " + p.fecha + "  ";
        if (p.lugar != null && !p.lugar.isEmpty()) m += "📍 " + p.lugar + "\n";
        m += p.autor + " · " + DateUtils.getRelativeTimeSpanString(p.creado);
        h.meta.setText(m);
        switch (p.tipo) {
            case Db.EVENTO: h.btn.setVisibility(View.VISIBLE); h.btn.setText("Confirmar asistencia (" + p.confirmados + ")"); break;
            case Db.AYUDA: h.btn.setVisibility(View.VISIBLE); h.btn.setText("Ofrecer ayuda"); break;
            case Db.COMPARTE: h.btn.setVisibility(View.VISIBLE); h.btn.setText("Contactar"); break;
            default: h.btn.setVisibility(View.GONE);
        }
        h.btn.setOnClickListener(v -> l.accion(p));
        h.itemView.setOnLongClickListener(v -> { l.largo(p); return true; });
    }
    static String etiqueta(String t) {
        switch (t) { case Db.EVENTO: return "EVENTO"; case Db.AYUDA: return "AYUDA"; case Db.COMPARTE: return "COMPARTE";
            case Db.REPORTE: return "REPORTE"; default: return "AVISO"; }
    }
}
