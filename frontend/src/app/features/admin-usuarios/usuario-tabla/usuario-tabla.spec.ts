import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsuarioTabla } from './usuario-tabla';

describe('UsuarioTabla', () => {
  let component: UsuarioTabla;
  let fixture: ComponentFixture<UsuarioTabla>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsuarioTabla]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsuarioTabla);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
