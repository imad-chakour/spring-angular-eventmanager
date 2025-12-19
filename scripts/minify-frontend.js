/**
 * Script pour minifier les fichiers CSS, HTML et TypeScript du frontend
 * Cela réduit leur taille et leur impact sur les statistiques GitHub
 */

const fs = require('fs');
const path = require('path');

const FRONTEND_SRC = path.join(__dirname, '../frontend/src');

// Fonction pour minifier CSS
function minifyCSS(content) {
  return content
    .replace(/\/\*[\s\S]*?\*\//g, '') // Supprimer les commentaires
    .replace(/\s+/g, ' ') // Remplacer les espaces multiples par un seul
    .replace(/;\s*}/g, '}') // Supprimer les points-virgules avant }
    .replace(/\s*{\s*/g, '{') // Supprimer les espaces autour de {
    .replace(/}\s*/g, '}') // Supprimer les espaces après }
    .replace(/:\s*/g, ':') // Supprimer les espaces après :
    .replace(/;\s*/g, ';') // Supprimer les espaces après ;
    .replace(/,\s*/g, ',') // Supprimer les espaces après ,
    .trim();
}

// Fonction pour minifier HTML
function minifyHTML(content) {
  return content
    .replace(/<!--[\s\S]*?-->/g, '') // Supprimer les commentaires HTML
    .replace(/\s+/g, ' ') // Remplacer les espaces multiples
    .replace(/>\s+</g, '><') // Supprimer les espaces entre balises
    .replace(/\s+>/g, '>') // Supprimer les espaces avant >
    .replace(/<\s+/g, '<') // Supprimer les espaces après <
    .trim();
}

// Fonction pour compacter TypeScript (supprime les commentaires et espaces inutiles)
function compactTS(content) {
  // Garder les imports et exports sur une ligne
  let result = content
    .replace(/\/\*[\s\S]*?\*\//g, '') // Supprimer les commentaires multi-lignes
    .replace(/\/\/.*$/gm, '') // Supprimer les commentaires de ligne
    .replace(/\n\s*\n\s*\n/g, '\n') // Supprimer les lignes vides multiples
    .replace(/^\s+/gm, '') // Supprimer l'indentation
    .replace(/\s+$/gm, '') // Supprimer les espaces en fin de ligne
    .replace(/\s*{\s*/g, '{') // Compacter les accolades
    .replace(/\s*}\s*/g, '}')
    .replace(/\s*\(\s*/g, '(')
    .replace(/\s*\)\s*/g, ')')
    .replace(/\s*=\s*/g, '=')
    .replace(/\s*:\s*/g, ':')
    .replace(/\s*,\s*/g, ',')
    .replace(/\s*;\s*/g, ';')
    .replace(/\s+/g, ' '); // Remplacer les espaces multiples par un seul
  
  // Remettre les imports sur des lignes séparées pour la lisibilité
  result = result.replace(/(import\s+[^;]+;)/g, '$1\n');
  result = result.replace(/(export\s+[^;]+;)/g, '$1\n');
  
  return result.trim();
}

// Fonction récursive pour parcourir les fichiers
function processDirectory(dir, extensions, processor) {
  const files = fs.readdirSync(dir);
  
  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);
    
    if (stat.isDirectory()) {
      // Ignorer node_modules, dist, .angular
      if (!['node_modules', 'dist', '.angular', '.cache', '.vite'].includes(file)) {
        processDirectory(filePath, extensions, processor);
      }
    } else if (extensions.some(ext => file.endsWith(ext))) {
      try {
        const content = fs.readFileSync(filePath, 'utf8');
        const minified = processor(content);
        
        // Écrire seulement si la taille a été réduite
        if (minified.length < content.length) {
          fs.writeFileSync(filePath, minified, 'utf8');
          const reduction = ((1 - minified.length / content.length) * 100).toFixed(1);
          console.log(`✓ ${filePath.replace(FRONTEND_SRC, '')} - Réduit de ${reduction}%`);
        }
      } catch (error) {
        console.error(`✗ Erreur sur ${filePath}:`, error.message);
      }
    }
  });
}

// Exécution
console.log('🚀 Minification des fichiers frontend...\n');

// Minifier CSS
console.log('📝 Minification CSS...');
processDirectory(FRONTEND_SRC, ['.css'], minifyCSS);

// Minifier HTML
console.log('\n📝 Minification HTML...');
processDirectory(FRONTEND_SRC, ['.html'], minifyHTML);

// Compacter TypeScript (plus conservateur)
console.log('\n📝 Compactage TypeScript...');
processDirectory(FRONTEND_SRC, ['.ts'], compactTS);

console.log('\n✅ Minification terminée!');
console.log('\n⚠️  Note: Les fichiers ont été minifiés. Pour revenir en arrière, utilisez git checkout.');
