import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';

import { permisoGuard } from './permiso.guard';
import { Auth } from '../services/auth';

@Component({ standalone: true, template: '' })
class GuardTestPage {}

describe('permisoGuard', () => {
  let httpTesting: HttpTestingController;
  let auth: Auth;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          {
            path: 'autores/nuevo',
            component: GuardTestPage,
            canActivate: [permisoGuard],
            data: { permiso: 'AGREGAR_AUTOR' },
          },
          { path: 'autores', component: GuardTestPage },
        ]),
      ],
    });
    httpTesting = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(Auth);
    router = TestBed.inject(Router);
    auth.token.set('sesion-de-prueba');
  });

  afterEach(() => httpTesting.verify());

  it('permits access only when the authenticated user has the required permission', async () => {
    const navegacion = router.navigateByUrl('/autores/nuevo');
    await new Promise((resolve) => setTimeout(resolve, 0));
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush([
      'VER_AUTORES',
      'AGREGAR_AUTOR',
    ]);

    await expectAsync(navegacion).toBeResolvedTo(true);
    expect(router.url).toBe('/autores/nuevo');
  });

  it('redirects users without the required permission back to the authors list', async () => {
    const navegacion = router.navigateByUrl('/autores/nuevo');
    await new Promise((resolve) => setTimeout(resolve, 0));
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);

    await expectAsync(navegacion).toBeResolvedTo(true);
    expect(router.url).toBe('/autores?acceso=denegado');
  });
});
