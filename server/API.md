# VALHALLA RAGE Server API

Base locale de test :

```text
http://192.168.1.15:8787
```

Toutes les routes proteges utilisent :

```text
Authorization: Bearer <token>
```

## Sante

`GET /health`

Retourne l'etat du serveur, le modele IA configure et les URLs locales.

## Authentification

`POST /auth/register`

```json
{
  "username": "Thomas",
  "password": "secret123"
}
```

`POST /auth/login`

```json
{
  "username": "Thomas",
  "password": "secret123"
}
```

`POST /auth/forgot-password`

Prepare une recuperation de mot de passe. En mode local de test, le serveur renvoie un code de developpement.

`POST /auth/reset-password`

```json
{
  "username": "Thomas",
  "code": "123456",
  "newPassword": "nouveauSecret123"
}
```

## Compte

`GET /me`

Retourne le profil public du compte connecte.

`PUT /me/profile`

Synchronise le profil sportif.

`DELETE /me`

Supprime le compte et les donnees serveur associees.

`GET /me/export`

Exporte les donnees serveur rattachees au compte : profil, historique, amis, groupes, conversations et defis.

## Coach IA

`POST /coach`

Accepte :

- `message`
- `profile`
- `currentPlan`
- `planAdjustment`

Retourne :

- `reply`
- `action`
- `program` si le serveur propose un tableau.

## Programme

`POST /program/generate`

Genere un programme personnalise selon le profil et les performances.

`GET /program/current`

Retourne le programme officiel de la semaine.

`POST /program/accept`

Enregistre un programme comme programme officiel.

## Seances

`POST /sessions/complete`

Synchronise une seance terminee, partielle ou non faite.

`GET /history`

Retourne l'historique des seances.

## Poids

`GET /weight`

Retourne les poids enregistres par jour et l'objectif de poids.

`POST /weight`

```json
{
  "date": "2026-10-05",
  "weightKg": 74.5,
  "targetWeight": "70"
}
```

Enregistre ou remplace le poids du jour, puis met a jour le profil serveur.

## Battle

`GET /battle/arena`

Retourne l'arene selon le niveau.

`GET /battle/community-challenges`

Retourne les defis communautaires adaptes au niveau.

`POST /battle/challenges`

Cree un defi prive ou de groupe.

`POST /battle/challenges/:id/proof`

Ajoute une preuve video privee.

`POST /battle/challenges/:id/claim`

Recupere la recompense si la preuve existe.

## Social

`GET /friends`

Liste les amis.

`POST /friends`

Ajoute un ami par nom d'utilisateur.

`DELETE /friends/:id`

Supprime un ami.

`GET /groups`

Liste les groupes.

`POST /groups`

Cree un groupe.

`GET /conversations/:id/messages`

Liste les messages.

`POST /conversations/:id/messages`

Envoie un message.

## Boutique

`GET /shop/items`

Liste les objets compatibles avec le heros actuel.

`GET /shop/inventory`

Retourne l'inventaire.

`POST /shop/purchase`

Achete un objet avec les pieces du Valhalla.

`POST /shop/equip`

Equipe ou retire un objet.

`PUT /shop/state`

Synchronise l'etat complet de boutique depuis l'application : pieces, inventaire, heros choisi, heros possedes et objets equipes.

`POST /coins/free-ad`

Ajoute les pieces gratuites limitees a deux fois par jour.

`POST /coins/purchase-intent`

Prepare un achat de pieces. La version publique devra finaliser via Google Play Billing.
