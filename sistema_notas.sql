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
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=20741 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encuesta_docente`
--

DROP TABLE IF EXISTS `encuesta_docente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `encuesta_docente` (
  `id_encuesta` int NOT NULL AUTO_INCREMENT,
  `calificacion` int NOT NULL,
  `fecha_respuesta` date NOT NULL,
  `id_docente` int NOT NULL,
  PRIMARY KEY (`id_encuesta`),
  KEY `FKacjetfa8diq8j82o96032db5d` (`id_docente`),
  CONSTRAINT `FKacjetfa8diq8j82o96032db5d` FOREIGN KEY (`id_docente`) REFERENCES `docente` (`id_docente`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `corregida` bit(1) NOT NULL,
  PRIMARY KEY (`id_nota`),
  KEY `FKer89uet8f033o2npm6j7e46xh` (`id_alumno`),
  KEY `FKtiqarr5h1nns2pyj428uvr669` (`id_evaluacion`),
  CONSTRAINT `FKer89uet8f033o2npm6j7e46xh` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`),
  CONSTRAINT `FKtiqarr5h1nns2pyj428uvr669` FOREIGN KEY (`id_evaluacion`) REFERENCES `evaluacion` (`id_evaluacion`)
) ENGINE=InnoDB AUTO_INCREMENT=4097 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-15  8:28:27
