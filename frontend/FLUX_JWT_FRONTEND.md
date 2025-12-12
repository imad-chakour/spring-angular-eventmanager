# 🔐 Flux JWT dans le Frontend Angular

## 📋 Vue d'Ensemble

Le frontend Angular gère le JWT token de manière **automatique** grâce à :
1. **AuthService** : Gestion du token (stockage, récupération)
2. **AuthInterceptor** : Ajout automatique du token aux requêtes HTTP
3. **AuthGuard** : Protection des routes
4. **Signals** : Réactivité pour l'état d'authentification

## 🔄 Flux Complet Frontend → Backend

### Étape 1 : Login - Obtention du Token

```
┌─────────────────┐
│ LoginComponent │
│ (Formulaire)   │
└────────┬────────┘
         │
         │ 1. Soumission du formulaire
         │    { email, password }
         ▼
┌─────────────────┐
│ AuthService     │
│ .login()        │
└────────┬────────┘
         │
         │ 2. POST /api/users/login
         │    (SANS token - endpoint public)
         ▼
┌─────────────────┐
│ Backend         │
│ AuthController  │
└────────┬────────┘
         │
         │ 3. Retourne { token, email }
         ▼
┌─────────────────┐
│ AuthService     │
│ .login()        │
│                 │ ✅ setToken(token)
│                 │ ✅ Stocke dans localStorage
│                 │ ✅ isAuthenticated = true
│                 │ ✅ Charge les infos utilisateur
└─────────────────┘
```

#### Code dans `auth.service.ts` :

```typescript
login(credentials: LoginRequest): Observable<LoginResponse> {
  return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
    tap((response) => {
      // 1. Sauvegarder le token
      this.setToken(response.token);
      
      // 2. Mettre à jour l'état d'authentification
      this.isAuthenticated.set(true);
      
      // 3. Charger les informations utilisateur
      this.loadUserInfo(response.email);
      
      // 4. Rediriger vers la page principale
      this.router.navigate(['/campaigns']);
    })
  );
}

private setToken(token: string): void {
  localStorage.setItem('auth_token', token);
}
```

---

### Étape 2 : Stockage du Token

Le token est stocké dans le **localStorage** du navigateur :

```typescript
// Clé utilisée
private readonly tokenKey = 'auth_token';

// Stockage
localStorage.setItem('auth_token', 'eyJhbGciOiJIUzI1NiJ9...');

// Récupération
getToken(): string | null {
  return localStorage.getItem('auth_token');
}
```

**Avantages :**
- ✅ Persiste même après fermeture du navigateur
- ✅ Accessible dans toute l'application
- ✅ Survit aux rechargements de page

---

### Étape 3 : Ajout Automatique du Token aux Requêtes

L'**AuthInterceptor** ajoute automatiquement le token à **toutes** les requêtes HTTP :

```
┌─────────────────┐
│ Component       │
│ (Ex: UserList)  │
└────────┬────────┘
         │
         │ 1. this.http.get('/api/users')
         ▼
┌─────────────────┐
│ AuthInterceptor │
│ (Intercepte)    │
└────────┬────────┘
         │
         │ 2. Récupère le token
         │    const token = auth.getToken();
         │
         │ 3. Clone la requête
         │    + Ajoute: Authorization: Bearer <token>
         ▼
┌─────────────────┐
│ HTTP Request    │
│ GET /api/users  │
│ Authorization:  │
│ Bearer eyJ...   │
└─────────────────┘
```

#### Code dans `auth.interceptor.ts` :

```typescript
export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  // Si un token existe, l'ajouter au header
  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(cloned);
  }

  // Sinon, laisser passer la requête sans modification
  return next(req);
};
```

#### Configuration dans `app.config.ts` :

```typescript
provideHttpClient(
  withFetch(),
  withInterceptors([authInterceptor])  // ✅ Interceptor enregistré
)
```

**Résultat :** Toutes les requêtes HTTP incluent automatiquement le header `Authorization: Bearer <token>` !

---

### Étape 4 : Protection des Routes

Les **Guards** protègent les routes en vérifiant l'authentification :

```
┌─────────────────┐
│ Utilisateur     │
│ Accède à        │
│ /users          │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ authGuard       │
│ (Route Guard)   │
└────────┬────────┘
         │
         │ Vérifie: auth.isAuthenticated()
         │
    ┌────┴────┐
    │        │
   OUI      NON
    │        │
    ▼        ▼
┌────────┐ ┌──────────────┐
│ Autorise│ │ Redirige vers│
│ Route   │ │ /login       │
└────────┘ └──────────────┘
```

#### Code dans `auth.guard.ts` :

```typescript
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Vérifier si l'utilisateur est authentifié
  if (auth.isAuthenticated()) {
    return true;  // ✅ Autoriser l'accès
  }

  // ❌ Rediriger vers la page de login
  router.navigate(['/login']);
  return false;
};

// Guard spécifique pour les admins
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && auth.isAdmin()) {
    return true;  // ✅ Admin authentifié
  }

  router.navigate(['/campaigns']);
  return false;
};
```

#### Configuration dans `app.routes.ts` :

```typescript
export const routes: Routes = [
  // Routes publiques (pas de guard)
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Routes protégées (avec guard)
  { 
    path: 'users', 
    component: UserListComponent,
    canActivate: [authGuard]  // ✅ Protection
  },
  
  // Routes admin (guard spécifique)
  { 
    path: 'users/new', 
    component: UserFormComponent,
    canActivate: [adminGuard]  // ✅ Protection admin
  }
];
```

---

### Étape 5 : Gestion de l'État d'Authentification

L'état d'authentification est géré avec des **Signals** Angular (réactivité) :

