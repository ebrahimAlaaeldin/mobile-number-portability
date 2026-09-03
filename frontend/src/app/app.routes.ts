import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'requests', pathMatch: 'full' },
  {
    path: 'requests',
    loadComponent: () => import('./features/requests/requests-page').then((m) => m.RequestsPage),
    title: 'Requests · MNP',
  },
  {
    path: 'new',
    loadComponent: () => import('./features/new-request/new-request-page').then((m) => m.NewRequestPage),
    title: 'New Request · MNP',
  },
  {
    path: 'lookup',
    loadComponent: () => import('./features/number-lookup/number-lookup-page').then((m) => m.NumberLookupPage),
    title: 'Number Lookup · MNP',
  },
  { path: '**', redirectTo: 'requests' },
];
