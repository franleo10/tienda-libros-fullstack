import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { MiBiblioteca } from './mi-biblioteca';

const URL_BIBLIOTECA = 'http://localhost:8080/biblioteca/usuario';

describe('MiBiblioteca', () => {
  let fixture: ComponentFixture<MiBiblioteca>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiBiblioteca],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MiBiblioteca);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  function textoPagina(): string {
    return fixture.nativeElement.textContent.replace(/\s+/g, ' ');
  }

  it('shows the purchased books sorted by title, each linking to its detail', () => {
    httpTesting.expectOne(URL_BIBLIOTECA).flush({
      idUsuario: 7,
      nombreUsuario: 'ana',
      libros: [
        {
          idLibro: 15,
          titulo: 'Rayuela',
          precio: 12500,
          urlPortada: 'https://covers.openlibrary.org/b/id/1-M.jpg',
          autores: [{ nombre: 'Julio', apellido: 'Cortázar' }],
          esFavorito: true,
        },
        {
          idLibro: 12,
          titulo: 'Bestiario',
          precio: 9000,
          urlPortada: null,
          autores: [{ nombre: 'Julio', apellido: 'Cortázar' }],
          esFavorito: false,
        },
      ],
    });
    fixture.detectChanges();

    const tarjetas: HTMLAnchorElement[] = Array.from(
      fixture.nativeElement.querySelectorAll('a.tarjeta')
    );
    expect(tarjetas.length).toBe(2);
    expect(tarjetas[0].textContent).toContain('Bestiario');
    expect(tarjetas[0].getAttribute('href')).toBe('/libros/12');
    expect(tarjetas[1].getAttribute('href')).toBe('/libros/15');
    expect(tarjetas[1].querySelector('img')?.getAttribute('src')).toBe(
      'https://covers.openlibrary.org/b/id/1-M.jpg'
    );
    expect(textoPagina()).toContain('Julio Cortázar');
    expect(textoPagina()).toContain('2 libros adquiridos');
    expect(fixture.nativeElement.querySelectorAll('.favorito').length).toBe(1);
  });

  it('shows an empty state when the user has no books', () => {
    httpTesting.expectOne(URL_BIBLIOTECA).flush({ idUsuario: 7, nombreUsuario: 'ana', libros: [] });
    fixture.detectChanges();

    expect(textoPagina()).toContain('Todavía no compraste ningún libro.');
    expect(fixture.nativeElement.querySelector('a.boton-secundario')?.getAttribute('href')).toBe(
      '/libros'
    );
  });

  it('shows an error message when the library cannot be loaded', () => {
    httpTesting
      .expectOne(URL_BIBLIOTECA)
      .flush('Error interno del servidor.', { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();

    expect(textoPagina()).toContain('No se pudo cargar tu biblioteca. Intentá de nuevo.');
  });
});
