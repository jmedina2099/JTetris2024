import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventSourceService } from './event-source.service';
import { environment } from 'src/environments/environment';
import { Figure } from 'src/app/model/figure/figure';
import { Board } from 'src/app/model/board/board';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  private fetchUrl = environment.baseUrl + environment.gameUri;

  constructor(
    private sseService: EventSourceService,
    private http: HttpClient
  ) {}

  start(): Observable<Figure> {
    return this.sseService.observeFigure(this.fetchUrl + '/start');
  }

  moveRight(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/moveRight', null);
  }

  moveLeft(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/moveLeft', null);
  }

  rotateRight(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/rotateRight', null);
  }

  rotateLeft(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/rotateLeft', null);
  }

  bottomDown(): Observable<Board> {
    return this.http.post<Board>(this.fetchUrl + '/bottomDown', null);
  }
}
