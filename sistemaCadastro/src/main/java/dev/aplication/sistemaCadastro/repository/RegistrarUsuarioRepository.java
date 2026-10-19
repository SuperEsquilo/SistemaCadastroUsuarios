package dev.aplication.sistemaCadastro.repository;

import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegistrarUsuarioRepository extends JpaRepository<RegistrarUsuario, Integer> {

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByEmail(String email);

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByEmailAndAtivoTrue(String email);

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByUsuarioId(String usuarioId);

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByUsuarioCpf(String cpf);

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByAtivo(Boolean ativo);

    long countByAtivo(Boolean ativo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RegistrarUsuario r SET r.ativo = :ativo WHERE r.id = :id")
    int atualizarAtivo(@Param("id") Integer id, @Param("ativo") Boolean ativo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RegistrarUsuario r SET r.senha = :senha WHERE r.id = :id")
    int atualizarSenha(@Param("id") Integer id, @Param("senha") String senhaCriptografada);

    void deletarByEmail(String email);
}