# Heberger le serveur VALHALLA RAGE sur Render

Objectif : obtenir une adresse HTTPS publique pour le serveur, afin que l'application Android puisse l'utiliser sans depender du PC.

## Ce qui est deja pret

- `server/server.mjs` demarre sur `PORT` quand l'hebergeur le fournit.
- `server/Dockerfile` permet de lancer le serveur dans une image Docker.
- `render.yaml` peut creer automatiquement le service Render.
- `/health` permet a l'hebergeur de verifier que le serveur est vivant.

## Etapes cote Render

1. Creer ou ouvrir un compte Render.
2. Mettre le projet dans un depot GitHub.
   - Creer un depot GitHub vide.
   - Lancer `tools/PUSH-TO-GITHUB.cmd`.
   - Coller l'adresse HTTPS du depot quand le script la demande.
3. Dans Render, choisir `New` puis `Blueprint`.
4. Connecter le depot qui contient `render.yaml`.
5. Lancer la creation du service `valhalla-rage-server`.
6. Attendre que Render affiche `Live`.
7. Copier l'adresse HTTPS fournie, par exemple :

```text
https://valhalla-rage-server.onrender.com
```

## Variables importantes

Render creera automatiquement `VALHALLA_SERVER_SECRET`.
La cle OpenAI n'est pas necessaire pour l'instant.

```text
NODE_ENV=production
VALHALLA_DATA_DIR=/app/data
VALHALLA_PROOF_RETENTION_HOURS=24
VALHALLA_SERVER_SECRET=<secret genere>
OPENAI_MODEL=gpt-5
OPENAI_MODERATION_MODEL=omni-moderation-latest
```

Plus tard, si on reactive la vraie IA, il faudra ajouter `OPENAI_API_KEY` manuellement dans les variables d'environnement du serveur.

## Brancher l'application Android

Dans VALHALLA RAGE :

```text
Profil -> Parametre -> Serveur IA
```

Coller l'adresse HTTPS Render puis enregistrer.

## A ne pas oublier avant Play Store

- Ajouter une vraie base de donnees hebergee.
- Garder les videos de defis privees.
- Supprimer les medias de verification apres controle.
- Ajouter Google Play Billing pour les achats de pieces.
- Publier une politique de confidentialite en ligne.

