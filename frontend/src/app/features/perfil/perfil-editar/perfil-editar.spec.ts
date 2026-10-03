import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerfilEditar } from './perfil-editar';

describe('PerfilEditar', () => {
  let component: PerfilEditar;
  let fixture: ComponentFixture<PerfilEditar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilEditar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PerfilEditar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
