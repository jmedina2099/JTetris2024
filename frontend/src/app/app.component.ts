import { Component, HostListener, OnInit } from '@angular/core';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { Box } from './model/figure/box';
import { ActivatedRoute, Router } from '@angular/router';
import { AppService } from './services/app/app.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  private lastCallTime = 0;

  constructor(
    private app: AppService,
    private fetchService: FetchService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authenticated()) {
      this.router.navigate(['/jtetris']);
    }
  }

  logout() {
    this.app.logout();
  }

  authenticated() {
    return this.app.authenticated;
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    const key = event.key;
    const elapsedTime = Date.now() - this.lastCallTime;
    if (elapsedTime > 50) {
      switch (key) {
        case 'ArrowRight':
          this.lastCallTime = Date.now();
          this.fetchService.moveRight().subscribe({
            next: (boxes: Box[]) => this.fillFallingArrayBoxes(boxes),
          });
          break;
        case 'ArrowLeft':
          this.lastCallTime = Date.now();
          this.fetchService.moveLeft().subscribe({
            next: (boxes: Box[]) => this.fillFallingArrayBoxes(boxes),
          });
          break;
        case 'ArrowUp':
          this.lastCallTime = Date.now();
          this.fetchService.rotateRight().subscribe({
            next: (boxes: Box[]) => this.fillFallingArrayBoxes(boxes),
          });
          break;
        case 'ArrowDown':
          this.lastCallTime = Date.now();
          this.fetchService.rotateLeft().subscribe({
            next: (boxes: Box[]) => this.fillFallingArrayBoxes(boxes),
          });
          break;
        case ' ':
          this.lastCallTime = Date.now();
          this.fetchService.bottomDown().subscribe({
            next: (boxes: Box[]) => this.fillBoardArrayBoxes(boxes),
          });
          break;
        default:
          break;
      }
    }
  }

  fillFallingArrayBoxes(boxes: Box[]): void {
    if (boxes && boxes.length > 0) {
      const child = this.route.snapshot.firstChild;
      if (child) {
        const initialTimeStamp: number = child.data['figureFalling']
          ?.initialTimeStamp as number;
        const currentTimeStamp: number = child.data['figureFalling']
          ?.timeStamp as number;
        if (
          initialTimeStamp <= boxes[0].initialTimeStamp &&
          currentTimeStamp < boxes[0].timeStamp
        ) {
          child.data['figureFalling'].boxes = boxes;
          child.data['figureFalling'].timeStamp = boxes[0].timeStamp;
        }
      }
    }
  }

  fillBoardArrayBoxes(boxes: Box[]): void {
    if (boxes && boxes.length > 0) {
      const child = this.route.snapshot.firstChild;
      if (child) {
        const currentTimeStamp: number = child.data['board']
          ?.timeStamp as number;
        if (currentTimeStamp < boxes[0].timeStamp) {
          child.data['board'].boxes = boxes;
          child.data['board'].timeStamp = boxes[0].timeStamp;
        }
      }
    }
  }
}
