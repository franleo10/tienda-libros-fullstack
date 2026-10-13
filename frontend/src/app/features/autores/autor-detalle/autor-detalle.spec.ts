import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

import { AutorDetalle } from './autor-detalle';

describe('AutorDetalle', () => {
  let fixture: ComponentFixture<AutorDetalle>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    const paramMap = new BehaviorSubject(convertToParamMap({ id: '1' }));

    await TestBed.configureTestingModule({
      imports: [AutorDetalle],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { paramMap } },
      ],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(AutorDetalle);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);
    httpTesting.expectOne('http://localhost:8080/autores/1').flush({
      idAutor: 1,
      nombre: 'Julio',
      apellido: 'Cortázar',
      libros: [],
    });
  });

  it('loads the author books with their details, reviews and prices', () => {
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);
    httpTesting.expectOne('http://localhost:8080/autores/1').flush({
      idAutor: 1,
      nombre: 'Julio',
      apellido: 'Cortázar',
      libros: [
        {
          id: 12,
          titulo: 'Rayuela',
          sinopsis: 'Una novela experimental.',
          precio: 12500,
          fecha_lanzamiento: '1963-06-28',
          generos: [{ nombre: 'Novela' }],
          resenias: [
            {
              id: 7,
              texto: 'Una lectura inolvidable.',
              calificacion: 5,
              nombreUsuario: 'lectora',
            },
            {
              id: 8,
              texto: 'Muy original.',
              calificacion: 4,
              nombreUsuario: 'lector',
            },
          ],
        },
      ],
    });

    fixture.detectChanges();
    const text = fixture.nativeElement.textContent as string;

    expect(text).toContain('Julio Cortázar');
    expect(text).toContain('Rayuela');
    expect(text).toContain('Una novela experimental.');
    expect(text).toContain('Novela');
    expect(text).toContain('12.500,00');
    expect(text).toContain('Una lectura inolvidable.');
    expect(text).toContain('5/5');
    expect(text).toContain('Calificación promedio');
  });

  it('shows an error when the author does not exist', () => {
    httpTesting.expectOne('http://localhost:8080/me/permisos').flush(['VER_AUTORES']);
    httpTesting.expectOne('http://localhost:8080/autores/1').flush(
      { message: 'Autor no encontrado' },
      { status: 404, statusText: 'Not Found' }
    );

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No se encontró el autor solicitado.');
  });

  it('shows the edit action to users with the edit permission', () => {
    httpTesting
      .expectOne('http://localhost:8080/me/permisos')
      .flush(['VER_AUTORES', 'MODIFICAR_AUTOR']);
    httpTesting.expectOne('http://localhost:8080/autores/1').flush({
      idAutor: 1,
      nombre: 'Julio',
      apellido: 'Cortázar',
      libros: [],
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a[href="/autores/1/editar"]')).not.toBeNull();
  });
});
