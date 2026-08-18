CREATE TABLE processo_seletivo (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   nome VARCHAR(255) NOT NULL,
                                   descricao VARCHAR(255),
                                   qtd_vagas INT NOT NULL DEFAULT 0 CHECK (qtd_vagas >= 0),
                                   status VARCHAR(50) NOT NULL DEFAULT 'CADASTRADO'
);