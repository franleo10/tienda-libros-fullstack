import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Estrellas } from './estrellas';

describe('Estrellas', () => {
  let component: Estrellas;
  let fixture: ComponentFixture<Estrellas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Estrellas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Estrellas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
