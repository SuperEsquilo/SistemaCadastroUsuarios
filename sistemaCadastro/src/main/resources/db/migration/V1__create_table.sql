CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    rg VARCHAR(11) UNIQUE NOT NULL,
    tipo_usuario VARCHAR(25) NOT NULL DEFAULT 'USUARIO' CHECK (tipo_usuario IN ('USUARIO', 'ADMINISTRADOR')),
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVO' CHECK (
        status IN (
            'ATIVO',
            'DESATIVADO',
            'BLOQUEADO',
            'VERIFICACAO_PENDENTE'
        )
    ),
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION usuario_atualizado() RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_usuario_atualizado
BEFORE UPDATE ON usuarios
FOR EACH ROW EXECUTE FUNCTION usuario_atualizado();

CREATE TABLE endereco (
    id SERIAL PRIMARY KEY,
    rua VARCHAR(120) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(60),
    bairro VARCHAR(120) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    uf CHAR(2) NOT NULL,
    cep CHAR(8) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_endereco_cep CHECK (cep ~ '^[0-9]{8}$'),
    CONSTRAINT ck_endereco_uf CHECK (uf = UPPER(uf))
);

CREATE INDEX idx_endereco_cep ON endereco (cep);

CREATE TABLE registrarUsuario(
    usuario_id SERIAL PRIMARY KEY,
    email VARCHAR(120) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);