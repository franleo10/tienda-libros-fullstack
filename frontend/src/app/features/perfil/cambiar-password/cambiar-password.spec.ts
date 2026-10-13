import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { CambiarPassword } from './cambiar-password';

describe('CambiarPassword', () => {
  let fixture: ComponentFixture<CambiarPassword>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CambiarPassword],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(CambiarPassword);
    fixture.detectChanges();
  });

  afterEach(() => httpTesting.verify());

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('does not submit when the new password confirmation does not match', () => {
    const setValue = (selector: string, value: string) => {
      const input = fixture.nativeElement.querySelector(selector) as HTMLInputElement;
      input.value = value;
      input.dispatchEvent(new Event('input'));
      input.dispatchEvent(new Event('blur'));
    };

    setValue('#passwordActual', 'current-password');
    setValue('#passwordNueva', 'new-password');
    setValue('#confirmarPassword', 'different-password');
    fixture.detectChanges();
    (fixture.nativeElement.querySelector('form') as HTMLFormElement).dispatchEvent(
      new Event('submit', { bubbles: true, cancelable: true })
    );

    httpTesting.expectNone('http://localhost:8080/me/password');
    expect(fixture.nativeElement.textContent).toContain('Las contraseñas nuevas no coinciden.');
  });
});
