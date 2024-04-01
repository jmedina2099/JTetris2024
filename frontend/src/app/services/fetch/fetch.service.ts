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

  public start(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/start', null);
  }

  public getFigureConversation(): Observable<Figure> {
    return this.sseService.observeFigure(
      this.fetchUrl + '/getFigureConversation'
    );
  }

  public getBoardConversation(): Observable<Board> {
    return this.sseService.observeBoard(
      this.fetchUrl + '/getBoardConversation'
    );
  }

  public moveRight(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/moveRight', null);
  }

  public moveLeft(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/moveLeft', null);
  }

  public rotateRight(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/rotateRight', null);
  }

  public rotateLeft(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/rotateLeft', null);
  }

  public bottomDown(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/bottomDown', null);
  }
}
