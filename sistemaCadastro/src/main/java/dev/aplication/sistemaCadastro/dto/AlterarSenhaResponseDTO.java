package dev.aplication.sistemaCadastro.dto;

public record AlterarSenhaResponseDTO(
        boolean sucesso,
        String mensagem
) {
    public static AlterarSenhaResponseDTO ok(String mensagem) {
        return new AlterarSenhaResponseDTO(true, mensagem);
    }
}
