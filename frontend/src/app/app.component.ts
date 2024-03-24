import { Component, HostListener, ViewChild } from '@angular/core';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { VentanaPrincipalComponent } from './components/ventana-principal/ventana-principal.component';
import { Box } from './model/figure/box';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  constructor(private fetchService: FetchService) {}

  @ViewChild(VentanaPrincipalComponent) ventana!: VentanaPrincipalComponent;

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    const key = event.key;
    const firstBoxRight: boolean[] = [false];
    const firstBoxLeft: boolean[] = [false];
    const firstBoxRotateRight: boolean[] = [false];
    const firstBoxRotateLeft: boolean[] = [false];
    switch (key) {
      case 'ArrowRight':
        this.fetchService.moveRight().subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxRight),
        });
        break;
      case 'ArrowLeft':
        this.fetchService.moveLeft().subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxLeft),
        });
        break;
      case 'ArrowUp':
        this.fetchService.rotateRight().subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxRotateRight),
        });
        break;
      case 'ArrowDown':
        this.fetchService.rotateLeft().subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxRotateLeft),
        });
        break;
      case ' ':
        this.fetchService.bottomDown().subscribe();
        break;
      default:
        break;
    }
  }

  fillFallingBoxes(box: Box, firstBox: boolean[]): void {
    if (this.ventana) {
      if (!firstBox[0]) {
        this.ventana.fallingBoxes = [];
        firstBox[0] = true;
      }
      this.ventana.fallingBoxes.push(box);
    }
  }
}
