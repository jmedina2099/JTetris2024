import { Component, HostListener, OnInit } from '@angular/core';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppService } from './services/app/app.service';
import { Board } from './model/board/board';

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
          this.fetchService.moveRight().subscribe();
          break;
        case 'ArrowLeft':
          this.lastCallTime = Date.now();
          this.fetchService.moveLeft().subscribe();
          break;
        case 'ArrowUp':
          this.lastCallTime = Date.now();
          this.fetchService.rotateRight().subscribe();
          break;
        case 'ArrowDown':
          this.lastCallTime = Date.now();
          this.fetchService.rotateLeft().subscribe();
          break;
        case ' ':
          this.lastCallTime = Date.now();
          this.fetchService.bottomDown().subscribe({
            next: (board: Board) => this.setBoardOperation(board),
          });
          break;
        default:
          break;
      }
    }
  }

  setBoardOperation(board: Board): void {
    //console.log( '---> setBoardOperation (reactive)...' );
    if (board && board.boxes.length > 0) {
      const child = this.route.snapshot.firstChild;
      if (child) {
        const currentTimeStamp: number = child.data['board']
          ?.timeStamp as number;
        if (currentTimeStamp < board.timeStamp) {
          //console.log( '--> set board (reactive) = '+board.timeStamp );
          child.data['board'] = board;
        }
      }
    }
  }
}
