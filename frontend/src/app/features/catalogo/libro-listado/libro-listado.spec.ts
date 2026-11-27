import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { LibroListado } from './libro-listado';

describe('LibroListado', () => {
  let component: LibroListado;
  let fixture: ComponentFixture<LibroListado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibroListado],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibroListado);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
