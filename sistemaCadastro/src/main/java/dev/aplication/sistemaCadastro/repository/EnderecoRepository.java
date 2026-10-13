package dev.aplication.sistemaCadastro.repository;

import dev.aplication.sistemaCadastro.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    List<Endereco> findByCep(String cep);

    List<Endereco> findByCidadeIgnoreCase(String cidade);

    List<Endereco> findByCidadeIgnoreCaseAndUf(String cidade, String uf);

    List<Endereco> findByBairroContainingIgnoreCase(String bairro);

    List<Endereco> findByRuaContainingIgnoreCase(String rua);

    List<Endereco> findByUfOrderByCidadeAscBairroAsc(String uf);

    Optional<Endereco> findByCepAndNumeroAndComplemento(String cep, String numero, String complemento);

    Optional<Endereco> findByCepAndNumeroAndComplementoIsNull(String cep, String numero);

    List<Endereco> findByCriadoEmBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByCepAndNumero(String cep, String numero);

    Integer countByUf(String uf);

    Page<Endereco> findByUf(String uf, Pageable pageable);

    Page<Endereco> findByCidadeIgnoreCase(String cidade, Pageable pageable);

    @Query("""
            SELECT e FROM Endereco e
            WHERE (:cidade IS NULL OR LOWER(e.cidade) = LOWER(:cidade))
              AND (:uf IS NULL OR e.uf = :uf)
              AND (:bairro IS NULL OR LOWER(e.bairro) LIKE LOWER(CONCAT('%', :bairro, '%')))
            """)
    Page<Endereco> buscarComFlitros(@Param("cidade") String cidade,
                                    @Param("uf") String uf,
                                    @Param("bairro") String bairro,
                                    Pageable pageable);

    @Query("SELECT DISTINCT e.cidade FROM Endereco e WHERE e.uf = :uf ORDER BY e.cidade")
    List<String> listarCidadesPorUf(@Param("uf") String uf);

    @Query("SELECT e.uf, COUNT(e) FROM Endereco e GROUP BY e.uf ORDER BY COUNT(e) DESC")
    List<Object[]> contarPorUf();

    @Query(value = "SELECT * FROM endereco WHERE cep LIKE :prefixo || '%' ORDER BY cep",
            nativeQuery = true)
    List<Endereco> buscarPorPrefixoCep(@Param("prefixo") String prefixo);

    @Modifying
    @Transactional
    @Query("UPDATE Endereco e SET e.uf = UPPER(e.uf) WHERE e.uf <> UPPER(e.uf)")
    int normalizarUf();

    @Modifying
    @Transactional
    Integer deleteByCep(String cep);

    interface EnderecoResumo {
        Integer getId();

        String getRua();

        String getNumero();

        String getCidade();

        String getUf();
    }

    List<EnderecoResumo> findResumoByCep(String cep);
}