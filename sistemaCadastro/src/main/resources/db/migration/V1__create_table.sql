CREATE TABLE usuarios
(
    id               BIGSERIAL PRIMARY KEY,
    nome_usuario     VARCHAR(120)        NOT NULL,
    email_usuario    VARCHAR(120) UNIQUE NOT NULL,
    telefone_usuario VARCHAR(20)         NOT NULL,
    cpf_usuario      VARCHAR(11) UNIQUE  NOT NULL,
    rg_usuario       VARCHAR(11) UNIQUE  NOT NULL,
    ativo            BOOLEAN                      DEFAULT TRUE,
    criado_em        TIMESTAMP           NOT NULL DEFAULT NOW(),
    atualizado_em    TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE
OR REPLACE FUNCITON usuario_atualizado()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em
= NOW();
RETURN NEW;
END;
$$
LANGUAGE PLPGSQL;

CREATE TABLE