import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Carrito } from './carrito';

const URL_CARRITO = 'http://localhost:8080/carrito/listar/usuario';

const carritoConLibros = {
  idCarrito: 3,
  precio: 21500,
  libros: [
    {
      idLibro: 12,
      titulo: 'Rayuela',
      precio: 12500,
      fechaLanzamiento: '1963-06-28',
      urlPortada: 'https://covers.openlibrary.org/b/id/1-M.jpg',
    },
    { idLibro: 15, titulo: 'Ficciones', precio: 9000, fechaLanzamiento: '1944-01-01', urlPortada: null },
  ],
};

describe('Carrito', () => {
  let fixture: ComponentFixture<Carrito>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Carrito],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Carrito);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  function textoPagina(): string {
    return fixture.nativeElement.textContent.replace(/\s+/g, ' ');
  }

  it('shows the books in the cart with the total', () => {
    httpTesting.expectOne(URL_CARRITO).flush(carritoConLibros);
    fixture.detectChanges();

    const items = fixture.nativeElement.querySelectorAll('li.item');
    expect(items.length).toBe(2);
    expect(textoPagina()).toContain('Rayuela');
    expect(textoPagina()).toContain('Ficciones');
    expect(textoPagina()).toContain('Total (2 libros)');
    expect(textoPagina()).toMatch(/21\.500/);
    expect(fixture.nativeElement.querySelector('a.titulo')?.getAttribute('href')).toBe('/libros/12');
    expect(fixture.nativeElement.querySelector('a.boton-pagar')?.getAttribute('href')).toBe('/checkout');
  });

  it('shows an empty state when the cart has no books', () => {
    httpTesting.expectOne(URL_CARRITO).flush({ idCarrito: 3, precio: 0, libros: [] });
    fixture.detectChanges();

    expect(textoPagina()).toContain('Tu carrito está vacío.');
    expect(fixture.nativeElement.querySelector('a.boton-pagar')).toBeNull();
  });

  it('removes a book and updates the total', () => {
    httpTesting.expectOne(URL_CARRITO).flush(carritoConLibros);
    fixture.detectChanges();

    const botonQuitar: HTMLButtonElement = fixture.nativeElement.querySelector('button.quitar');
    botonQuitar.click();

    const request = httpTesting.expectOne('http://localhost:8080/carrito/eliminar-libro/12');
    expect(request.request.method).toBe('PUT');
    request.flush('Libro eliminado del carrito con exito...');
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li.item').length).toBe(1);
    expect(textoPagina()).not.toContain('Rayuela');
    expect(textoPagina()).toContain('Total (1 libro)');
    expect(textoPagina()).toMatch(/9\.000/);
  });

  it('keeps the book and shows an error when removing fails', () => {
    httpTesting.expectOne(URL_CARRITO).flush(carritoConLibros);
    fixture.detectChanges();

    fixture.nativeElement.querySelector('button.quitar').click();
    httpTesting
      .expectOne('http://localhost:8080/carrito/eliminar-libro/12')
      .flush('Error interno del servidor.', { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li.item').length).toBe(2);
    expect(textoPagina()).toContain('No se pudo quitar "Rayuela" del carrito.');
  });

  it('shows an error message when the cart cannot be loaded', () => {
    httpTesting
      .expectOne(URL_CARRITO)
      .flush('Error interno del servidor.', { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();

    expect(textoPagina()).toContain('No se pudo cargar el carrito. Intentá de nuevo.');
  });
});
