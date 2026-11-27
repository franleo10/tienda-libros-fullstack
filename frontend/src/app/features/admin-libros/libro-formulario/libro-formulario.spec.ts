import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { LibroFormulario } from './libro-formulario';

describe('LibroFormulario', () => {
  let component: LibroFormulario;
  let fixture: ComponentFixture<LibroFormulario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibroFormulario],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibroFormulario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
