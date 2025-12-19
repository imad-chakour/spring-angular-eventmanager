import { Routes } from '@angular/router';

import { EventListComponent } from './features/events/event-list.component';
import { CampaignListComponent } from './features/campaigns/campaign-list.component';
import { HomeComponent } from './features/home/home.component';
import { AnalyticsOverviewComponent } from './features/analytics/analytics-overview.component';
import { NotificationListComponent } from './features/notifications/notification-list.component';
import { LoginComponent } from './features/users/components/login/login.component';
import { RegisterComponent } from './features/users/components/register/register.component';
import { UserListComponent } from './features/users/components/user-list/user-list.component';
import { UserFormComponent } from './features/users/components/user-form/user-form.component';
import { authGuard, adminGuard } from './core/auth.guard';

export const routes: Routes = [
  // Routes publiques
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Routes protégées
  { path: '', component: HomeComponent, canActivate: [authGuard], pathMatch: 'full' },
  { path: 'events', component: EventListComponent, canActivate: [authGuard] },
  { path: 'campaigns', component: CampaignListComponent, canActivate: [authGuard] },
  { path: 'analytics', component: AnalyticsOverviewComponent, canActivate: [authGuard] },
  { path: 'notifications', component: NotificationListComponent, canActivate: [authGuard] },

  // Routes gestion utilisateurs (admin uniquement)
  { path: 'users', component: UserListComponent, canActivate: [adminGuard] },
  { path: 'users/new', component: UserFormComponent, canActivate: [adminGuard] },
  { path: 'users/:id/edit', component: UserFormComponent, canActivate: [adminGuard] },

  // Routes par défaut
  { path: '**', redirectTo: '', pathMatch: 'full' },
];
