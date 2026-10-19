package dev.aplication.sistemaCadastro.service;

import dev.aplication.sistemaCadastro.dto.RegistrarUsuarioRequestDTO;
import dev.aplication.sistemaCadastro.dto.RegistrarUsuarioResponseDTO;
import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.model.Usuario;
import dev.aplication.sistemaCadastro.repository.RegistrarUsuarioRepository;
import dev.aplication.sistemaCadastro.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarUsuarioService {

    private RegistrarUsuarioRepository registrarUsuarioRepository;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public RegistrarUsuarioService(RegistrarUsuarioRepository registrarUsuarioRepository,
                                   UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder) {
        this.registrarUsuarioRepository = registrarUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrarUsuarioResponseDTO autenticar(RegistrarUsuarioRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();

        RegistrarUsuario registro = registrarUsuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha incorretos."));

        if (registro.getSenha() == null || !passwordEncoder.matches(dto.senha(), registro.getSenha())) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        if (!Boolean.TRUE.equals(registro.getAtivo())) {
            throw new IllegalStateException("Esta conta está desativada.");
        }

        Usuario usuario = registro.getUsuario();

        if (usuario.getStatus() == StatusUsuario.DESATIVADO) {
            throw new IllegalStateException("Esta conta está desativada.");
        }

        if (usuario.getStatus() == StatusUsuario.BLOQUEADO) {
            throw new IllegalStateException("Esta conta está bloqueada.");
        }

        return new RegistrarUsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }

    @Transactional
    public RegistrarUsuarioResponseDTO registrar(RegistrarUsuarioRequestDTO dto) {
        String cpf = somenteDigitos(dto.cpf());
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalStateException("CPF de usuário ja registrado.");
        }

        if (usuarioRepository.existsByEmail(email) || registrarUsuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("E-mail de usuário ja registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(somenteDigitos(dto.telefone()));
        usuario.setCpf(somenteDigitos(dto.cpf()));
        usuario.setRg(somenteDigitos(dto.rg()));
        usuario.setTipoUsuario(TipoUsuario.USUARIO);
        usuario.setStatus(StatusUsuario.VERIFICACAO_PENDENTE);

        Usuario salvo = usuarioRepository.save(usuario);

        RegistrarUsuario registro = new RegistrarUsuario();
        registro.setUsuario(salvo);
        registro.setEmail(email);
        registro.setSenha(passwordEncoder.encode(dto.senha()));
        registro.setAtivo(true);
        registrarUsuarioRepository.save(registro);

        return new RegistrarUsuarioResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTipoUsuario()
        );
    }

    @Transactional
    public void alterarSenha(Integer usuarioId, String senhaAtual, String novaSenha) {
        RegistrarUsuario registro = registrarUsuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Credencias do usuário não encontradas" + usuarioId));

        if (!passwordEncoder.matches(senhaAtual, registro.getSenha())) {
            throw new IllegalArgumentException("Senha atual está incorreta.");
        }

        registro.setSenha(passwordEncoder.encode(novaSenha));
    }

    private String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}