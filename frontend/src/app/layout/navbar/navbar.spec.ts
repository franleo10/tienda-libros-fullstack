import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Navbar } from './navbar';

describe('Navbar', () => {
  let fixture: ComponentFixture<Navbar>;
  let httpTesting: HttpTestingController;

  // El token se lee de localStorage al crear el servicio Auth, por eso se setea antes de crear el componente
  const crear = (token: string | null) => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Navbar);
    fixture.detectChanges();
  };

  const linkUsuarios = () => fixture.nativeElement.querySelector('a[href="/admin/usuarios"]');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Navbar],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.removeItem('token');
  });

  it('should create', () => {
    crear(null);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('muestra Admin Usuarios a quien puede administrar usuarios', () => {
    crear('token-admin');
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['ELIMINAR_USUARIOS']);
    fixture.detectChanges();

    expect(linkUsuarios()).not.toBeNull();
    expect(fixture.nativeElement.querySelector('a[href="/admin/libros"]')).toBeNull();
  });

  it('oculta Admin Usuarios a un usuario común', () => {
    crear('token-usuario');
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_LIBROS', 'VER_CARRITO']);
    fixture.detectChanges();

    expect(linkUsuarios()).toBeNull();
  });

  it('no consulta permisos ni muestra links de admin sin sesión', () => {
    crear(null);

    httpTesting.expectNone('http://localhost:8080/me/permisos');
    expect(linkUsuarios()).toBeNull();
  });
});
