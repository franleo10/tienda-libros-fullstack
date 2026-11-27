import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { ReseniaFormulario } from './resenia-formulario';

describe('ReseniaFormulario', () => {
  let component: ReseniaFormulario;
  let fixture: ComponentFixture<ReseniaFormulario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReseniaFormulario],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReseniaFormulario);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('idLibro', 1);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
