import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Box } from 'src/app/model/figure/box';
import { EventSourceService } from './event-source.service';
import { environment } from 'src/environments/environment';
import { Figure } from 'src/app/model/figure/figure';

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

  moveRight(): Observable<Box[]> {
    return this.http.post<Box[]>(this.fetchUrl + '/moveRight', null);
  }

  moveLeft(): Observable<Box[]> {
    return this.http.post<Box[]>(this.fetchUrl + '/moveLeft', null);
  }

  rotateRight(): Observable<Box[]> {
    return this.http.post<Box[]>(this.fetchUrl + '/rotateRight', null);
  }

  rotateLeft(): Observable<Box[]> {
    return this.http.post<Box[]>(this.fetchUrl + '/rotateLeft', null);
  }

  bottomDown(): Observable<Box[]> {
    return this.http.post<Box[]>(this.fetchUrl + '/bottomDown', null);
  }
}
