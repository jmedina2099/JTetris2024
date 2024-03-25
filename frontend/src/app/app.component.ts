import { Component, HostListener, ViewChild } from '@angular/core';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { VentanaPrincipalComponent } from './components/ventana-principal/ventana-principal.component';
import { Box } from './model/figure/box';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { AppService } from './services/app/app.service';
import { Credentials } from 'src/app/components/login/login.component';
import { finalize } from 'rxjs/operators'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  constructor(private app: AppService, private http: HttpClient, private router: Router, private fetchService: FetchService, private route: ActivatedRoute) {
  }

  logout() {
    this.http.post('logout', {}).pipe( finalize(() => {
        this.app.authenticated = false;
        this.router.navigateByUrl('/login');
    })).subscribe();
  }

  authenticated() { return this.app.authenticated; }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    const key = event.key;
    const firstBoxRight: boolean[] = [false];
    const firstBoxLeft: boolean[] = [false];
    const firstBoxRotateRight: boolean[] = [false];
    const firstBoxRotateLeft: boolean[] = [false];
    const firstBoxBottomDown: boolean[] = [false];
    switch (key) {
      case 'ArrowRight':
        this.fetchService.moveRight(this.app.credentials).subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxRight)
        });
        break;
      case 'ArrowLeft':
        this.fetchService.moveLeft(this.app.credentials).subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxLeft),
        });
        break;
      case 'ArrowUp':
        this.fetchService.rotateRight(this.app.credentials).subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxRotateRight),
        });
        break;
      case 'ArrowDown':
        this.fetchService.rotateLeft(this.app.credentials).subscribe({
          next: (box: Box) => this.fillFallingBoxes(box, firstBoxRotateLeft),
        });
        break;
      case ' ':
        let child = this.route.snapshot.firstChild;
        if( child ) {
          child.data['fallingBoxes'] = [];
        }
        this.fetchService.bottomDown(this.app.credentials).subscribe();
        break;
      default:
        break;
    }
  }

  fillFallingBoxes(box: Box, firstBox: boolean[]): void {
    let child = this.route.snapshot.firstChild;
    if( child ) {
      if (!firstBox[0]) {
        child.data['fallingBoxes'] = [];
        firstBox[0] = true;
      }
      child.data['fallingBoxes'].push(box);
    }
  }
}
