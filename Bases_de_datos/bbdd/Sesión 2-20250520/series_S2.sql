# Definición del esquema y las tablas

DROP SCHEMA IF EXISTS series;
CREATE SCHEMA series;
USE series;

CREATE TABLE serie (
  id_serie INT,
  titulo VARCHAR(100),
  sinopsis VARCHAR(200),
  idioma VARCHAR(50),
  fecha_estreno DATE,
  PRIMARY KEY (id_serie)
);

CREATE TABLE temporada (
  id_serie INT,
  n_temporada INT,
  n_capitulos INT,
  fecha_estreno DATE,
  PRIMARY KEY (id_serie, n_temporada),
  FOREIGN KEY (id_serie) REFERENCES serie (id_serie)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE capitulo (
  id_serie INT,
  n_temporada INT,
  n_orden INT,
  fecha_estreno DATE,
  titulo INT,
  PRIMARY KEY (id_serie, n_temporada, n_orden),
  FOREIGN KEY (id_serie, n_temporada) REFERENCES temporada (id_serie, n_temporada)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE usuario (
  id_usuario INT,
  nombre VARCHAR(50),
  apellido1 VARCHAR(50),
  apellido2 VARCHAR(50),
  fotografia LONGBLOB,
  PRIMARY KEY (id_usuario)
);

CREATE TABLE genero (
  id_genero INT,
  descripcion VARCHAR(50),
  PRIMARY KEY (id_genero)
);

CREATE TABLE pertenece (
  id_serie INT,
  id_genero INT,
  PRIMARY KEY (id_serie, id_genero),
  FOREIGN KEY (id_serie) REFERENCES serie (id_serie)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_genero) REFERENCES genero (id_genero)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- Datos wikipedia, pero
-- los datos de ejemplo de temporadas no son reales
-- Fecha en yy-mm-dd

INSERT INTO serie VALUES(1, "Battlestar Galactica", "La base argumental es que en algún lugar distante del universo existe una civilización humana que vive en unos planetas llamados Las doce colonias de Kobol, [...]", "En", "2003-01-01");
INSERT INTO serie VALUES(2, "La leyenda de Vox Machina", "La serie está ambientada en Exandria, un mundo ficticio creado por Matthew Mercer en 2012 para su campaña personal de Dungeons & Dragons, que luego se lanzó como la serie web Critical Role en 2015.", "En", "2022-01-28");
INSERT INTO serie VALUES(3, "Love, Death & Robots", "Love, Death + Robots es una colección de historias cortas animadas que van desde la ciencia ficción hasta la fantasía, el horror y la comedia.", "En", "2019-03-15");

INSERT INTO temporada VALUES(3,99,99,"2013-12-13");

