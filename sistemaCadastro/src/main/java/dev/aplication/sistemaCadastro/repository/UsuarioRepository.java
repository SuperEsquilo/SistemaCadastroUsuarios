package dev.aplication.sistemaCadastro.repository;

import dev.aplication.sistemaCadastro.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByRg(String rg);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByCpfAndIdNot(String cpf, Integer id);

    boolean existsByRgAndIdNot(String rg, Integer id);

    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);


    @Query("""
            SELECT u FROM Usuario u
            WHERE LOWER(u.nome)  LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR u.cpf = :termo
            """)
    Page<Usuario> pesquisar(@Param("termo") String termo, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.ativo = false WHERE u.id = :id")
    void desativar(@Param("id") Integer id);

    @Transactional
    void deleteByCpf(String cpf);

    long countByAtivoTrue();
}