import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { AutorFormulario } from './autor-formulario';

describe('AutorFormulario', () => {
  let fixture: ComponentFixture<AutorFormulario>;
  let httpTesting: HttpTestingController;
  let routeId: string | null;

  beforeEach(async () => {
    routeId = null;
    await TestBed.configureTestingModule({
      imports: [AutorFormulario],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => routeId } } },
        },
      ],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  function crearFormulario(id: string | null = null): void {
    routeId = id;
    fixture = TestBed.createComponent(AutorFormulario);
    fixture.detectChanges();
  }

  function completarFormulario(nombre: string, apellido: string): void {
    const nombreInput: HTMLInputElement = fixture.nativeElement.querySelector('#nombre');
    const apellidoInput: HTMLInputElement = fixture.nativeElement.querySelector('#apellido');
    nombreInput.value = nombre;
    nombreInput.dispatchEvent(new Event('input'));
    apellidoInput.value = apellido;
    apellidoInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  function enviarFormulario(): void {
    const form: HTMLFormElement = fixture.nativeElement.querySelector('form');
    form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    fixture.detectChanges();
  }

  it('requires both fields and reports field-level validation errors', () => {
    crearFormulario();
    enviarFormulario();

    expect(fixture.nativeElement.textContent).toContain('El nombre es obligatorio.');
    expect(fixture.nativeElement.textContent).toContain('El apellido es obligatorio.');
    httpTesting.expectNone('http://localhost:8080/autores/crear');
  });

  it('rejects fields containing only whitespace', () => {
    crearFormulario();
    completarFormulario('   ', 'Cortázar');
    enviarFormulario();

    expect(fixture.nativeElement.textContent).toContain(
      'El nombre no puede contener únicamente espacios.'
    );
    httpTesting.expectNone('http://localhost:8080/autores/crear');
  });

  it('trims and submits a new author', () => {
    crearFormulario();
    completarFormulario('  Julio  ', '  Cortázar  ');
    enviarFormulario();

    const request = httpTesting.expectOne('http://localhost:8080/autores/crear');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ nombre: 'Julio', apellido: 'Cortázar' });
    request.flush('Autor agregado con éxito.');
  });

  it('shows the duplicate-author message returned by the server', () => {
    crearFormulario();
    completarFormulario('Julio', 'Cortázar');
    enviarFormulario();

    httpTesting.expectOne('http://localhost:8080/autores/crear').flush(
      'Ya existe un autor con ese nombre y apellido.',
      { status: 409, statusText: 'Conflict' }
    );
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain(
      'Ya existe un autor con ese nombre y apellido.'
    );
  });

  it('loads and updates an existing author', () => {
    crearFormulario('7');
    httpTesting.expectOne('http://localhost:8080/autores/7').flush({
      idAutor: 7,
      nombre: 'Julio',
      apellido: 'Cortázar',
      libros: [],
    });
    fixture.detectChanges();
    completarFormulario('Julio', 'Cortázar');
    enviarFormulario();

    const request = httpTesting.expectOne('http://localhost:8080/autores/actualizar-autor/7');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({ nombre: 'Julio', apellido: 'Cortázar' });
    request.flush('Autor actualizado con éxito.');
  });

  it('reports a missing author when edit data cannot be loaded', () => {
    crearFormulario('7');
    httpTesting.expectOne('http://localhost:8080/autores/7').flush(
      'No se encontró el autor.',
      { status: 404, statusText: 'Not Found' }
    );
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain(
      'No se encontró el autor que querés editar.'
    );
    const botonEnviar: HTMLButtonElement =
      fixture.nativeElement.querySelector('button[type="submit"]');
    expect(botonEnviar.disabled).toBeTrue();
  });
});
