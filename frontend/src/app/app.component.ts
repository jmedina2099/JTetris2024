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
    const child = this.route.snapshot.firstChild;
    if (child) {
      const initialTimeStamp: number = child.data['initialTimeStamp'] as number;
      const currentTimeStamp: number = child.data['timeStamp'] as number;
      if (
        initialTimeStamp <= box.initialTimeStamp &&
        currentTimeStamp <= box.timeStamp
      ) {
        if (!firstBox[0]) {
          child.data['fallingBoxes'] = [];
          child.data['timeStamp'] = box.timeStamp;
          firstBox[0] = true;
        }
        child.data['fallingBoxes'].push(box);
      }
    }
  }
}
