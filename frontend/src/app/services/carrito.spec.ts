import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Carrito } from './carrito';

describe('Carrito', () => {
  let service: Carrito;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(Carrito);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('gets the logged-in user cart', () => {
    service.obtener().subscribe();

    const request = httpTesting.expectOne('http://localhost:8080/carrito/listar/usuario');
    expect(request.request.method).toBe('GET');
    request.flush({ idCarrito: 1, precio: 0, libros: [] });
  });

  it('removes a book from the cart', () => {
    service.quitarLibro(12).subscribe();

    const request = httpTesting.expectOne('http://localhost:8080/carrito/eliminar-libro/12');
    expect(request.request.method).toBe('PUT');
    request.flush('Libro eliminado del carrito con exito...');
  });
});