```typescript
// Dans AuthService
readonly isAuthenticated = signal<boolean>(!!this.getToken());
readonly currentUser = signal<User | null>(this.getCurrentUser());

// Vérification au démarrage
constructor() {
  // Si un token existe dans localStorage, l'utilisateur est authentifié
  this.isAuthenticated.set(!!this.getToken());
}
```

**Utilisation dans les composants :**

```typescript
// Dans un composant
authService = inject(AuthService);

// Vérifier l'état
if (this.authService.isAuthenticated()) {
  // Utilisateur connecté
}

// Accéder aux infos utilisateur
const user = this.authService.currentUser();
if (user?.role === 'ADMIN') {
  // Afficher fonctionnalités admin
}
```

---

### Étape 6 : Logout - Suppression du Token

```
┌─────────────────┐
│ Utilisateur     │
│ Clique "Logout" │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ AuthService     │
│ .logout()       │
└────────┬────────┘
         │
         │ 1. Supprime le token
         │    localStorage.removeItem('auth_token')
         │
         │ 2. Supprime les infos utilisateur
         │    localStorage.removeItem('current_user')
         │
         │ 3. Met à jour l'état
         │    isAuthenticated = false
         │    currentUser = null
         │
         │ 4. Redirige vers /login
         ▼
┌─────────────────┐
│ Login Page      │
└─────────────────┘
```

#### Code dans `auth.service.ts` :

```typescript
logout(): void {
  // Supprimer le token et les infos utilisateur
  localStorage.removeItem('auth_token');
  localStorage.removeItem('current_user');
  
  // Mettre à jour l'état
  this.isAuthenticated.set(false);
  this.currentUser.set(null);
  
  // Rediriger vers la page de login
  this.router.navigate(['/login']);
}
```

---

## 📊 Schéma Complet du Flux

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND ANGULAR                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. LOGIN                                                   │
│  ┌─────────────┐                                           │
│  │ LoginForm   │ → AuthService.login()                     │
│  └─────────────┘     ↓                                      │
│                      POST /api/users/login                  │
│                      (SANS token)                           │
│                                                             │
│  2. RÉCEPTION DU TOKEN                                      │
│  ┌─────────────┐                                           │
│  │ AuthService │ ← { token, email }                        │
│  └─────────────┘     ↓                                      │
│                      localStorage.setItem('auth_token', ...) │
│                      isAuthenticated = true                  │
│                                                             │
│  3. REQUÊTES PROTÉGÉES                                      │
│  ┌─────────────┐                                           │
│  │ Component   │ → this.http.get('/api/users')             │
│  └─────────────┘     ↓                                      │
│  ┌─────────────┐                                           │
│  │ Interceptor │ → Récupère token                          │
│  └─────────────┘     ↓                                      │
│                      Clone requête                          │
│                      + Authorization: Bearer <token>        │
│                                                             │
│  4. PROTECTION DES ROUTES                                  │
│  ┌─────────────┐                                           │
│  │ Route Guard │ → Vérifie isAuthenticated()               │
│  └─────────────┘     ↓                                      │
│                      Autorise ou redirige                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                            ↓
                    ┌───────────────┐
                    │   BACKEND     │
                    │   Gateway     │
                    │   + Services │
                    └───────────────┘
```

## 🔑 Points Clés

### 1. Stockage du Token
- ✅ **localStorage** : Persiste après fermeture du navigateur
- ✅ Clé : `'auth_token'`
- ✅ Récupération automatique au démarrage de l'app

### 2. Ajout Automatique du Token
- ✅ **AuthInterceptor** : Ajoute le token à toutes les requêtes HTTP
- ✅ Format : `Authorization: Bearer <token>`
- ✅ Aucune action manuelle nécessaire dans les composants

### 3. Protection des Routes
- ✅ **authGuard** : Vérifie l'authentification
- ✅ **adminGuard** : Vérifie l'authentification + rôle admin
- ✅ Redirection automatique vers `/login` si non authentifié

### 4. Gestion de l'État
- ✅ **Signals** : Réactivité automatique
- ✅ `isAuthenticated` : État de connexion
- ✅ `currentUser` : Informations utilisateur

### 5. Gestion des Erreurs
- ✅ Si le token est invalide → Backend retourne 401
- ✅ L'interceptor peut être étendu pour gérer les 401 automatiquement
- ✅ Redirection vers `/login` en cas d'erreur

## 🧪 Exemple Complet : Requête Protégée

### Dans un Composant

```typescript
// user-list.component.ts
export class UserListComponent {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  
  users: User[] = [];
  
  ngOnInit() {
    // Cette requête inclut automatiquement le token !
    this.http.get<User[]>(`${getApiUrl('/api/users')}`).subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        if (error.status === 401) {
          // Token invalide ou expiré
          this.authService.logout();
        }
      }
    });
  }
}
```

### Ce qui se passe en arrière-plan

1. **Component** : `this.http.get('/api/users')`
2. **Interceptor** : Récupère le token → Clone la requête → Ajoute `Authorization: Bearer <token>`
3. **Backend Gateway** : Valide le token
4. **Backend UserService** : Valide le token → Traite la requête
5. **Response** : Retourne les données

## ✅ Résumé

Le frontend gère le JWT de manière **automatique et transparente** :

1. ✅ **Login** → Token stocké dans localStorage
2. ✅ **Requêtes HTTP** → Token ajouté automatiquement via interceptor
3. ✅ **Routes** → Protégées par des guards
4. ✅ **État** → Géré avec des signals réactifs
5. ✅ **Logout** → Token supprimé, redirection vers login

**Aucune manipulation manuelle du token n'est nécessaire dans les composants !** 🎯
