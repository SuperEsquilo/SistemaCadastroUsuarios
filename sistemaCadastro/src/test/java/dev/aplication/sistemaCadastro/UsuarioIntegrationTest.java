package dev.aplication.sistemaCadastro;

import dev.aplication.sistemaCadastro.dto.*;
import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.repository.RegistrarUsuarioRepository;
import dev.aplication.sistemaCadastro.repository.UsuarioRepository;
import dev.aplication.sistemaCadastro.service.RegistrarUsuarioService;
import dev.aplication.sistemaCadastro.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RegistrarUsuarioService registrarUsuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RegistrarUsuarioRepository registrarUsuarioRepository;

    @BeforeEach
    void setUp() {
        registrarUsuarioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar usuário com tipo e senha e autenticar com sucesso")
    void testCadastrarELogarComSucesso() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Carlos Silva",
                "carlos@teste.com",
                "11988887777",
                "12345678901",
                "123456789",
                "12345678",
                TipoUsuario.USUARIO
        );

        UsuarioResponseDTO salvo = usuarioService.salvar(dto);

        assertNotNull(salvo.id());
        assertEquals("Carlos Silva", salvo.nome());
        assertEquals("carlos@teste.com", salvo.email());
        assertEquals(TipoUsuario.USUARIO, salvo.tipoUsuario());
        assertEquals(StatusUsuario.ATIVO, salvo.status());
        assertTrue(salvo.ativo());

        // Autenticar com credenciais corretas
        LoginRequestDTO loginValido = new LoginRequestDTO("carlos@teste.com", "12345678");
        LoginResponseDTO loginResponse = usuarioService.autenticar(loginValido);

        assertNotNull(loginResponse);
        assertEquals(salvo.id(), loginResponse.id());
        assertEquals("carlos@teste.com", loginResponse.email());
        assertEquals(TipoUsuario.USUARIO, loginResponse.tipoUsuario());

        // Autenticar com senha incorreta
        LoginRequestDTO loginInvalido = new LoginRequestDTO("carlos@teste.com", "senhaerrada");
        assertThrows(BadCredentialsException.class, () -> usuarioService.autenticar(loginInvalido));
    }

    @Test
    @DisplayName("Deve registrar administrador por RegistrarUsuarioService e alterar senha")
    void testRegistrarAdministradorEAlterarSenha() {
        RegistrarUsuarioRequestDTO dto = new RegistrarUsuarioRequestDTO(
                "Ana Administradora",
                "ana@admin.com",
                "11977776666",
                "98765432100",
                "987654321",
                "senhaForte123",
                TipoUsuario.ADMINISTRADOR
        );

        RegistrarUsuarioResponseDTO salvo = registrarUsuarioService.registrar(dto);

        assertNotNull(salvo.id());
        assertEquals("Ana Administradora", salvo.nome());
        assertEquals("ana@admin.com", salvo.email());
        assertEquals(TipoUsuario.ADMINISTRADOR, salvo.tipoUsuario());

        // Login inicial
        LoginRequestDTO loginRequest = new LoginRequestDTO("ana@admin.com", "senhaForte123");
        LoginResponseDTO loginResponse = registrarUsuarioService.autenticar(loginRequest);
        assertEquals(TipoUsuario.ADMINISTRADOR, loginResponse.tipoUsuario());

        // Alterar Senha
        AlterarSenhaRequestDTO alterarDto = new AlterarSenhaRequestDTO("senhaForte123", "novaSenhaSegura99");
        AlterarSenhaResponseDTO alterarResponse = registrarUsuarioService.alterarSenha(salvo.id(), alterarDto);
        assertTrue(alterarResponse.sucesso());

        // Antiga senha deve falhar
        assertThrows(BadCredentialsException.class, () -> registrarUsuarioService.autenticar(loginRequest));

        // Nova senha deve funcionar
        LoginRequestDTO loginNovo = new LoginRequestDTO("ana@admin.com", "novaSenhaSegura99");
        LoginResponseDTO loginNovoResponse = registrarUsuarioService.autenticar(loginNovo);
        assertNotNull(loginNovoResponse);
        assertEquals("ana@admin.com", loginNovoResponse.email());
    }

    @Test
    @DisplayName("Deve rejeitar CPF ou Email duplicado")
    void testRejeitarDuplicados() {
        UsuarioRequestDTO dto1 = new UsuarioRequestDTO(
                "Lucas",
                "lucas@teste.com",
                "11911112222",
                "11122233344",
                "112233445",
                "12345678",
                TipoUsuario.USUARIO
        );
        usuarioService.salvar(dto1);

        UsuarioRequestDTO dtoDuplicadoEmail = new UsuarioRequestDTO(
                "Lucas Outro",
                "lucas@teste.com",
                "11999998888",
                "55566677788",
                "556677889",
                "12345678",
                TipoUsuario.USUARIO
        );
        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(dtoDuplicadoEmail));

        UsuarioRequestDTO dtoDuplicadoCpf = new UsuarioRequestDTO(
                "Lucas Outro CPF",
                "outro@teste.com",
                "11999998888",
                "11122233344",
                "998877665",
                "12345678",
                TipoUsuario.USUARIO
        );
        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(dtoDuplicadoCpf));
    }

    @Test
    @DisplayName("Deve desativar e reativar usuário bloqueando login enquanto desativado")
    void testDesativarEAtivarUsuario() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Juliana",
                "juliana@teste.com",
                "11933334444",
                "22233344455",
                "223344556",
                "12345678",
                TipoUsuario.USUARIO
        );
        UsuarioResponseDTO salvo = usuarioService.salvar(dto);

        // Desativar usuário
        UsuarioResponseDTO desativado = usuarioService.desativar(salvo.id());
        assertEquals(StatusUsuario.DESATIVADO, desativado.status());
        assertFalse(desativado.ativo());

        // Tentativa de login deve falhar
        LoginRequestDTO login = new LoginRequestDTO("juliana@teste.com", "12345678");
        assertThrows(IllegalStateException.class, () -> usuarioService.autenticar(login));

        // Reativar usuário
        UsuarioResponseDTO ativado = usuarioService.ativar(salvo.id());
        assertEquals(StatusUsuario.ATIVO, ativado.status());
        assertTrue(ativado.ativo());

        // Login deve voltar a funcionar
        LoginResponseDTO loginOk = usuarioService.autenticar(login);
        assertNotNull(loginOk);
    }
}
