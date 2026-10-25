import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { LibroDetalle } from './libro-detalle';

const libro = {
  id: 12,
  titulo: 'Rayuela',
  sinopsis: 'Una novela experimental.',
  precio: 12500,
  fecha_lanzamiento: '1963-06-28',
  autores: [{ nombre: 'Julio', apellido: 'Cortázar' }],
  generos: [{ nombre: 'Novela' }],
  urlPortada: null,
};

describe('LibroDetalle', () => {
  let fixture: ComponentFixture<LibroDetalle>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibroDetalle],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: '12' }) } } },
      ],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(LibroDetalle);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  function responder(librosEnBiblioteca: { idLibro: number }[]) {
    httpTesting.expectOne('http://localhost:8080/biblioteca/usuario').flush({
      idUsuario: 7,
      nombreUsuario: 'ana',
      libros: librosEnBiblioteca,
    });
    httpTesting.expectOne('http://localhost:8080/libro/buscar/id/12').flush(libro);
    fixture.detectChanges();
  }

  it('offers to add the book to the cart when the user does not own it', () => {
    responder([{ idLibro: 15 }]);

    expect(fixture.nativeElement.textContent).toContain('Rayuela');
    expect(fixture.nativeElement.querySelector('button')?.textContent).toContain('Agregar al carrito');
    expect(fixture.nativeElement.querySelector('.ya-comprado')).toBeNull();
  });

  it('hides the add to cart button when the book is already in the library', () => {
    responder([{ idLibro: 12 }]);

    expect(fixture.nativeElement.querySelector('button')).toBeNull();
    expect(fixture.nativeElement.querySelector('.ya-comprado')?.textContent).toContain(
      'Ya tenés este libro.'
    );
    expect(
      fixture.nativeElement.querySelector('.ya-comprado a')?.getAttribute('href')
    ).toBe('/mi-biblioteca');
  });
});
