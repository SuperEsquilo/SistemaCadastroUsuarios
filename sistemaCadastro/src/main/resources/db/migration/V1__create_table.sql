CREATE TABLE usuarios
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120)        NOT NULL,
    email         VARCHAR(120) UNIQUE NOT NULL,
    telefone      VARCHAR(20)         NOT NULL,
    cpf           VARCHAR(11) UNIQUE  NOT NULL,
    rg            VARCHAR(11) UNIQUE  NOT NULL,
    status        VARCHAR(50)         NOT NULL DEFAULT 'ATIVO'
        CHECK (status IN ('ATIVO', 'DESATIVADO', 'BLOQUEADO', 'VERIFICACAO_PENDENTE')),
    ativo         BOOLEAN                      DEFAULT TRUE,
    criado_em     TIMESTAMP           NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP           NOT NULL DEFAULT NOW()
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

CREATE TABLE endereco
(
    id            BIGSERIAL PRIMARY KEY,
    rua           VARCHAR(120) NOT NULL,
    numero        VARCHAR(10)  NOT NULL,
    complemento   VARCHAR(60),
    bairro        VARCHAR(120) NOT NULL,
    cidade        VARCHAR(100) NOT NULL,
    uf            CHAR(2)      NOT NULL,
    cep           CHAR(8)      NOT NULL,
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_endereco_cep CHECK (cep ~ '^[0-9]{8}$'
) ,
    CONSTRAINT ck_endereco_uf  CHECK (uf = UPPER(uf)));

CREATE INDEX idx_endereco_cep ON endereco (cep);