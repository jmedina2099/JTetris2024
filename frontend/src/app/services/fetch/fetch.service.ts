import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventSourceService } from './event-source.service';
import { environment } from 'src/environments/environment';
import { BoardOperation } from 'src/app/model/board/boardOperation';
import { FigureOperation } from 'src/app/model/figure/figureOperation';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  private fetchUrl = environment.baseUrl + environment.gameUri;

  constructor(
    private sseService: EventSourceService,
    private http: HttpClient
  ) {}

  public isUp(): Observable<boolean> {
    return this.http.get<boolean>(this.fetchUrl + '/isUp');
  }

  public stop(): Observable<boolean> {
    return this.http.post<boolean>(this.fetchUrl + '/stop', null);
  }

  public getFigureConversation(): Observable<FigureOperation> {
    return this.sseService.observeFigure(
      this.fetchUrl + '/getFigureConversation'
    );
  }

  public getBoardConversation(): Observable<BoardOperation> {
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
