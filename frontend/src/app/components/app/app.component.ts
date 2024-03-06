import { Component, HostListener } from '@angular/core';
import { FetchService } from 'src/app/services/fetch/fetch.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  constructor(private fetchService: FetchService) {}

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    const key = event.key;
    console.log('Key = ' + key);
    switch (key) {
      case 'ArrowRight':
        this.fetchService.moveRight().subscribe((value: boolean) => {
          console.log('moveRight = ' + value);
        });
        break;
      case 'ArrowLeft':
        this.fetchService.moveLeft().subscribe((value: boolean) => {
          console.log('moveLeft = ' + value);
        });
        break;
      case 'ArrowDown':
        this.fetchService.moveDown().subscribe((value: boolean) => {
          console.log('moveDown = ' + value);
        });
        break;
      default:
        break;
    }
  }
}
