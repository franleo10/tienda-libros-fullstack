import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { AutorListado } from './autor-listado';

describe('AutorListado', () => {
  let fixture: ComponentFixture<AutorListado>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutorListado],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(AutorListado);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
    });
  });

  it('loads and displays authors from the API', () => {
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [{ idAutor: 1, nombre: 'Gabriel', apellido: 'García Márquez', cantidadLibros: 1 }],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Gabriel García Márquez');
    expect(fixture.nativeElement.textContent).toContain('1 libro');
    expect(fixture.nativeElement.querySelector('a.autor-card')?.getAttribute('href')).toBe('/autores/1');
    expect(fixture.nativeElement.querySelector('a[href="/autores/nuevo"]')).toBeNull();
    expect(fixture.nativeElement.querySelector('a[href="/autores/1/editar"]')).toBeNull();
    expect(fixture.nativeElement.querySelector('button.eliminar-autor')).toBeNull();
  });

  it('searches the API by author name', () => {
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
    });

    const input: HTMLInputElement = fixture.nativeElement.querySelector('#buscar-autor');
    input.value = 'Cortázar';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const form: HTMLFormElement = fixture.nativeElement.querySelector('form');
    form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));

    const request = httpTesting.expectOne(
      (req) =>
        req.url === 'http://localhost:8080/autores/buscar' &&
        req.params.get('nombre') === 'Cortázar' &&
        req.params.get('pagina') === '0'
    );
    expect(request.request.method).toBe('GET');
    request.flush({
      content: [{ idAutor: 2, nombre: 'Julio', apellido: 'Cortázar', cantidadLibros: 1 }],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });

    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Julio Cortázar');
  });

  it('shows author management actions only to users with the corresponding permissions', () => {
    httpTesting
      .expectOne('http://localhost:8080/me/permisos')
      .flush(['VER_AUTORES', 'AGREGAR_AUTOR', 'MODIFICAR_AUTOR']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [{ idAutor: 2, nombre: 'Julio', apellido: 'Cortázar', cantidadLibros: 1 }],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a[href="/autores/nuevo"]')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('a[href="/autores/2/editar"]')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('button.eliminar-autor')).toBeNull();
  });

  it('deactivates an author after confirmation and reloads the list', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    httpTesting
      .expectOne('http://localhost:8080/me/permisos')
      .flush(['VER_AUTORES', 'ELIMINAR_AUTOR']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [{ idAutor: 2, nombre: 'Julio', apellido: 'Cortázar', cantidadLibros: 1 }],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });
    fixture.detectChanges();

    const deactivateButton: HTMLButtonElement = fixture.nativeElement.querySelector(
      'button.eliminar-autor'
    );
    expect(deactivateButton).not.toBeNull();
    deactivateButton.click();

    const deactivateRequest = httpTesting.expectOne('http://localhost:8080/autores/baja/2');
    expect(deactivateRequest.request.method).toBe('PUT');
    deactivateRequest.flush('Autor dado de baja correctamente.');

    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain(
      'El autor Julio Cortázar se dio de baja correctamente.'
    );
    expect(fixture.nativeElement.textContent).toContain('No se encontraron autores.');
  });

  it('does not deactivate an author when confirmation is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    httpTesting
      .expectOne('http://localhost:8080/me/permisos')
      .flush(['VER_AUTORES', 'ELIMINAR_AUTOR']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [{ idAutor: 2, nombre: 'Julio', apellido: 'Cortázar', cantidadLibros: 1 }],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });
    fixture.detectChanges();

    (fixture.nativeElement.querySelector('button.eliminar-autor') as HTMLButtonElement).click();

    expect(window.confirm).toHaveBeenCalledWith(
      '¿Dar de baja a Julio Cortázar? Dejará de aparecer en el catálogo, pero se conservarán sus datos y sus libros.'
    );
    httpTesting.expectNone('http://localhost:8080/autores/baja/2');
  });

  it('lists inactive authors and allows reactivating one', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    httpTesting
      .expectOne('http://localhost:8080/me/permisos')
      .flush(['VER_AUTORES', 'ELIMINAR_AUTOR']);
    httpTesting.expectOne('http://localhost:8080/autores/listar/0').flush({
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
    });
    fixture.detectChanges();

    (fixture.nativeElement.querySelector('button.boton-nuevo') as HTMLButtonElement).click();
    fixture.detectChanges();
    const inactiveRequest = httpTesting.expectOne(
      'http://localhost:8080/autores/listar/inactivos/0'
    );
    inactiveRequest.flush({
      content: [{ idAutor: 2, nombre: 'Julio', apellido: 'Cortázar', cantidadLibros: 1 }],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Autor dado de baja');
    const reactivateButton: HTMLButtonElement = fixture.nativeElement.querySelector(
      'button.editar-autor'
    );
    expect(reactivateButton.textContent).toContain('Reactivar autor');
    reactivateButton.click();

    const reactivateRequest = httpTesting.expectOne('http://localhost:8080/autores/alta/2');
    expect(reactivateRequest.request.method).toBe('PUT');
    reactivateRequest.flush('Autor reactivado correctamente.');
    httpTesting.expectOne('http://localhost:8080/autores/listar/inactivos/0').flush({
      content: [],
      totalPages: 0,
      totalElements: 0,
      number: 0,
    });

    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('El autor Julio Cortázar volvió a estar activo.');
  });
});
