import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { LibroTabla } from './libro-tabla';

describe('LibroTabla', () => {
  let component: LibroTabla;
  let fixture: ComponentFixture<LibroTabla>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibroTabla],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibroTabla);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
