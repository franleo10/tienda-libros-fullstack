import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Perfil } from './perfil';

describe('Perfil', () => {
  let fixture: ComponentFixture<Perfil>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Perfil],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Perfil);
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

  it('shows the signed-in user profile details', () => {
    httpTesting.expectOne('http://localhost:8080/me/perfil').flush({
      id: 1,
      nombre: 'Ana',
      email: 'ana@example.com',
      edad: 25,
    });

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Ana');
    expect(fixture.nativeElement.textContent).toContain('ana@example.com');
    expect(fixture.nativeElement.textContent).toContain('25');
  });
});
