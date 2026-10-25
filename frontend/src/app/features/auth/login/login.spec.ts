import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Login } from './login';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    })
    .compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('muestra el aviso de cuenta suspendida cuando el usuario está baneado', () => {
    component.formulario.setValue({ email: 'beto@mail.com', password: 'clave1234' });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    httpTesting
      .expectOne('http://localhost:8080/auth')
      .flush('Tu cuenta fue suspendida. Contactá a un administrador.', {
        status: 403,
        statusText: 'Forbidden',
      });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Tu cuenta fue suspendida');
  });
});
