import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { PerfilEditar } from './perfil-editar';

describe('PerfilEditar', () => {
  let fixture: ComponentFixture<PerfilEditar>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilEditar],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(PerfilEditar);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
    httpTesting.expectOne('http://localhost:8080/me/perfil').flush({
      id: 1,
      nombre: 'Ana',
      email: 'ana@example.com',
      edad: 25,
    });
  });

  it('loads profile fields for editing', () => {
    httpTesting.expectOne('http://localhost:8080/me/perfil').flush({
      id: 1,
      nombre: 'Ana',
      email: 'ana@example.com',
      edad: 25,
    });
    fixture.detectChanges();

    expect((fixture.nativeElement.querySelector('#nombre') as HTMLInputElement).value).toBe('Ana');
    expect((fixture.nativeElement.querySelector('#email') as HTMLInputElement).value).toBe('ana@example.com');
    expect((fixture.nativeElement.querySelector('#edad') as HTMLInputElement).value).toBe('25');
  });

  it('shows a clear message when the username is already occupied', () => {
    httpTesting.expectOne('http://localhost:8080/me/perfil').flush({
      id: 1,
      nombre: 'Ana',
      email: 'ana@example.com',
      edad: 25,
    });
    fixture.detectChanges();

    const nameInput = fixture.nativeElement.querySelector('#nombre') as HTMLInputElement;
    nameInput.value = 'ocupado';
    nameInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    fixture.nativeElement.querySelector('form').dispatchEvent(
      new Event('submit', { bubbles: true, cancelable: true })
    );
    fixture.detectChanges();

    httpTesting.expectOne('http://localhost:8080/me/perfil').flush(
      'El nombre de usuario que ingresó ya está ocupado.',
      { status: 409, statusText: 'Conflict' }
    );
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain(
      'El nombre de usuario que ingresó ya está ocupado.'
    );
  });

  it('does not show raw database details for a conflict', () => {
    httpTesting.expectOne('http://localhost:8080/me/perfil').flush({
      id: 1,
      nombre: 'Ana',
      email: 'ana@example.com',
      edad: 25,
    });
    fixture.detectChanges();

    const nameInput = fixture.nativeElement.querySelector('#nombre') as HTMLInputElement;
    nameInput.value = 'ocupado';
    nameInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    fixture.nativeElement.querySelector('form').dispatchEvent(
      new Event('submit', { bubbles: true, cancelable: true })
    );
    fixture.detectChanges();

    httpTesting.expectOne('http://localhost:8080/me/perfil').flush(
      "Violación de integridad de datos: Duplicate entry 'ocupado'",
      { status: 409, statusText: 'Conflict' }
    );
    fixture.detectChanges();

    const errorMessage = fixture.nativeElement.querySelector('[role="alert"]').textContent;
    expect(errorMessage).toContain('El nombre de usuario o el email');
    expect(errorMessage).not.toContain('Duplicate entry');
  });
});
