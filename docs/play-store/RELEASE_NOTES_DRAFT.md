# Notes de version - brouillon

## 0.47.0

- Recuperation automatique depuis le serveur de l'historique des seances et du suivi de poids apres connexion.

## 0.46.0

- Battle commence a se synchroniser avec le serveur : amis, groupes et conversations.
- Les groupes peuvent etre crees avec les noms d'amis et recuperent une conversation serveur.
- Les messages et defis envoyes dans un chat sont conserves localement et envoyes au serveur quand il est disponible.
- Les preuves video de defis restent privees dans le prototype serveur.
- Le serveur fusionne mieux les anciennes et nouvelles performances sportives pour doser les poids et repetitions.

## 0.45.0

- Affichage clair du mode coach : choix guidés locaux ou vraie IA serveur.
- Les paramètres indiquent si la clé IA serveur est active et quel modèle est utilisé.
- La zone libre du coach reste disponible, mais l'utilisateur voit si elle utilise l'IA réelle ou le mode local.

## 0.44.0

- Sauvegarde serveur du tableau personnel cree par l'utilisateur.
- Sauvegarde serveur du tableau accepte apres proposition du coach.
- Recuperation du programme officiel serveur apres connexion.

## 0.43.0

- Correction du visuel boutique `Chapeau d'archimage`.
- Coach IA avec choix guidés : conseil exercice, programme plus difficile, programme plus facile.
- Liste d'exercices du coach organisée avec les exercices maison avant les exercices de salle.
- Conseils exercices plus complets : muscles travaillés, posture, points de vigilance et astuces.
- Ajout d'un écran pour créer et modifier son propre tableau de sport.

## 0.42.0

- Synchronisation serveur de l'inventaire, des objets equipes, du heros choisi et des pieces.
- Ajout de la route serveur `PUT /shop/state`.
- La boutique conserve le mode local si le serveur n'est pas joignable.

## 0.41.0

- Changement de mot de passe synchronise avec le serveur quand le compte est connecte.
- Regle de mot de passe harmonisee a 6 caracteres minimum.

## 0.40.0

- Identifiant Android officiel aligne sur `com.valhallarage.app`.
- Preparation plus propre pour le futur build Play Store.

## 0.39.0

- Synchronisation serveur du suivi de poids journalier.
- Ajout des routes serveur `GET /weight` et `POST /weight`.
- Le poids actuel et l'objectif de poids sont conserves dans le profil serveur.

## 0.38.0

- Nettoyage des dernieres traces de l'ancien nom dans la configuration Android.
- Nom interne du projet aligne sur `VALHALLA RAGE`.
- APK V38 reconstruit et envoye au Pixel.

## 0.37.0

- Ajout de l'export des donnees serveur dans Parametre > Infos perso.
- Ajout de l'endpoint serveur `GET /me/export`.
- L'export inclut profil, seances, poids, amis, groupes, conversations et defis.

## 0.36.0

- Ajout de la suppression du compte serveur dans Parametre > Infos perso.
- Ajout de l'endpoint serveur `DELETE /me`.
- Nettoyage des donnees serveur liees au compte supprime.
- Scripts internes pour reconstruire l'APK et envoyer le dernier APK au Pixel.

## 0.35.0

- Connexion au serveur VALHALLA RAGE.
- Creation et migration de compte vers le serveur.
- Programme sportif synchronisable avec le serveur.
- Coach IA connecte au serveur local de test.
- Validation de seance synchronisee avec le serveur.
- Mot de passe oublie compatible serveur en mode test.
- Ajout d'amis synchronise avec le serveur quand le compte ami existe.
- Mode local conserve si le serveur est indisponible.

## A tester sur Pixel

- Installer `VALHALLA-RAGE-v45-clean.apk`.
- Profil > Parametre > Serveur IA > Tester la connexion serveur.
- Creer/ouvrir un compte.
- Demander au coach : "cree mon tableau de la semaine".
- Valider une seance.
- Enregistrer le poids du jour dans l'accueil.
- Ajouter un ami de test.
