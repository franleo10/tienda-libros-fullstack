import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { ReseniaListado } from './resenia-listado';

describe('ReseniaListado', () => {
  let component: ReseniaListado;
  let fixture: ComponentFixture<ReseniaListado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReseniaListado],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReseniaListado);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('idLibro', 1);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
