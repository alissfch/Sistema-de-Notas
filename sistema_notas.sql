-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: sistema_notas
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrador` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `UKferp3xx2iyuy3qltd4ey5pf7l` (`id_usuario`),
  CONSTRAINT `FKpt2bj0l5q4npigarogy7p1834` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES (1,1);
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alumno`
--

DROP TABLE IF EXISTS `alumno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alumno` (
  `estado` bit(1) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `id_alumno` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `apellidos` varchar(255) NOT NULL,
  `codigo_institucional` varchar(255) DEFAULT NULL,
  `nombres` varchar(255) NOT NULL,
  `seccion` varchar(255) NOT NULL,
  PRIMARY KEY (`id_alumno`),
  UNIQUE KEY `UK8niai0o699f9bterqc7obpwyp` (`id_usuario`),
  UNIQUE KEY `UKtgfx9d7amj17rj56egq208pgl` (`codigo_institucional`),
  CONSTRAINT `FK3j6mgfljwrkb96hnya4lc1fhd` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alumno`
--

LOCK TABLES `alumno` WRITE;
/*!40000 ALTER TABLE `alumno` DISABLE KEYS */;
INSERT INTO `alumno` VALUES (_binary '','2011-06-09',1,5,'Rivas Sanchez','AL1222','Mario James','A');
/*!40000 ALTER TABLE `alumno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencia` (
  `fecha` date NOT NULL,
  `id_alumno` int NOT NULL,
  `id_asistencia` int NOT NULL AUTO_INCREMENT,
  `id_curso` int NOT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  `estado` enum('ASISTIO','FALTA','FALTA_JUSTIFICADA','TARDANZA') NOT NULL,
  PRIMARY KEY (`id_asistencia`),
  KEY `FKhoistiucdphdkhv4gcsan5o0d` (`id_alumno`),
  KEY `FKiboik92fgov8skdrwk760eqee` (`id_curso`),
  CONSTRAINT `FKhoistiucdphdkhv4gcsan5o0d` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`),
  CONSTRAINT `FKiboik92fgov8skdrwk760eqee` FOREIGN KEY (`id_curso`) REFERENCES `curso` (`id_curso`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
INSERT INTO `asistencia` VALUES ('2026-06-09',1,1,5,NULL,'TARDANZA'),('2026-06-07',1,2,5,NULL,'ASISTIO'),('2026-06-01',1,3,5,NULL,'FALTA');
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `curso`
--

DROP TABLE IF EXISTS `curso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `curso` (
  `estado` bit(1) NOT NULL,
  `id_curso` int NOT NULL AUTO_INCREMENT,
  `id_docente` int DEFAULT NULL,
  `descripcion` text,
  `nombre_curso` varchar(255) NOT NULL,
  `seccion` varchar(255) NOT NULL,
  PRIMARY KEY (`id_curso`),
  KEY `FKlou4yvig92uae5rmlqf7i9fq8` (`id_docente`),
  CONSTRAINT `FKlou4yvig92uae5rmlqf7i9fq8` FOREIGN KEY (`id_docente`) REFERENCES `docente` (`id_docente`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `curso`
--

LOCK TABLES `curso` WRITE;
/*!40000 ALTER TABLE `curso` DISABLE KEYS */;
INSERT INTO `curso` VALUES (_binary '',1,2,'Álgebra, Geometría, Aritmética y Trigonometría','Matemática','A'),(_binary '',2,NULL,'Literatura, Gramática y Razonamiento Verbal','Comunicación','A'),(_binary '',3,NULL,'Idioma extranjero','Inglés','A'),(_binary '',4,NULL,'Expresión artística, danza y artes visuales','Arte y Cultura','A'),(_binary '',5,1,'Historia del Perú y del Mundo, Geografía y Economía','Ciencias Sociales','A'),(_binary '',6,NULL,'Ciudadanía, Cívica y Psicología','Desarrollo Personal (DPCC)','A'),(_binary '',7,NULL,'Deportes, psicomotricidad y vida saludable','Educación Física','A'),(_binary '',8,NULL,'Formación en valores y religión','Educación Religiosa','A'),(_binary '',9,3,'Física, Química y Biología','Ciencia y Tecnología','A'),(_binary '',10,NULL,'Emprendimiento y gestión de proyectos','Educación para el Trabajo (EPT)','A'),(_binary '',11,NULL,'Acompañamiento socioafectivo y cognitivo','Tutoría y Orientación','A'),(_binary '',12,NULL,'Herramientas TIC y ofimática','Computación e Informática','A'),(_binary '',13,NULL,'Álgebra, Geometría, Aritmética y Trigonometría','Matemática','B'),(_binary '',14,NULL,'Literatura, Gramática y Razonamiento Verbal','Comunicación','B'),(_binary '',15,NULL,'Idioma extranjero','Inglés','B'),(_binary '',16,NULL,'Expresión artística, danza y artes visuales','Arte y Cultura','B'),(_binary '',17,NULL,'Historia del Perú y del Mundo, Geografía y Economía','Ciencias Sociales','B'),(_binary '',18,NULL,'Ciudadanía, Cívica y Psicología','Desarrollo Personal (DPCC)','B'),(_binary '',19,NULL,'Deportes, psicomotricidad y vida saludable','Educación Física','B'),(_binary '',20,NULL,'Formación en valores y religión','Educación Religiosa','B'),(_binary '',21,NULL,'Física, Química y Biología','Ciencia y Tecnología','B'),(_binary '',22,NULL,'Emprendimiento y gestión de proyectos','Educación para el Trabajo (EPT)','B'),(_binary '',23,NULL,'Acompañamiento socioafectivo y cognitivo','Tutoría y Orientación','B'),(_binary '',24,2,'Herramientas TIC y ofimática','Computación e Informática','B'),(_binary '',25,NULL,'Álgebra, Geometría, Aritmética y Trigonometría','Matemática','C'),(_binary '',26,NULL,'Literatura, Gramática y Razonamiento Verbal','Comunicación','C'),(_binary '',27,NULL,'Idioma extranjero','Inglés','C'),(_binary '',28,NULL,'Expresión artística, danza y artes visuales','Arte y Cultura','C'),(_binary '',29,NULL,'Historia del Perú y del Mundo, Geografía y Economía','Ciencias Sociales','C'),(_binary '',30,1,'Ciudadanía, Cívica y Psicología','Desarrollo Personal (DPCC)','C'),(_binary '',31,NULL,'Deportes, psicomotricidad y vida saludable','Educación Física','C'),(_binary '',32,NULL,'Formación en valores y religión','Educación Religiosa','C'),(_binary '',33,NULL,'Física, Química y Biología','Ciencia y Tecnología','C'),(_binary '',34,3,'Emprendimiento y gestión de proyectos','Educación para el Trabajo (EPT)','C'),(_binary '',35,NULL,'Acompañamiento socioafectivo y cognitivo','Tutoría y Orientación','C'),(_binary '',36,NULL,'Herramientas TIC y ofimática','Computación e Informática','C');
/*!40000 ALTER TABLE `curso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `docente`
--

DROP TABLE IF EXISTS `docente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `docente` (
  `estado` bit(1) NOT NULL,
  `id_docente` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `codigo_institucional` varchar(255) DEFAULT NULL,
  `especialidad` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_docente`),
  UNIQUE KEY `UKg59bvniliwyev7wy00glxe17a` (`id_usuario`),
  UNIQUE KEY `UKrc3jw3kr1j23xwa7ylg397smd` (`codigo_institucional`),
  CONSTRAINT `FK86smr8ieg6n2thvd52c336w9n` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `docente`
