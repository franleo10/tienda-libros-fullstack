import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { Auth } from '../services/auth';
import { Permisos } from '../services/permisos';

export const permisoGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const auth = inject(Auth);
  const permisos = inject(Permisos);
  const permiso = route.data['permiso'];

  if (!auth.token()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }

  if (typeof permiso !== 'string') {
    return router.createUrlTree(['/autores']);
  }

  return permisos.cargar().pipe(
    map((lista) =>
      lista.includes(permiso)
        ? true
        : router.createUrlTree(['/autores'], { queryParams: { acceso: 'denegado' } })
    ),
    catchError((error: HttpErrorResponse) =>
      of(
        error.status === 401
          ? router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } })
          : router.createUrlTree(['/autores'], {
              queryParams: { acceso: error.status === 403 ? 'denegado' : 'verificacion' },
            })
      )
    )
  );
};
