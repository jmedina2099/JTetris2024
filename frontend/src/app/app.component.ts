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
    const timeStamp: number[] = [0];
    const elapsedTime = Date.now() - this.lastCallTime;
    const child = this.route.snapshot.firstChild;
    if (elapsedTime > 50) {
      switch (key) {
        case 'ArrowRight':
          this.lastCallTime = Date.now();
          this.fetchService.moveRight().subscribe({
            next: (box: Box) => this.fillFallingBoxes(box, timeStamp),
            complete: () => this.endFillFallingBoxes(timeStamp),
          });
          break;
        case 'ArrowLeft':
          this.lastCallTime = Date.now();
          this.fetchService.moveLeft().subscribe({
            next: (box: Box) => this.fillFallingBoxes(box, timeStamp),
            complete: () => this.endFillFallingBoxes(timeStamp),
          });
          break;
        case 'ArrowUp':
          this.lastCallTime = Date.now();
          this.fetchService.rotateRight().subscribe({
            next: (box: Box) => this.fillFallingBoxes(box, timeStamp),
            complete: () => this.endFillFallingBoxes(timeStamp),
          });
          break;
        case 'ArrowDown':
          this.lastCallTime = Date.now();
          this.fetchService.rotateLeft().subscribe({
            next: (box: Box) => this.fillFallingBoxes(box, timeStamp),
            complete: () => this.endFillFallingBoxes(timeStamp),
          });
          break;
        case ' ':
          if (child) {
            child.data['fallingBoxes'] = [];
          }
          this.lastCallTime = Date.now();
          this.fetchService.bottomDown().subscribe({
            next: (box: Box) => this.fillBoardBoxes(box, timeStamp),
            complete: () => this.endFillBoardBoxes(timeStamp),
          });
          break;
        default:
          break;
      }
    }
  }

  fillFallingBoxes(box: Box, timeStamp: number[]): void {
    const child = this.route.snapshot.firstChild;
    if (child) {
      const initialTimeStamp: number = child.data['initialTimeStamp'] as number;
      const currentTimeStamp: number = child.data['timeStamp'] as number;
      if (
        initialTimeStamp <= box.initialTimeStamp &&
        currentTimeStamp < box.timeStamp
      ) {
        if (timeStamp[0] == 0) {
          child.data['fallingBoxes'] = [];
          timeStamp[0] = box.timeStamp;
        }
        child.data['fallingBoxes'].push(box);
      }
    }
  }

  fillBoardBoxes(box: Box, timeStamp: number[]): void {
    const child = this.route.snapshot.firstChild;
    if (child) {
      const currentTimeStamp: number = child.data['board'].timeStamp as number;
      if (currentTimeStamp < box.timeStamp) {
        if (timeStamp[0] == 0) {
          child.data['board'].boxes = [];
          timeStamp[0] = box.timeStamp;
        }
        child.data['board'].boxes.push(box);
      }
    }
  }

  endFillFallingBoxes(timeStamp: number[]): void {
    const child = this.route.snapshot.firstChild;
    if (child) {
      if (timeStamp[0] != 0) {
        //console.log( '--> endFillFallingBoxes (reactive) = '+timeStamp[0] );
        child.data['timeStamp'] = timeStamp[0];
      }
    }
  }

  endFillBoardBoxes(timeStamp: number[]) {
    const child = this.route.snapshot.firstChild;
    if (child) {
      if (timeStamp[0] != 0) {
        //console.log( '--> endFillBoardBoxes (reactive) = '+timeStamp[0] );
        child.data['board'].timeStamp = timeStamp[0];
      }
    }
    const event = new KeyboardEvent('keydown', { key: 'ArrowRight' });
    this.handleKeyboardEvent(event);
  }
}
