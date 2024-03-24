import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { Message } from 'src/app/model/message/message';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { Figure } from './model/figure/figure';
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
  handleKeyboardEvent(event: KeyboardEvent) {
    const key = event.key;
    switch (key) {
      case 'ArrowRight':
        let firstBoxRight = false;
        this.fetchService.moveRight().subscribe({ 
          next: value => {
            if( this.ventana ) {
              if( !firstBoxRight ) {
                this.ventana.fallingBoxes = [];
                firstBoxRight = true;
              }
              this.ventana.fallingBoxes.push(value);
            }
          }
        });
        break;
      case 'ArrowLeft':
        let firstBoxLeft = false;
        this.fetchService.moveLeft().subscribe({ 
          next: value => {
            if( this.ventana ) {
              if( !firstBoxLeft ) {
                this.ventana.fallingBoxes = [];
                firstBoxLeft = true;
              }
              this.ventana.fallingBoxes.push(value);
            }
          }
        });
        break;
      case 'ArrowUp':
        let firstBoxRotateRight = false;
        this.fetchService.rotateRight().subscribe({ 
          next: value => {
            if( this.ventana ) {
              if( !firstBoxRotateRight ) {
                this.ventana.fallingBoxes = [];
                firstBoxRotateRight = true;
              }
              this.ventana.fallingBoxes.push(value);
            }
          }
        });
        break;
      case 'ArrowDown':
        let firstBoxRotateLeft = false;
        this.fetchService.rotateLeft().subscribe({ 
          next: value => {
            if( this.ventana ) {
              if( !firstBoxRotateLeft ) {
                this.ventana.fallingBoxes = [];
                firstBoxRotateLeft = true;
              }
              this.ventana.fallingBoxes.push(value);
            }
          }
        });
        break;
      case ' ':
        this.fetchService.bottomDown().subscribe();
        break;
        default:
        break;
    }
  }
}
