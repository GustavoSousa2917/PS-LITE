CREATE TABLE balao_informativo (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   titulo VARCHAR(255) NOT NULL,
                                   mensagem VARCHAR(255) NOT NULL,
                                   processo_seletivo_id BIGINT NOT NULL,

CONSTRAINT fk_balao_processo
                               FOREIGN KEY (processo_seletivo_id)
                               REFERENCES processo_seletivo(id)
                               ON DELETE CASCADE
);