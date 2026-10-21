package dev.aplication.sistemaCadastro.repository;

import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    Optional<RegistrarUsuario> findByUsuario_Id(Integer usuarioId);

    @EntityGraph(attributePaths = "usuario")
    Optional<RegistrarUsuario> findByUsuario_Cpf(String cpf);

    @EntityGraph(attributePaths = "usuario")
    List<RegistrarUsuario> findByAtivo(Boolean ativo);

    long countByAtivo(Boolean ativo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE RegistrarUsuario r SET r.ativo = :ativo WHERE r.id = :id")
    int atualizarAtivo(@Param("id") Integer id, @Param("ativo") Boolean ativo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE RegistrarUsuario r SET r.senha = :senha WHERE r.id = :id")
    int atualizarSenha(@Param("id") Integer id, @Param("senha") String senhaCriptografada);

    @Modifying
    @Transactional
    void deleteByEmail(String email);
}