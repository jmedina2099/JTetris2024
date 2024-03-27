import { Component } from '@angular/core';
import { AppService } from 'src/app/services/app/app.service';

export interface Greeting {
  id: string;
  username: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  title = 'Demo';
  greeting: Greeting = { id: '', username: '' };

  constructor(private app: AppService) {
    this.app.getResource().subscribe((data: Greeting) => {
      this.greeting = data;
    });
  }

  authenticated() {
    return this.app.authenticated;
  }
}
