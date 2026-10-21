package dev.aplication.sistemaCadastro.service;

import dev.aplication.sistemaCadastro.dto.LoginRequestDTO;
import dev.aplication.sistemaCadastro.dto.LoginResponseDTO;
import dev.aplication.sistemaCadastro.dto.UsuarioRequestDTO;
import dev.aplication.sistemaCadastro.dto.UsuarioResponseDTO;
import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.model.Usuario;
import dev.aplication.sistemaCadastro.repository.RegistrarUsuarioRepository;
import dev.aplication.sistemaCadastro.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RegistrarUsuarioRepository registrarUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RegistrarUsuarioRepository registrarUsuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.registrarUsuarioRepository = registrarUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        String rg = somenteDigitos(dto.rg());
        String telefone = somenteDigitos(dto.telefone());
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Usuário com esse CPF já existe.");
        }
        if (usuarioRepository.existsByRg(rg)) {
            throw new IllegalArgumentException("Usuário com esse RG já existe.");
        }
        if (usuarioRepository.existsByEmail(email) || registrarUsuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Usuário com esse e-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setRg(rg);
        usuario.setTipoUsuario(dto.tipoUsuario() != null ? dto.tipoUsuario() : TipoUsuario.USUARIO);
        usuario.setStatus(StatusUsuario.ATIVO);

        Usuario salvo = usuarioRepository.save(usuario);

        if (dto.senha() != null && !dto.senha().isBlank()) {
            RegistrarUsuario registro = new RegistrarUsuario();
            registro.setUsuario(salvo);
            registro.setEmail(email);
            registro.setSenha(passwordEncoder.encode(dto.senha()));
            registro.setAtivo(true);
            registrarUsuarioRepository.save(registro);
            salvo.setRegistrarUsuario(registro);
        }

        return UsuarioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();

        RegistrarUsuario registro = registrarUsuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha incorretos."));

        if (!passwordEncoder.matches(dto.senha(), registro.getSenha())) {
            throw new BadCredentialsException("E-mail ou senha incorretos.");
        }

        if (!Boolean.TRUE.equals(registro.getAtivo())) {
            throw new IllegalStateException("Esta conta está desativada.");
        }

        Usuario usuario = registro.getUsuario();
        if (usuario == null) {
            throw new EntityNotFoundException("Dados de perfil do usuário não encontrados.");
        }

        if (usuario.getStatus() == StatusUsuario.DESATIVADO) {
            throw new IllegalStateException("Esta conta está desativada.");
        }

        if (usuario.getStatus() == StatusUsuario.BLOQUEADO) {
            throw new IllegalStateException("Esta conta está bloqueada.");
        }

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getStatus(),
                "Login realizado com sucesso!"
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);

        String cpf = somenteDigitos(dto.cpf());
        String rg = somenteDigitos(dto.rg());
        String telefone = somenteDigitos(dto.telefone());
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new IllegalArgumentException("CPF já cadastrado para outro usuário.");
        }
        if (usuarioRepository.existsByRgAndIdNot(rg, id)) {
            throw new IllegalArgumentException("RG já cadastrado para outro usuário.");
        }
        if (usuarioRepository.existsByEmailAndIdNot(email, id)
                || registrarUsuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("E-mail já cadastrado para outro usuário.");
        }

        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setRg(rg);

        if (dto.tipoUsuario() != null) {
            usuario.setTipoUsuario(dto.tipoUsuario());
        }

        registrarUsuarioRepository.findById(id).ifPresent(registro -> {
            registro.setEmail(email);
            if (dto.senha() != null && !dto.senha().isBlank()) {
                registro.setSenha(passwordEncoder.encode(dto.senha()));
            }
            registrarUsuarioRepository.save(registro);
        });

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO ativar(Integer id) {
        Usuario usuario = buscarEntidade(id);

        if (usuario.getStatus() == StatusUsuario.ATIVO) {
            throw new IllegalStateException("Este usuário já está ativo no sistema.");
        }

        usuario.setStatus(StatusUsuario.ATIVO);
        registrarUsuarioRepository.findById(id).ifPresent(registro -> {
            registro.setAtivo(true);
            registrarUsuarioRepository.save(registro);
        });

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO desativar(Integer id) {
        Usuario usuario = buscarEntidade(id);

        if (usuario.getStatus() == StatusUsuario.DESATIVADO) {
            throw new IllegalStateException("Este usuário já está desativado no sistema.");
        }

        usuario.setStatus(StatusUsuario.DESATIVADO);
        registrarUsuarioRepository.findById(id).ifPresent(registro -> {
            registro.setAtivo(false);
            registrarUsuarioRepository.save(registro);
        });

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado: " + id);
        }
        registrarUsuarioRepository.deleteById(id);
        usuarioRepository.deleteById(id);
    }

    private Usuario buscarEntidade(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
    }

    private String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}