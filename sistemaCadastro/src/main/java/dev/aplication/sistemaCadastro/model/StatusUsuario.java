package dev.aplication.sistemaCadastro.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum StatusUsuario {
    ATIVO, DESATIVADO, BLOQUEADO, VERIFICACAO_PENDENTE
}