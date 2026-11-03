CREATE DATABASE bloom_db;
use bloom_db;

CREATE TABLE comprador (
                           nome VARCHAR(50) NOT NULL,
                           email VARCHAR(50) NOT NULL UNIQUE,
                           cpf VARCHAR(11) NOT NULL UNIQUE,
                           senha VARCHAR(60) NOT NULL,
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           saldo DECIMAL(10,2) NOT NULL,
                           endereco VARCHAR(200)
);

CREATE TABLE vendedor (
                          nome VARCHAR(50) NOT NULL,
                          email VARCHAR(50) NOT NULL UNIQUE,
                          cnpj VARCHAR(11) NOT NULL UNIQUE,
                          senha VARCHAR(60) NOT NULL,
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          loja_id BIGINT,
                          FOREIGN KEY (loja_id) references loja(id),
                          perfil BLOB
);

CREATE TABLE carrinho (
                          comprador_id BIGINT,
                          FOREIGN KEY (comprador_id) REFERENCES comprador(id),
                          id BIGINT PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE pedido (
                        comprador_id BIGINT,
                        FOREIGN KEY (comprador_id) REFERENCES comprador(id),
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        valor_total DECIMAL(10,2)
);

CREATE TABLE loja (
                      nome VARCHAR(50)NOT NULL,
                      descricao VARCHAR(500),
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      capa BLOB
);

CREATE TABLE produto (
                         nome VARCHAR(50) NOT NULL,
                         descricao VARCHAR(500),
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         loja_id BIGINT,
                         FOREIGN KEY (loja_id) REFERENCES loja(id),
                         imagem BLOB,
                         preco DECIMAL(10,2) NOT NULL,
                         estoque INT DEFAULT 0,
                         categoria ENUM("frutas", "verduras", "ervas", "grãos", "legumes")
);

CREATE TABLE item (
                      produto_id BIGINT,
                      FOREIGN KEY (produto_id) REFERENCES produto(id),
                      quantidade int NOT NULL DEFAULT 1,
                      id BIGINT PRIMARY KEY,
                      container_id BIGINT, # O container pode ser um carrinho ou um pedido
                      valor_unitario DECIMAL(10,2) NOT NULL
);
