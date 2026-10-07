# VALHALLA RAGE - Plan de test Pixel

APK actuel a tester :

```text
outputs/VALHALLA-RAGE-v47-clean.apk
```

## Installation

1. Sur le Pixel, ouvrir l'application Fichiers.
2. Aller dans Telechargements.
3. Installer `VALHALLA-RAGE-v47-clean.apk`.
4. Si Android propose une mise a jour, accepter.

## Tests prioritaires

1. Creer un compte avec un mot de passe de 6 caracteres minimum.
2. Aller dans Profil > Parametre > Serveur IA > Tester la connexion serveur.
3. Faire le questionnaire de depart.
4. Dans Accueil, enregistrer le poids du jour.
5. Dans Coach IA, demander : `cree mon tableau de la semaine`.
6. Verifier que le tableau apparait dans Plan avec jours, exercices, series, repetitions, poids et repos.
7. Valider la seance du jour.
8. Changer le mot de passe dans Profil > Parametre > Infos perso.
9. Fermer puis rouvrir l'application, puis tester la connexion.
10. Tester l'export des donnees serveur.
11. Acheter ou equiper un item gratuit/possible dans la boutique, puis rouvrir l'application.
12. Dans Coach IA, tester `Conseil exercice` avec un exercice maison puis un exercice de salle.
13. Dans Plan, créer un tableau personnel, l'enregistrer, puis vérifier l'accueil et la validation de séance.
14. Se reconnecter au compte et vérifier que le tableau officiel peut être récupéré depuis le serveur.
15. Dans Paramètre > Serveur IA, vérifier l'indication `IA réelle : active/non activée`.
16. Dans Battle, ajouter un ami serveur existant, ouvrir le chat, envoyer un message, créer un groupe et envoyer un défi.
17. Se reconnecter et verifier que l'historique des seances et le suivi du poids reviennent depuis le serveur.

## Points a observer

- Le nom visible doit etre VALHALLA RAGE.
- L'application ne doit pas repasser en haut de page apres un achat impossible.
- Le serveur doit indiquer `Connecte` dans les parametres.
- Le coach doit utiliser le tableau officiel quand il propose une modification.
- Le suivi de poids doit garder la valeur enregistree.

## A ne pas faire sans vouloir vraiment tester

- Ne pas cliquer sur `Supprimer mon compte serveur` sauf si le but est de verifier la suppression, car cela efface les donnees serveur du compte.
