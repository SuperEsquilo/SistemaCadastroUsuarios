package dev.aplication.sistemaCadastro.service;

import dev.aplication.sistemaCadastro.dto.*;
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

@Service
public class RegistrarUsuarioService {

    private final RegistrarUsuarioRepository registrarUsuarioRepository;
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
    public RegistrarUsuarioResponseDTO registrar(RegistrarUsuarioRequestDTO dto) {
        String cpf = somenteDigitos(dto.cpf());
        String rg = somenteDigitos(dto.rg());
        String telefone = somenteDigitos(dto.telefone());
        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF de usuário já registrado.");
        }
        if (usuarioRepository.existsByRg(rg)) {
            throw new IllegalArgumentException("RG de usuário já registrado.");
        }
        if (usuarioRepository.existsByEmail(email) || registrarUsuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail de usuário já registrado.");
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

        RegistrarUsuario registro = new RegistrarUsuario();
        registro.setUsuario(salvo);
        registro.setEmail(email);
        registro.setSenha(passwordEncoder.encode(dto.senha()));
        registro.setAtivo(true);
        RegistrarUsuario registroSalvo = registrarUsuarioRepository.save(registro);

        return RegistrarUsuarioResponseDTO.fromEntity(salvo, registroSalvo);
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
            throw new EntityNotFoundException("Dados do usuário não encontrados.");
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
    public AlterarSenhaResponseDTO alterarSenha(Integer usuarioId, AlterarSenhaRequestDTO dto) {
        RegistrarUsuario registro = registrarUsuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Credenciais do usuário não encontradas: " + usuarioId));

        if (!passwordEncoder.matches(dto.senhaAtual(), registro.getSenha())) {
            throw new IllegalArgumentException("Senha atual está incorreta.");
        }

        registro.setSenha(passwordEncoder.encode(dto.novaSenha()));
        registrarUsuarioRepository.save(registro);

        return AlterarSenhaResponseDTO.ok("Senha alterada com sucesso.");
    }

    private String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}