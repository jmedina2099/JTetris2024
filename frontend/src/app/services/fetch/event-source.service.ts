import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Box } from 'src/app/model/figure/box';

@Injectable({
  providedIn: 'root'
})
export class EventSourceService {

  constructor() { }

  observeMessages(url: string): Observable<Box> {
    return new Observable<Box>(obs => {
      const es = new EventSource(url);
      es.addEventListener('message', (evt: MessageEvent) => {
        obs.next(evt.data != null ? JSON.parse(evt.data): '');
      });
      return () => es.close();
    });
  }
}
