package dev.aplication.sistemaCadastro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.aplication.sistemaCadastro.model.Usuario;
import dev.aplication.sistemaCadastro.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("ID do usuario nao encontrado" + id));
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        Usuario usuarioSalvo = new Usuario();
        
        return null;
    }
}