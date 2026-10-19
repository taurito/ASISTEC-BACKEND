import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Dashboard } from './features/dashboard/dashboard';
import { authGuard } from './core/auth/auth.guard';
import { Activos } from './features/activos/activos/activos';
import { EmpleadosComponent } from './features/empleados/empleados/empleados';
import { roleGuard } from './core/auth/role.guard';
import { AsignacionesComponent } from './features/asignaciones/asignaciones/asignaciones';
import { AsistenciasComponent } from './features/asistencias/asistencias/asistencias';

export const routes: Routes = [
    {
        path: 'login',
        component: Login,
        title: 'Iniciar Sesion'
    },
    {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard],
        title: 'Panel Principal'
    },
    {
        path: 'activos',
        component: Activos,
        canActivate: [authGuard],
        title: 'Activos Tecnologicos'
    },
    {
        path: 'empleados',
        component: EmpleadosComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            roles: ['ADMIN', 'TECNICO']
        },
        title: 'Empleados'
    },
    {
        path: 'asignaciones',
        component: AsignacionesComponent,
        canActivate: [authGuard],
        title: 'Asignaciones de activos'
    },
    {
        path: 'asistencias',
        component: AsistenciasComponent,
        canActivate: [authGuard],
        title: 'asistencias técnica'
    },
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'login'
    },
    {
        path: '**',
        redirectTo: 'login'
    },

];
