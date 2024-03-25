import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { AppService } from 'src/app/services/app/app.service';

interface Greeting {
  id: string,
  content: string
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  title = 'Demo';
  greeting : Greeting = { id: '', content: '' };

  constructor(private app: AppService, private http: HttpClient) {
    http.get<Greeting>('resource').subscribe( (data : Greeting) => {
      console.log( '===> resource = '+JSON.stringify(data) )
      this.greeting = data;
    });
  }

  authenticated() { return this.app.authenticated; }

}
