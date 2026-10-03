import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutorFormulario } from './autor-formulario';

describe('AutorFormulario', () => {
  let component: AutorFormulario;
  let fixture: ComponentFixture<AutorFormulario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutorFormulario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AutorFormulario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
