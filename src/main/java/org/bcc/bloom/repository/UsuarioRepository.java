package org.bcc.bloom.repository;

import org.bcc.bloom.model.Comprador;
import org.bcc.bloom.model.Vendedor;
import org.bcc.bloom.model.Usuario;
import java.util.ArrayList;

public class UsuarioRepository {
    private static final ArrayList<Comprador> compradores = new ArrayList<>();
    private static final ArrayList<Vendedor> vendedores = new ArrayList<>();

    public static void salvarComprador(Comprador comprador) {
        if (comprador != null) {
            compradores.add(comprador);
        }
    }

    public static void salvarVendedor(Vendedor vendedor) {
        if (vendedor != null) {
            vendedores.add(vendedor);
        }
    }

    public static Usuario buscarPorEmail(String email) {
        for (Comprador c : compradores) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        for (Vendedor v : vendedores) {
            if (v.getEmail().equalsIgnoreCase(email)) {
                return v;
            }
        }
        return null;
    }

    public static Usuario buscarUsuario(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }

    public static int getTotalCompradores() {
        return compradores.size();
    }

    public static int getTotalVendedores() {
        return vendedores.size();
    }
}