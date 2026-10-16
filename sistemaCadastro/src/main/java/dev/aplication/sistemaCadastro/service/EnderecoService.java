
package dev.aplication.sistemaCadastro.service;

import dev.aplication.sistemaCadastro.dto.EnderecoRequestDTO;
import dev.aplication.sistemaCadastro.dto.EnderecoResponseDTO;
import dev.aplication.sistemaCadastro.model.Endereco;
import dev.aplication.sistemaCadastro.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listarTodos() {
         return enderecoRepository.findAll()
                 .stream()
                 .map(EnderecoResponseDTO::fromEntity)
                 .toList();
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarPorId(Integer id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID do endereço não encontrado: " + id));
        return EnderecoResponseDTO.fromEntity(endereco);
    }

    @Transactional
    public EnderecoResponseDTO salvar(@Valid EnderecoRequestDTO dto) {
        String cep = somenteDigitos(dto.cep());
        String ef = dto.uf().trim().toUpperCase();

        Endereco endereco = new Endereco();
        endereco.setRua(dto.rua().trim());
        endereco.setNumero(dto.numero().trim());
        endereco.setComplemento(dto.complemento() != null ? dto.complemento().trim() : null);
        endereco.setCidade(dto.cidade().trim());
        endereco.setUf(dto.uf());
        endereco.setCep(dto.cep());

        return EnderecoResponseDTO.fromEntity(enderecoRepository.save(endereco));
    }

    @Transactional
    public EnderecoResponseDTO atualizar(Integer id, @Valid EnderecoRequestDTO dto) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Endereco de usuário não encontrado: " + id));

        String cep = somenteDigitos(dto.cep());
        String uf = dto.uf().trim().toUpperCase();

        endereco.setRua(dto.rua().trim());
        endereco.setNumero(dto.numero().trim());
        endereco.setComplemento(dto.complemento() != null ? dto.complemento().trim() : null);
        endereco.setBairro(dto.bairro().trim());
        endereco.setCidade(dto.cidade().trim());
        endereco.setUf(uf);
        endereco.setCep(cep);

        return EnderecoResponseDTO.fromEntity(enderecoRepository.save(endereco));
    }

    @Transactional
    public void deletar(Integer id) {
        if (!enderecoRepository.existsById(id)) {
            throw new EntityNotFoundException("Endereco ddo usuário não encontrado: " + id);
        }
        enderecoRepository.deleteById(id);
    }

    private String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }
}