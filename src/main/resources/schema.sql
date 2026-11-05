CREATE DATABASE IF NOT EXISTS bloom_db CHARACTER SET utf8mb4;
USE bloom_db;

CREATE TABLE comprador (
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    senha VARCHAR(60) NOT NULL,
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- saldo em CENTAVOS (o Usuario.saldo e o CompraService trabalham em centavos)
    saldo BIGINT NOT NULL DEFAULT 0,
    endereco VARCHAR(200)
);

-- loja precisa existir ANTES de vendedor (vendedor.loja_id referencia loja.id)
CREATE TABLE loja (
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capa MEDIUMBLOB
);

CREATE TABLE vendedor (
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    senha VARCHAR(60) NOT NULL,
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    saldo BIGINT NOT NULL DEFAULT 0,
    loja_id BIGINT,
    FOREIGN KEY (loja_id) REFERENCES loja(id),
    perfil MEDIUMBLOB
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

CREATE TABLE produto (
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loja_id BIGINT,
    FOREIGN KEY (loja_id) REFERENCES loja(id),
    imagem MEDIUMBLOB,
    preco DECIMAL(10,2) NOT NULL,
    estoque INT DEFAULT 0,
    categoria ENUM('frutas', 'verduras', 'ervas', 'grãos', 'legumes')
);

CREATE TABLE item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    quantidade INT NOT NULL DEFAULT 1,
    
    container_id BIGINT NOT NULL,
    tipo_container ENUM('CARRINHO', 'PEDIDO') NOT NULL DEFAULT 'CARRINHO',
    valor_unitario DECIMAL(10,2) NOT NULL
);