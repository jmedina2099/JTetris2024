import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VentanaPrincipalComponent } from './ventana-principal.component';
import { HttpClientModule } from '@angular/common/http';

describe('VentanaPrincipalComponent', () => {
  let component: VentanaPrincipalComponent;
  let fixture: ComponentFixture<VentanaPrincipalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      declarations: [VentanaPrincipalComponent],
    });
    fixture = TestBed.createComponent(VentanaPrincipalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
