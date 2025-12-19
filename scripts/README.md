# Scripts d'optimisation

## Minification des fichiers frontend

Pour minimiser la taille des fichiers CSS, HTML et TypeScript afin que Java soit le langage dominant sur GitHub :

```bash
node scripts/minify-frontend.js
```

⚠️ **Attention** : Ce script modifie les fichiers source. Assurez-vous d'avoir commité vos changements avant de l'exécuter.

Pour annuler les modifications :
```bash
git checkout frontend/src/
```

## Alternative : Utilisation de .gitattributes

Le fichier `.gitattributes` est déjà configuré pour exclure les fichiers frontend du comptage linguistique GitHub. Cela permet à Java d'être automatiquement le langage dominant sans modifier les fichiers source.
