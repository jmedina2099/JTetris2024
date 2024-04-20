import { Injectable } from '@angular/core';
import { Observable, Subscriber, EMPTY } from 'rxjs';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { AppService } from '../app/app.service';
import { BoardOperation } from 'src/app/model/board/boardOperation';
import { FigureOperation } from 'src/app/model/figure/figureOperation';

const EventSource = EventSourcePolyfill;

@Injectable({
  providedIn: 'root',
})
export class EventSourceService {
  constructor(private app: AppService) {}

  observeFigure(url: string): Observable<FigureOperation> {
    //console.log( '---> observeFigure' );
    const auth = sessionStorage.getItem('authorization');
    //console.log('===> auth = ' + auth);
    if (auth === null) {
      return EMPTY;
    }
    return new Observable<FigureOperation>(
      (observer: Subscriber<FigureOperation>) => {
        const eventSource = new EventSource(url, {
          withCredentials: true,
          lastEventIdQueryParameterName: 'Last-Event-Id',
          headers: {
            authorization: auth,
            'Content-Type': 'text/event-stream',
            'Cache-Control': 'no-cache',
            Connection: 'keep-alive',
            'Keep-Alive': 'timeout=3600',
            'X-Accel-Buffering': 'no',
          },
          heartbeatTimeout: 60 * 60 * 1000,
        });
        eventSource.addEventListener('message', messageEvent => {
          //console.log( '---> observeFigure.message ='+messageEvent.data );
          if (messageEvent.data != undefined) {
            observer.next(JSON.parse(messageEvent.data) as FigureOperation);
          }
        });
        eventSource.addEventListener('error', () => {
          //console.log( '---> observeFigure.error!' );
          observer.complete();
        });
        return () => eventSource.close();
      }
    );
  }

  observeBoard(url: string): Observable<BoardOperation> {
    //console.log( '---> observeBoard' );
    const auth = sessionStorage.getItem('authorization');
    //console.log('===> auth = ' + auth);
    if (auth === null) {
      return EMPTY;
    }
    return new Observable<BoardOperation>(
      (observer: Subscriber<BoardOperation>) => {
        const eventSource = new EventSource(url, {
          withCredentials: true,
          lastEventIdQueryParameterName: 'Last-Event-Id',
          headers: {
            authorization: auth,
            'Content-Type': 'text/event-stream',
            'Cache-Control': 'no-cache',
            Connection: 'keep-alive',
            'Keep-Alive': 'timeout=3600',
            'X-Accel-Buffering': 'no',
          },
          heartbeatTimeout: 60 * 60 * 1000,
        });
        eventSource.addEventListener('message', messageEvent => {
          //console.log( '---> observeBoard.message ='+messageEvent.data );
          if (messageEvent.data != undefined) {
            observer.next(JSON.parse(messageEvent.data) as BoardOperation);
          }
        });
        eventSource.addEventListener('error', () => {
          //console.log( '---> observeBoard.error!' );
          observer.complete();
        });
        return () => eventSource.close();
      }
    );
  }
}
