import { Routes } from '@angular/router';

import { EventListComponent } from './features/events/event-list.component';
import { CampaignListComponent } from './features/campaigns/campaign-list.component';
import { ParticipantListComponent } from './features/participants/participant-list.component';
import { AnalyticsOverviewComponent } from './features/analytics/analytics-overview.component';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  {
    path: 'users',
    loadChildren: () => import('./features/users/users.module').then(m => m.UsersModule),
    canActivate: [authGuard] // Add auth guard if needed
  },
  { path: 'events', component: EventListComponent},
  { path: 'campaigns', component: CampaignListComponent},
  { path: 'participants', component: ParticipantListComponent},
  { path: 'analytics', component: AnalyticsOverviewComponent},
  { path: '', pathMatch: 'full', redirectTo: 'campaigns' },
  { path: '**', redirectTo: 'campaigns', pathMatch: 'full' },
];

