-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : sam. 06 déc. 2025 à 16:32
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `cabinet_medical`
--

-- --------------------------------------------------------

--
-- Structure de la table `medecin`
--

CREATE TABLE `medecin` (
  `id_medecin` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `date_naissance` date DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `telephone` varchar(15) DEFAULT NULL,
  `specialite` varchar(100) NOT NULL,
  `licence` varchar(50) NOT NULL,
  `id_utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ordonnance`
--

CREATE TABLE `ordonnance` (
  `id_ordonnance` int(11) NOT NULL,
  `date_prescription` date NOT NULL,
  `contenu_textuel` text DEFAULT NULL,
  `id_patient` int(11) NOT NULL,
  `id_medecin` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `patient`
--

CREATE TABLE `patient` (
  `id_patient` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `date_naissance` date DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `telephone` varchar(15) DEFAULT NULL,
  `dossier_medical` varchar(255) DEFAULT NULL,
  `mutuelle` varchar(100) DEFAULT NULL,
  `id_utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `rendezvous`
--

CREATE TABLE `rendezvous` (
  `id_rdv` int(11) NOT NULL,
  `date_heure` datetime NOT NULL,
  `motif` varchar(255) DEFAULT NULL,
  `statut` enum('PLANIFIE','ANNULE','TERMINE') NOT NULL,
  `id_patient` int(11) NOT NULL,
  `id_medecin` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id_utilisateur` int(11) NOT NULL,
  `login` varchar(50) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `role` enum('ADMIN','MEDECIN','PATIENT') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id_utilisateur`, `login`, `mot_de_passe`, `role`) VALUES
(1, 'admin', 'adminpasswordhash', 'ADMIN');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `medecin`
--
ALTER TABLE `medecin`
  ADD PRIMARY KEY (`id_medecin`),
  ADD UNIQUE KEY `licence` (`licence`),
  ADD UNIQUE KEY `id_utilisateur` (`id_utilisateur`);

--
-- Index pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD PRIMARY KEY (`id_ordonnance`),
  ADD KEY `id_patient` (`id_patient`),
  ADD KEY `id_medecin` (`id_medecin`);

--
-- Index pour la table `patient`
--
ALTER TABLE `patient`
  ADD PRIMARY KEY (`id_patient`),
  ADD UNIQUE KEY `id_utilisateur` (`id_utilisateur`);

--
-- Index pour la table `rendezvous`
--
ALTER TABLE `rendezvous`
  ADD PRIMARY KEY (`id_rdv`),
  ADD KEY `id_patient` (`id_patient`),
  ADD KEY `id_medecin` (`id_medecin`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id_utilisateur`),
  ADD UNIQUE KEY `login` (`login`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `medecin`
--
ALTER TABLE `medecin`
  MODIFY `id_medecin` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  MODIFY `id_ordonnance` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `patient`
--
ALTER TABLE `patient`
  MODIFY `id_patient` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `rendezvous`
--
ALTER TABLE `rendezvous`
  MODIFY `id_rdv` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id_utilisateur` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `medecin`
--
ALTER TABLE `medecin`
  ADD CONSTRAINT `medecin_ibfk_1` FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateur` (`id_utilisateur`);

--
-- Contraintes pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD CONSTRAINT `ordonnance_ibfk_1` FOREIGN KEY (`id_patient`) REFERENCES `patient` (`id_patient`),
  ADD CONSTRAINT `ordonnance_ibfk_2` FOREIGN KEY (`id_medecin`) REFERENCES `medecin` (`id_medecin`);

--
-- Contraintes pour la table `patient`
--
ALTER TABLE `patient`
  ADD CONSTRAINT `patient_ibfk_1` FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateur` (`id_utilisateur`);

--
-- Contraintes pour la table `rendezvous`
--
ALTER TABLE `rendezvous`
  ADD CONSTRAINT `rendezvous_ibfk_1` FOREIGN KEY (`id_patient`) REFERENCES `patient` (`id_patient`),
  ADD CONSTRAINT `rendezvous_ibfk_2` FOREIGN KEY (`id_medecin`) REFERENCES `medecin` (`id_medecin`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
