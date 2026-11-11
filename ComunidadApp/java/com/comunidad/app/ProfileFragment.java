package com.comunidad.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = i.inflate(R.layout.fragment_profile, c, false);
        Db.User u = Db.get(requireContext()).sesion(requireContext());
        ((TextView) v.findViewById(R.id.tvNombre)).setText(u.nombre);
        ((TextView) v.findViewById(R.id.tvCom)).setText(u.comunidad + " · " + Db.get(requireContext()).miembros(u.comunidad) + " miembros");
        ((TextView) v.findViewById(R.id.tvRol)).setText((u.admin() ? "Administrador" : "Miembro") + " · " + u.email);
        v.findViewById(R.id.btnSalir).setOnClickListener(x -> {
            Db.get(requireContext()).logout(requireContext());
            Intent in = new Intent(getContext(), WelcomeActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); startActivity(in);
        });
        return v;
    }
}
