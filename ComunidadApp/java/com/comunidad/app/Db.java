package com.comunidad.app;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.security.MessageDigest;
import java.util.*;

/** Capa de acceso a datos (SQLite). Una sola tabla "posts" con campo tipo. */
public class Db extends SQLiteOpenHelper {
    public static final String EVENTO = "EVENTO", AYUDA = "AYUDA", COMPARTE = "COMPARTE",
            REPORTE = "REPORTE", AVISO = "AVISO";

    public static class User { public int id; public String nombre, email, comunidad, rol;
        public boolean admin() { return "admin".equals(rol); } }
    public static class Post { public int id, autorId, confirmados; public long creado;
        public String tipo, titulo, descripcion, fecha, lugar, estado, autor; }

    private static Db inst; private static User actual;
    public static Db get(Context c) { if (inst == null) inst = new Db(c.getApplicationContext()); return inst; }
    private Db(Context c) { super(c, "comunidad.db", null, 1); }

    @Override public void onCreate(SQLiteDatabase d) {
        d.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT,email TEXT UNIQUE,pass TEXT,comunidad TEXT,rol TEXT)");
        d.execSQL("CREATE TABLE posts(id INTEGER PRIMARY KEY AUTOINCREMENT,tipo TEXT,titulo TEXT,descripcion TEXT,fecha TEXT,lugar TEXT,estado TEXT,autor_id INTEGER,autor TEXT,comunidad TEXT,confirmados INTEGER DEFAULT 0,creado INTEGER)");
        d.execSQL("CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT,de INTEGER,para INTEGER,texto TEXT,creado INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase d, int o, int n) {}

    static String hash(String s) {
        try { StringBuilder sb = new StringBuilder();
            for (byte b : MessageDigest.getInstance("SHA-256").digest(s.getBytes())) sb.append(String.format("%02x", b));
            return sb.toString(); } catch (Exception e) { return s; }
    }

    // ---- Usuarios ----
    /** El primer usuario de una comunidad queda como administrador. */
    public boolean registrar(String nombre, String email, String pass, String comunidad) {
        SQLiteDatabase d = getWritableDatabase();
        Cursor c = d.rawQuery("SELECT COUNT(*) FROM users WHERE comunidad=?", new String[]{comunidad});
        c.moveToFirst(); String rol = c.getInt(0) == 0 ? "admin" : "user"; c.close();
        ContentValues v = new ContentValues();
        v.put("nombre", nombre); v.put("email", email.toLowerCase()); v.put("pass", hash(pass));
        v.put("comunidad", comunidad); v.put("rol", rol);
        return d.insert("users", null, v) != -1;
    }
    public User login(Context ctx, String email, String pass) {
        Cursor c = getReadableDatabase().rawQuery("SELECT id,nombre,email,comunidad,rol FROM users WHERE email=? AND pass=?",
                new String[]{email.toLowerCase(), hash(pass)});
        User u = c.moveToFirst() ? leer(c) : null; c.close();
        if (u != null) { actual = u; ctx.getSharedPreferences("s", 0).edit().putInt("uid", u.id).apply(); }
        return u;
    }
    public User sesion(Context ctx) {
        if (actual == null) { int id = ctx.getSharedPreferences("s", 0).getInt("uid", -1);
            if (id != -1) actual = usuario(id); }
        return actual;
    }
    public void logout(Context ctx) { actual = null; ctx.getSharedPreferences("s", 0).edit().clear().apply(); }
    public User usuario(int id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT id,nombre,email,comunidad,rol FROM users WHERE id=?", new String[]{"" + id});
        User u = c.moveToFirst() ? leer(c) : null; c.close(); return u;
    }
    private User leer(Cursor c) { User u = new User(); u.id = c.getInt(0); u.nombre = c.getString(1);
        u.email = c.getString(2); u.comunidad = c.getString(3); u.rol = c.getString(4); return u; }
    public int miembros(String com) {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM users WHERE comunidad=?", new String[]{com});
        c.moveToFirst(); int n = c.getInt(0); c.close(); return n;
    }

    // ---- Publicaciones ----
    public static String[] opciones(String tipo) {
        switch (tipo) {
            case EVENTO: return new String[]{"Programado", "Cancelado"};
            case AYUDA: return new String[]{"Abierta", "En coordinación", "Cerrada"};
            case COMPARTE: return new String[]{"Regalar", "Intercambiar", "Prestar"};
            case REPORTE: return new String[]{"Pendiente", "En seguimiento", "Resuelto"};
            default: return new String[]{"Activo", "Cerrado"};
        }
    }
    public void publicar(Post p, User u) {
        ContentValues v = new ContentValues();
        v.put("tipo", p.tipo); v.put("titulo", p.titulo); v.put("descripcion", p.descripcion);
        v.put("fecha", p.fecha); v.put("lugar", p.lugar); v.put("estado", p.estado);
        v.put("autor_id", u.id); v.put("autor", u.nombre); v.put("comunidad", u.comunidad);
        v.put("creado", System.currentTimeMillis());
        getWritableDatabase().insert("posts", null, v);
    }
    public List<Post> posts(String tipo, String com) {
        List<Post> l = new ArrayList<>();
        String sql = "SELECT id,tipo,titulo,descripcion,fecha,lugar,estado,autor_id,autor,confirmados,creado FROM posts WHERE comunidad=?"
                + (tipo != null ? " AND tipo=?" : "") + " ORDER BY creado DESC";
        Cursor c = getReadableDatabase().rawQuery(sql, tipo != null ? new String[]{com, tipo} : new String[]{com});
        while (c.moveToNext()) { Post p = new Post(); p.id = c.getInt(0); p.tipo = c.getString(1); p.titulo = c.getString(2);
            p.descripcion = c.getString(3); p.fecha = c.getString(4); p.lugar = c.getString(5); p.estado = c.getString(6);
            p.autorId = c.getInt(7); p.autor = c.getString(8); p.confirmados = c.getInt(9); p.creado = c.getLong(10); l.add(p); }
        c.close(); return l;
    }
    public void confirmar(int id) { getWritableDatabase().execSQL("UPDATE posts SET confirmados=confirmados+1 WHERE id=?", new Object[]{id}); }
    /** Si se cierra/cancela se conserva visible, agregando el motivo. */
    public void cambiarEstado(Post p, String estado, String motivo) {
        String desc = p.descripcion + (motivo != null && !motivo.isEmpty() ? "\n[Motivo: " + motivo + "]" : "");
        getWritableDatabase().execSQL("UPDATE posts SET estado=?,descripcion=? WHERE id=?", new Object[]{estado, desc, p.id});
    }
    public void eliminar(int id) { getWritableDatabase().delete("posts", "id=?", new String[]{"" + id}); }

    // ---- Mensajería privada ----
    public void enviar(int de, int para, String texto) {
        ContentValues v = new ContentValues(); v.put("de", de); v.put("para", para); v.put("texto", texto);
        v.put("creado", System.currentTimeMillis()); getWritableDatabase().insert("messages", null, v);
    }
    public List<String[]> hilo(int a, int b) { // {deId, texto}
        List<String[]> l = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT de,texto FROM messages WHERE (de=? AND para=?) OR (de=? AND para=?) ORDER BY id",
                new String[]{"" + a, "" + b, "" + b, "" + a});
        while (c.moveToNext()) l.add(new String[]{"" + c.getInt(0), c.getString(1)});
        c.close(); return l;
    }
    public List<User> conversaciones(int yo) {
        List<User> l = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT DISTINCT CASE WHEN de=? THEN para ELSE de END FROM messages WHERE de=? OR para=?",
                new String[]{"" + yo, "" + yo, "" + yo});
        List<Integer> ids = new ArrayList<>(); while (c.moveToNext()) ids.add(c.getInt(0)); c.close();
        for (int i : ids) { User u = usuario(i); if (u != null) l.add(u); }
        return l;
    }
}
