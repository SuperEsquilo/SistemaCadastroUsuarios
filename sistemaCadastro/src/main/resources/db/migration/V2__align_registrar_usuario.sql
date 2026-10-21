CREATE TABLE IF NOT EXISTS registrar_usuario (
    usuario_id INTEGER PRIMARY KEY,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    email VARCHAR(120) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    CONSTRAINT fk_registrar_usuario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);