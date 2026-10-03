import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibroListado } from './libro-listado';

describe('LibroListado', () => {
  let component: LibroListado;
  let fixture: ComponentFixture<LibroListado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibroListado]
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
