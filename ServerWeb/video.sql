-- phpMyAdmin SQL Dump
-- version 3.4.10.1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le : Sam 02 Juillet 2016 à 17:31
-- Version du serveur: 5.5.20
-- Version de PHP: 5.3.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `video`
--
CREATE DATABASE `video` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `video`;

-- --------------------------------------------------------

--
-- Structure de la table `movies`
--

CREATE TABLE IF NOT EXISTS `movies` (
  `teacherID` int(2) NOT NULL,
  `title` varchar(30) NOT NULL,
  PRIMARY KEY (`teacherID`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `typegroup`
--

CREATE TABLE IF NOT EXISTS `typegroup` (
  `groupID` int(2) NOT NULL AUTO_INCREMENT,
  `studentID` int(2) NOT NULL,
  `teacherID` int(2) NOT NULL,
  PRIMARY KEY (`groupID`),
  KEY `studentID` (`studentID`,`teacherID`),
  KEY `teacherID` (`teacherID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Contenu de la table `typegroup`
--

INSERT INTO `typegroup` (`groupID`, `studentID`, `teacherID`) VALUES
(1, 4, 1),
(2, 4, 2),
(3, 5, 1),
(4, 6, 1);

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `userID` int(2) NOT NULL AUTO_INCREMENT COMMENT 'Identifiant de l''utilisateur',
  `login` varchar(15) NOT NULL,
  `password` varchar(10) NOT NULL,
  `type` enum('teacher','student') NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Contenu de la table `user`
--

INSERT INTO `user` (`userID`, `login`, `password`, `type`) VALUES
(1, 'teacher1', 'password', 'teacher'),
(2, 'teacher2', 'password', 'teacher'),
(3, 'teacher3', 'password', 'teacher'),
(4, 'student1', 'password', 'student'),
(5, 'student2', 'password', 'student'),
(6, 'student3', 'password', 'student');

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `movies`
--
ALTER TABLE `movies`
  ADD CONSTRAINT `movies_ibfk_1` FOREIGN KEY (`teacherID`) REFERENCES `user` (`userID`);

--
-- Contraintes pour la table `typegroup`
--
ALTER TABLE `typegroup`
  ADD CONSTRAINT `typegroup_ibfk_1` FOREIGN KEY (`studentID`) REFERENCES `user` (`userID`),
  ADD CONSTRAINT `typegroup_ibfk_2` FOREIGN KEY (`teacherID`) REFERENCES `user` (`userID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
