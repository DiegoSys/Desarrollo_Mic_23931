import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionPOAComponent } from './gestion-poa.component';

describe('GestionPOAComponent', () => {
  let component: GestionPOAComponent;
  let fixture: ComponentFixture<GestionPOAComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionPOAComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionPOAComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
