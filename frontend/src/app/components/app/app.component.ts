import { Component, HostListener } from '@angular/core';
import { Message } from 'src/app/model/message/message';
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
        this.fetchService.moveRight().subscribe((value: Message) => {
          console.log('moveRight = ' + value? value.content: '' );
        });
        break;
      case 'ArrowLeft':
        this.fetchService.moveLeft().subscribe((value: Message) => {
          console.log('moveLeft = ' + value? value.content: '');
        });
        break;
      case 'ArrowUp':
        this.fetchService.rotateRight().subscribe((value: Message) => {
          console.log('rotateRight = ' + value? value.content: '');
        });
        break;
      case 'ArrowDown':
        this.fetchService.rotateLeft().subscribe((value: Message) => {
          console.log('rotateLeft = ' + value? value.content: '');
        });
        break;
      default:
        break;
    }
  }
}
