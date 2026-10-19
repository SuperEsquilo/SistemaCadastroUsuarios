package dev.aplication.sistemaCadastro.service;

import java.util.List;

import dev.aplication.sistemaCadastro.dto.UsuarioRequestDTO;
import dev.aplication.sistemaCadastro.dto.UsuarioResponseDTO;
import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.model.Usuario;
import dev.aplication.sistemaCadastro.repository.RegistrarUsuarioRepository;
import dev.aplication.sistemaCadastro.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RegistrarUsuarioRepository registrarUsuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RegistrarUsuarioRepository registrarUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.registrarUsuarioRepository = registrarUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Integer id) {
        Usuario usuario = buscarEntidade(id);
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        String cpf = somenteDigitos(dto.cpf());
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Usuário com esse CPF já existe.");
        }
        if (usuarioRepository.existsByEmail(email) || registrarUsuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Usuário com esse e-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(somenteDigitos(dto.telefone()));
        usuario.setCpf(cpf);
        usuario.setRg(somenteDigitos(dto.rg()));
        usuario.setTipoUsuario(dto.tipoUsuario() != null ? dto.tipoUsuario() : TipoUsuario.USUARIO);
        usuario.setStatus(StatusUsuario.VERIFICACAO_PENDENTE);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);

        String cpf = somenteDigitos(dto.cpf());
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new IllegalArgumentException("CPF já cadastrado para outro usuário.");
        }
        if (usuarioRepository.existsByEmailAndIdNot(email, id)
                || registrarUsuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("E-mail já cadastrado para outro usuário.");
        }

        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(somenteDigitos(dto.telefone()));
        usuario.setCpf(cpf);
        usuario.setRg(somenteDigitos(dto.rg()));

        if (dto.tipoUsuario() != null) {
            usuario.setTipoUsuario(dto.tipoUsuario());
        }

        RegistrarUsuario registro = buscarRegistro(id);
        registro.setEmail(email);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO ativar(Integer id) {
        Usuario usuario = buscarEntidade(id);
        RegistrarUsuario registro = buscarRegistro(id);

        if (usuario.getStatus() == StatusUsuario.ATIVO && Boolean.TRUE.equals(registro.getAtivo())) {
            throw new IllegalStateException("Este usuário já está ativo no sistema.");
        }

        usuario.setStatus(StatusUsuario.ATIVO);
        registro.setAtivo(true);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO desativar(Integer id) {
        Usuario usuario = buscarEntidade(id);
        RegistrarUsuario registro = buscarRegistro(id);

        if (usuario.getStatus() == StatusUsuario.DESATIVADO && !Boolean.TRUE.equals(registro.getAtivo())) {
            throw new IllegalStateException("Este usuário já está desativado no sistema.");
        }

        usuario.setStatus(StatusUsuario.DESATIVADO);
        registro.setAtivo(false);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario buscarEntidade(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
    }

    private RegistrarUsuario buscarRegistro(Integer usuarioId) {
        return registrarUsuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Credenciais não encontradas para o usuário: " + usuarioId));
    }

    private String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}