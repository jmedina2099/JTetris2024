import { Component, HostListener, OnInit } from '@angular/core';
import { FetchService } from 'src/app/services/fetch/fetch.service';
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

  protected logout() {
    this.app.logout();
  }

  protected authenticated() {
    return this.app.authenticated;
  }

  private sleep(num: number): void {
    let now = new Date();
    const stop = now.getTime() + num;
    const running = true;
    while (running) {
      now = new Date();
      if (now.getTime() > stop) return;
    }
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadApp(): void {
    this.fetchService.stop().subscribe();
    this.sleep(1000);
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    const key = event.key;
    const elapsedTime = Date.now() - this.lastCallTime;
    const child = this.route.snapshot.firstChild;
    const isRunning = child?.data['game']?.isRunning;
    const waiting = child?.data['waitForBottomDown']?.waiting;
    if (isRunning && elapsedTime > 50 && !waiting) {
      switch (key) {
        case 'ArrowRight':
          this.lastCallTime = Date.now();
          this.fetchService.moveRight().subscribe({
            //next: (value: Boolean) => console.log('--> moveRight.next = '+value),
            //error: err => console.log('--> moveRight.error = '+err),
            //complete: () => console.log('--> moveRight.complete!'),
          });
          break;
        case 'ArrowLeft':
          this.lastCallTime = Date.now();
          this.fetchService.moveLeft().subscribe({
            //next: (value: Boolean) => console.log('--> moveLeft = '+value),
            //error: err => console.log('--> moveLeft.error = '+err),
            //complete: () => console.log('--> moveLeft.complete!'),
          });
          break;
        case 'ArrowUp':
          this.lastCallTime = Date.now();
          this.fetchService.rotateRight().subscribe({
            //next: (value: Boolean) => console.log('--> rotateRight = '+value),
            //error: err => console.log('--> rotateRight.error = '+err),
            //complete: () => console.log('--> rotateRight.complete!'),
          });
          break;
        case 'ArrowDown':
          this.lastCallTime = Date.now();
          this.fetchService.rotateLeft().subscribe({
            //next: (value: Boolean) => console.log('--> rotateLeft = '+value),
            //error: err => console.log('--> rotateLeft.error = '+err),
            //complete: () => console.log('--> rotateLeft.complete!'),
          });
          break;
        case ' ':
          this.lastCallTime = Date.now();
          if (child) {
            child.data['waitForBottomDown'].waiting = true;
          }
          this.fetchService.bottomDown().subscribe({
            //next: (value: Boolean) => console.log('--> bottomDown = '+value),
            //error: err => console.log('--> bottomDown.error = '+err),
            //complete: () => console.log('--> bottomDown.complete!'),
          });
          break;
        default:
          break;
      }
    }
  }
}
