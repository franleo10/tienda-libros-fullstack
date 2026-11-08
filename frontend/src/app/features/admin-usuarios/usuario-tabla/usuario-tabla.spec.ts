import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { UsuarioTabla } from './usuario-tabla';

const API = 'http://localhost:8080/usuario';

const pagina = (content: unknown[], totalPages = 1, number = 0) => ({
  content,
  totalPages,
  totalElements: content.length,
  number,
});

const ana = { id: 1, nombre: 'Ana', email: 'ana@mail.com', rol: 'ADMINISTRADOR', activo: true };
const beto = { id: 2, nombre: 'Beto', email: 'beto@mail.com', rol: 'USUARIO', activo: true };

describe('UsuarioTabla', () => {
  let fixture: ComponentFixture<UsuarioTabla>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsuarioTabla],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(UsuarioTabla);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  const texto = () => fixture.nativeElement.textContent as string;

  it('muestra los usuarios con su rol y estado', () => {
    httpTesting.expectOne(`${API}/admin/listar/0`).flush(pagina([ana, { ...beto, activo: false }]));
    fixture.detectChanges();

    expect(texto()).toContain('ana@mail.com');
    expect(texto()).toContain('Administrador');
    expect(texto()).toContain('Baneado');
    expect(fixture.nativeElement.querySelector('button.banear')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('button.reactivar')).not.toBeNull();
  });

  it('envía la búsqueda y los filtros al backend', () => {
    httpTesting.expectOne(`${API}/admin/listar/0`).flush(pagina([ana, beto]));

    const input: HTMLInputElement = fixture.nativeElement.querySelector('#buscar-usuario');
    input.value = ' beto ';
    input.dispatchEvent(new Event('input'));
    const rol: HTMLSelectElement = fixture.nativeElement.querySelector('#filtro-rol');
    rol.value = 'USUARIO';
    rol.dispatchEvent(new Event('change'));

    const request = httpTesting.expectOne(
      (req) =>
        req.url === `${API}/admin/listar/0` &&
        req.params.get('busqueda') === 'beto' &&
        req.params.get('rol') === 'USUARIO' &&
        !req.params.has('activo')
    );
    request.flush(pagina([beto]));
    fixture.detectChanges();

    expect(texto()).toContain('beto@mail.com');
    expect(texto()).not.toContain('ana@mail.com');
  });
});