--

LOCK TABLES `docente` WRITE;
/*!40000 ALTER TABLE `docente` DISABLE KEYS */;
INSERT INTO `docente` VALUES (_binary '\0',1,2,'DOC-12345678',NULL),(_binary '\0',2,3,'DOC-37821234',NULL),(_binary '\0',3,4,'DOC-123555',NULL);
/*!40000 ALTER TABLE `docente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluacion`
--

DROP TABLE IF EXISTS `evaluacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluacion` (
  `estado` bit(1) NOT NULL,
  `fecha` date NOT NULL,
  `id_curso` int NOT NULL,
  `id_evaluacion` int NOT NULL AUTO_INCREMENT,
  `peso_porcentual` decimal(5,2) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `tipo` enum('EXAMEN','PARTICIPACION','PRACTICA','TAREA') NOT NULL,
  PRIMARY KEY (`id_evaluacion`),
  KEY `FKk4l3846ghken3y7kc09dses65` (`id_curso`),
  CONSTRAINT `FKk4l3846ghken3y7kc09dses65` FOREIGN KEY (`id_curso`) REFERENCES `curso` (`id_curso`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluacion`
--

LOCK TABLES `evaluacion` WRITE;
/*!40000 ALTER TABLE `evaluacion` DISABLE KEYS */;
INSERT INTO `evaluacion` VALUES (_binary '','2026-06-07',5,1,15.00,'PC1','PRACTICA');
/*!40000 ALTER TABLE `evaluacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matricula`
--

DROP TABLE IF EXISTS `matricula`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matricula` (
  `fecha_matricula` date NOT NULL,
  `id_alumno` int NOT NULL,
  `id_curso` int NOT NULL,
  `id_matricula` int NOT NULL AUTO_INCREMENT,
  `estado` varchar(255) NOT NULL,
  PRIMARY KEY (`id_matricula`),
  KEY `FK7hs7wugi5i1l9wqnu9fkm7oav` (`id_alumno`),
  KEY `FK3kmpjws06uktvh1va9scigafr` (`id_curso`),
  CONSTRAINT `FK3kmpjws06uktvh1va9scigafr` FOREIGN KEY (`id_curso`) REFERENCES `curso` (`id_curso`),
  CONSTRAINT `FK7hs7wugi5i1l9wqnu9fkm7oav` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matricula`
--

LOCK TABLES `matricula` WRITE;
/*!40000 ALTER TABLE `matricula` DISABLE KEYS */;
/*!40000 ALTER TABLE `matricula` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nota`
--

DROP TABLE IF EXISTS `nota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nota` (
  `fecha_registro` date NOT NULL,
  `id_alumno` int NOT NULL,
  `id_evaluacion` int NOT NULL,
  `id_nota` int NOT NULL AUTO_INCREMENT,
  `valor` decimal(4,2) NOT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_nota`),
  KEY `FKer89uet8f033o2npm6j7e46xh` (`id_alumno`),
  KEY `FKtiqarr5h1nns2pyj428uvr669` (`id_evaluacion`),
  CONSTRAINT `FKer89uet8f033o2npm6j7e46xh` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`),
  CONSTRAINT `FKtiqarr5h1nns2pyj428uvr669` FOREIGN KEY (`id_evaluacion`) REFERENCES `evaluacion` (`id_evaluacion`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nota`
--

LOCK TABLES `nota` WRITE;
/*!40000 ALTER TABLE `nota` DISABLE KEYS */;
INSERT INTO `nota` VALUES ('2026-06-09',1,1,1,12.00,NULL);
/*!40000 ALTER TABLE `nota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `estado` bit(1) NOT NULL,
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `apellido` varchar(255) NOT NULL,
  `contraseña` varchar(255) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `rol` enum('ADMIN','ALUMNO','DOCENTE') NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UK2mlfr087gb1ce55f2j87o74t` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (_binary '',1,'Principal','$2a$10$0YrJl6FntLL6bjOA35HVG.Ge8WSE7HiID5.vWfCtu0q/tUki.WYei','admin@jcm.edu.pe','Admin','ADMIN'),(_binary '',2,'Gamarra Lopez','$2a$10$ZFHg1ItUG3AO4R/T.PbyDuJrMrZDgDPi8b2uIWxBv113d50tPcgfe','mgamarral@jcm.edu.pe','Maria Gracia','DOCENTE'),(_binary '',3,'Espinoza Cueva','$2a$10$ad1GGHtgdgjFnO5wcc8.UOayGooC3352YHustDay1/gu4YpqCbkJm','respinozac@jcm.edu.pe','Rosa Maria','DOCENTE'),(_binary '',4,'Maartinz Lopez','$2a$10$oKURDoRrJ0xyHkUsraPyo.X1RXLJcaLqriOedislKyOjmszFvrHQO','jmartinezl@jcm.edu.pe','Jose Luis','DOCENTE'),(_binary '',5,'Rivas Sanchez','$2a$10$9/Mmbf0zQkXSyP.SBTMI1u.V3csZxUIt.41Skxvc3jJiGzYZNQFhW','mrivass@jcm.edu.pe','Mario James','ALUMNO');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-10  8:51:39
