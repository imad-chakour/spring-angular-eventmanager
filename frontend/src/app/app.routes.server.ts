import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Routes publiques - prerender
  {
    path: 'login',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'register',
    renderMode: RenderMode.Prerender
  },
  // Routes protégées - pas de prerendering (nécessitent authentification)
  // Routes avec paramètres - pas de prerendering (nécessitent getPrerenderParams)
  // Toutes les autres routes - prerender
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
