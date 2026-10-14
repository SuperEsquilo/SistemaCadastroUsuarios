package dev.aplication.sistemaCadastro.service;

import java.util.List;
import java.util.Locale;

import jakarta.persistence.EntityNotFoundException;
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
        String cpf = somenteDigitos(usuario.getCpf());
        String email = usuario.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Usuário com esse CPF ja existe");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Usuário com esse Email ja cadastrado");
        }

        Usuario usuarioSalvo = new Usuario();
        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(somenteDigitos(usuario.getTelefone()));
        usuario.setCpf(cpf);
        usuario.setRg(somenteDigitos(usuario.getRg()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Integer id, Usuario usuario) {
        Usuario usuarioAtualizado = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
        String cpf = somenteDigitos(usuario.getCpf());
        String email = usuario.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new IllegalArgumentException("CPF de usuário ja cadastrado");
        }

        if (usuarioRepository.existsByCpfAndIdNot(email, id)) {
            throw new IllegalArgumentException("E-mail de usuário ja cadastrado");
        }

        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(somenteDigitos(usuario.getTelefone()));
        usuario.setCpf(cpf);
        usuario.setRg(somenteDigitos(usuario.getRg()));

        return usuario;
    }

    @Transactional
    public void deletar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontraddo: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}