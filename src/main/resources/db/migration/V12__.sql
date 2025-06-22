-- Adiciona nova coluna 'name' nas tabelas
ALTER TABLE type_provider ADD name VARCHAR(255);
ALTER TABLE type_status ADD name VARCHAR(255);

-- Copia os dados de 'nome' para 'name'
UPDATE type_provider SET name = nome;
UPDATE type_status SET name = nome;

-- Define a nova coluna como NOT NULL (após popular os dados)
ALTER TABLE type_provider ALTER COLUMN name SET NOT NULL;
ALTER TABLE type_status ALTER COLUMN name SET NOT NULL;

-- Remove a coluna antiga 'nome' das tabelas
ALTER TABLE type_provider DROP COLUMN nome;
ALTER TABLE type_status DROP COLUMN nome;